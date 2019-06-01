package org.dhbw.mosbach.ai.v2.provider;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.dhbw.mosbach.ai.base.Position;
import org.dhbw.mosbach.ai.base.V2Info;
import org.dhbw.mosbach.ai.base.radio.BroadcastConsumer;
import org.dhbw.mosbach.ai.base.radio.Configuration;
import org.dhbw.mosbach.ai.information_system.api.InformationSOAP;
import org.dhbw.mosbach.ai.name_server.api.NameServerSOAP;
import org.dhbw.mosbach.ai.v2.api.IV2;
import org.dhbw.mosbach.ai.v2.api.V2SOAP;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebService(endpointInterface = "org.dhbw.mosbach.ai.v2.api.IV2")
@Component(name = "v2", service = IV2.class)
public class V2Impl implements IV2, Runnable {

    private static class Route {
        private ArrayList<Position> getRouteDummy;
    }

    private static volatile long Generatingid;
    private long id;
    private Position origin;
    private Position destination;
    private volatile Position currentPosition;
    private Position direction;
    private volatile ArrayList<Position> routePositions;
    private volatile AtomicDouble velocity;
    private String webURL;
    private int nextRoutePositionIndex;
    private String SOAPURL;
    private volatile String infoWsdl;
    private int TIMEOUT = 1;
    private double maxVelocity;
    private Route routeList;

    private InformationSOAP informationSOAP;

    private BroadcastConsumer nameListener;
    private BroadcastConsumer webListener;

    public V2Impl() {

    }

    public static synchronized long getID() {
        return Generatingid++;
    }

    public V2Impl(long id, double originLongitude, double originLatitude, double destinationLongitude, double destinationLatitude, double maxVelocity) throws UnknownHostException {
        this.id = getID();
        velocity.set(0.0);
        direction = new Position(0, 0);
        this.maxVelocity = maxVelocity;
        origin = new Position(originLongitude, originLatitude);
        destination = new Position(destinationLongitude, destinationLatitude);
    }

    @Activate
    public void activate(ComponentContext context, BundleContext bundleContext, Map<String, ?> properties) throws MalformedURLException {
        System.out.println("V2 booting ...");
    }

    @Deactivate
    public void deactivate() {
        System.out.println("V2 shutting down ...");
    }

    private ArrayList<Position> getRouteDummy() {
        ArrayList<Position> positions = new ArrayList<>();

        positions.add(new Position(49.303717, 9.002668));
        positions.add(new Position(49.308762, 9.001426));
        positions.add(new Position(49.320000, 9.084893));

        return positions;
    }

    private ArrayList<Position> getRoutePositions() {

        try {
            JsonParser parser = new JsonParser();

            String json = null;
            json = readUrl();
            JsonElement jsonObject = parser.parse(json);
            JsonObject details = jsonObject.getAsJsonObject();

            JsonArray route = details.get("routes").getAsJsonArray();
            JsonObject geometry = route.get(0).getAsJsonObject().get("geometry").getAsJsonObject();
            JsonArray coordinates = geometry.get("coordinates").getAsJsonArray();


            ArrayList<Position> positions = new ArrayList<>();

            for (int i = 0; i < coordinates.size(); i++) {

                JsonArray coord = coordinates.get(i).getAsJsonArray();
                positions.add(new Position(Double.parseDouble(coord.get(0).getAsString()), Double.parseDouble(coord.get(1).getAsString())));
            }

            return positions;

        } catch (Exception exp) {
            System.out.println("ROUTE SERVICE DOWN --");
            return getRouteDummy();
        }
    }

    private String readUrl() throws IOException {
        BufferedReader reader = null;
        String urlString = "http://router.project-osrm.org/route/v1/driving/"
                + origin.longitude + "," + origin.latitude + ";" + destination.longitude + "," + destination.latitude +
                "?steps=true&alternatives=true&geometries=geojson";
        System.out.println(urlString);
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    @WebMethod
    public Position getDestination() {
        return destination;
    }

    @WebMethod
    public Position getOrigin() {
        return origin;
    }

    @WebMethod
    public void reduceSpeed() {
        if (velocity.get() >= 10.0) {
            velocity.addAndGet(-5.0);
        }
    }

    @WebMethod
    @Override
    public V2Info getV2Information() {
        V2Info v2Info = new V2Info();
        v2Info.position = currentPosition;
        v2Info.speed = velocity.get();
        v2Info.SOAPURL = SOAPURL;
        v2Info.V2id = id;
        v2Info.direction = direction;
        return v2Info;
    }

    @WebMethod
    public Position getCurrentPosition() {
        return currentPosition;
    }

    @WebMethod
    public long getId() {
        return id;
    }

    public void calculateDirection(Position from, Position to) {

        Position delta = new Position();
        delta.latitude = to.latitude - from.latitude;
        delta.longitude = to.longitude - from.longitude;
        direction.latitude = delta.latitude;
        direction.longitude = delta.longitude;
    }

    public void startup() throws UnknownHostException {
        routePositions = getRoutePositions();

        origin = routePositions.get(0);
        destination = routePositions.get(routePositions.size() - 1);
        currentPosition = new Position(origin.longitude, origin.latitude);

        nextRoutePositionIndex = 0;

        //2. Listen to Nameserver
        nameListener = new BroadcastConsumer(Configuration.NameService_multiCastAddress, Configuration.NameService_multiCastPort);
        Thread nameListenerThread = new Thread(nameListener);
        nameListenerThread.start();

        //3. Listen to Webserver
        webListener = new BroadcastConsumer(Configuration.Webserver_multiCastAddress, Configuration.Webserver_multiCastPort);
        Thread webListenerThread = new Thread(webListener);
        webListenerThread.start();


        //Start SOAP
        //generate random port
        int port = (int) (Math.random() * (Configuration.V2PortMax - Configuration.V2PortMin)) + Configuration.V2PortMin;
        //build URL

        String localIp = Inet4Address.getLocalHost().getHostAddress();

        SOAPURL = Configuration.general_https + localIp + ":" + port + "/" + id + "/" + Configuration.V2SOAP;

        String SOAPPublish = Configuration.general_https + "0.0.0.0:" + port + "/" + id + "/" + Configuration.V2SOAP;


        //Start Service
        Object implementor = this;
        Endpoint.publish(SOAPPublish, implementor);
        System.out.println("OPENING V2-SOAP on " + SOAPURL);


        while (!nameListener.isServiceFound()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
/*
        while (webListener.isServiceFound()==false){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        */

        try {

            /*
            String webserverURL = webListener.getServiceURLs().get(0);
            webserverSOAP = new WebserverSOAP(webserverURL);
*/
            String nameServerURL = nameListener.getServiceURLs().get(0);
            nameServerSOAP = new NameServerSOAP(nameServerURL);
            infoWsdl = getInformationService(nameServerURL, currentPosition);
            if (infoWsdl.equals("OutOfMap")) {
                currentPosition = destination;
            } else {
                informationSOAP = new InformationSOAP(infoWsdl);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        System.out.println("InformationServer on " + infoWsdl);

    }

    /*  1) Position publishen
        2) Neighbours anfragen
        3) Collision Detection: Geschwindigkeit anpassen
        4) Drive
    * */
    public void run() {

        try {
            startup();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            currentPosition = destination;
        }

        while (currentPosition.longitude != destination.longitude && currentPosition.latitude != destination.latitude) {
            try {
                while ((routePositions.size() - 1) > nextRoutePositionIndex && currentPosition.getLongitude() != this.routePositions.get(nextRoutePositionIndex).getLongitude() && currentPosition.getLatitude() != this.routePositions.get(nextRoutePositionIndex).getLatitude()) {
                    Thread.sleep(TIMEOUT);

                    //diceBraking();
                    properBraking();
                    accelerate();
                    calculateDirection(currentPosition, this.routePositions.get(nextRoutePositionIndex));
                    currentPosition = drive(
                            currentPosition.getLatitude(), currentPosition.getLongitude(),
                            this.routePositions.get(nextRoutePositionIndex).getLatitude(), this.routePositions.get(nextRoutePositionIndex).getLongitude()
                    );


                    publishPosition();
                    if (currentPosition.latitude == currentPosition.latitude && currentPosition.longitude == destination.longitude) {
                        break;
                    }
                    System.out.println("id: " + id + " Pos: " + currentPosition.latitude + "|" + currentPosition.longitude);
                }
                if (nextRoutePositionIndex < routePositions.size()) {
                    nextRoutePositionIndex++;
                } else {
                    break;
                }
            } catch (InterruptedException | MalformedURLException exc) {
                exc.printStackTrace();
            }

        }

        for (int i = 0; i < 100; i++) {
            System.out.println("FINISHED " + id);
        }

        try {
            publishFinished();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void accelerate() {
        if (velocity.get() <= maxVelocity) {
            velocity.addAndGet(1.0);
        }
    }

    private void properBraking() throws MalformedURLException {
        ArrayList<V2Info> v2InfoArrayList = getNeighboursFromInfo(infoWsdl);
        for (V2Info v2Info :
                v2InfoArrayList) {
            if (1000 < distance(
                    v2Info.position.latitude,
                    v2Info.position.longitude,
                    currentPosition.latitude,
                    currentPosition.longitude)
            ) {
                V2SOAP v2SOAP = new V2SOAP(v2Info.SOAPURL);
                v2SOAP.reduceSpeed();
            }
        }
    }

    public List<Position> retrieveNeighbours() {
        return new ArrayList<>();
    }

    private Position drive(double x1, double y1, double x2, double y2) {
        System.out.println("Driving");
        double distance = distance(x1, y1, x2, y2);
        double distanceV2CanDrive = 100 / 3.6;
        Position resultingVector = new Position();
        resultingVector.latitude = x2 - x1;
        resultingVector.longitude = y2 - y1;

        if (distance - distanceV2CanDrive > 0) {

            double factor = 1 - (distance - distanceV2CanDrive) / (distance); //Prozentual way made to next checkpoint

            Position newPosition = new Position();
            newPosition.latitude = x1 + resultingVector.latitude * factor;
            newPosition.longitude = y1 + resultingVector.longitude * factor;

            return newPosition;

        } else {
            return new Position(y2, x2);
        }

    }


    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        distance = Math.pow(distance, 2);
        return Math.sqrt(distance);
    }

    //GET URLS OF ALL NAMESERVICES; WILL ONLY BE ONE BUT FOR FUTURE !!
    public ArrayList<String> getNameServerUrl() {

        if (nameListener.isServiceFound()) {
            return nameListener.getServiceURLs();
        }
        return new ArrayList<>();
    }

    //GET URLS OF ALL NAMESERVICES; WILL ONLY BE ONE BUT FOR FUTURE !!
    public ArrayList<String> getWebserverUrl() {

        if (webListener.isServiceFound()) {
            return webListener.getServiceURLs();
        }
        return new ArrayList<>();
    }


    private NameServerSOAP nameServerSOAP;

    //GET URL TO INFOSERVIE @PARAM url of nameservice and current position
    private String getInformationService(String wsdl, Position position) throws MalformedURLException {
        return nameServerSOAP.getInfoServer(position);
    }


    //Publish position to @Param url to Inforservice return false => get new Infoservice
    private boolean publishPositionToInfoserver(String wsdl) throws MalformedURLException {
        V2Info v2Info = getV2Information();
        // webserverSOAP.receivePosition(v2Info);
        return informationSOAP.receivePosition(v2Info);
    }

    private ArrayList<V2Info> getNeighboursFromInfo(String wsdl) throws MalformedURLException {
        return informationSOAP.getNeighbours(getV2Information());
    }

    private void publishPosition() throws MalformedURLException {
        if (!publishPositionToInfoserver(infoWsdl)) {
            infoWsdl = getInformationService(nameListener.getServiceURLs().get(0), currentPosition);
            if (infoWsdl.equals("OutOfMap")) {
                currentPosition = destination;
            } else {
                informationSOAP = new InformationSOAP(infoWsdl);
                publishPositionToInfoserver(infoWsdl);
            }
        }
    }

    private void publishFinished() throws MalformedURLException {
        if (!infoWsdl.equals("OutOfMap")) {
            informationSOAP.receiveFinished(getV2Information());
        }
    }

}
