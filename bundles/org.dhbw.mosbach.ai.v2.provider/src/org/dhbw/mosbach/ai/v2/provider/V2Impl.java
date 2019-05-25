package org.dhbw.mosbach.ai.v2.provider;

import org.dhbw.mosbach.ai.base.Position;
import org.dhbw.mosbach.ai.base.Radio.BroadcastConsumer;
import org.dhbw.mosbach.ai.base.Radio.Configuration;
import org.dhbw.mosbach.ai.base.V2Info;
import org.dhbw.mosbach.ai.information_system.api.InformationSOAP;
import org.dhbw.mosbach.ai.name_server.api.NameServerSOAP;
import org.dhbw.mosbach.ai.v2.api.IV2;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;

//import org.dhbw.mosbach.ai.base.Position;


@WebService(endpointInterface = "org.dhbw.mosbach.ai.v2.api.IV2")
@Component(name = "v2", service = IV2.class)
public class V2Impl implements IV2, Runnable {

    private long id;
    private Position origin;
    private Position destination;
    private Position currentPosition;
    private Position direction;
    private double velocity;
    private ArrayList<Position> routePositions;
    private int nextRoutePositionIndex;
    private String SOAPURL;
    private String infoWsdl;
    private int TIMEOUT = 1;
    private double maxVelocity;

    public V2Impl() {

    }

    BroadcastConsumer nameListener;
    BroadcastConsumer webListener;


    public V2Impl(long id, double originLongitude, double originLatitude, double destinationLongitude, double destinationLatitude, double maxVelocity) throws UnknownHostException {
        this.id = id;
        velocity = 0.0;
        this.maxVelocity = maxVelocity;
        origin = new Position(originLongitude, originLatitude);
        destination = new Position(destinationLongitude, destinationLatitude);
        currentPosition = origin;
        routePositions = getRoutePositions();
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

    }

    @Activate
    public void activate(ComponentContext context, BundleContext bundleContext, Map<String, ?> properties, String wsdl) throws MalformedURLException {
        System.out.println("V2 booting ...");
        // RadioClient -> NameServer -> InformationServer
        ArrayList<String> nameServer = getNameServerUrl();
        infoWsdl = getInformationService(wsdl, currentPosition);
    }

    @Deactivate
    public void deactivate() {
        System.out.println("V2 shutting down ...");
    }


    //USE API
    public ArrayList<Position> getRoutePositions() {
        Position a = new Position(49.303717, 9.002668);
        Position b = new Position(49.30481, 8.999361);
        Position c = new Position(49.313765, 8.998264);
        ArrayList<Position> abc = new ArrayList<>();
        abc.add(a);
        abc.add(b);
        abc.add(c);

        return abc;
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
        if(velocity >= 10.0){
           velocity-= 5.0;
        }
    }

    @WebMethod
    @Override
    public V2Info getV2Information() {
        V2Info v2Info = new V2Info();
        v2Info.position = currentPosition;
        v2Info.speed = velocity;
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

    /*  1) Position publishen
        2) Neighbours anfragen
        3) Collision Detection: Geschwindigkeit anpassen
        4) Drive
    * */
    public void run() {
        while (currentPosition != destination) {
            try {
                while (currentPosition.getLongitude() != routePositions.get(nextRoutePositionIndex).getLongitude()) {
                    Thread.sleep(TIMEOUT);
                    publishPosition();
                    diceBraking();
                    drive(
                            currentPosition.getLatitude(), currentPosition.getLongitude(),
                            currentPosition.getLatitude(), routePositions.get(nextRoutePositionIndex).getLongitude()
                    );
                }
                while (currentPosition.getLatitude() != routePositions.get(nextRoutePositionIndex).getLatitude()) {
                    Thread.sleep(TIMEOUT);
                    diceBraking();
                    //properBraking();
                    accelerate();
                    drive(
                            currentPosition.getLatitude(), currentPosition.getLongitude(),
                            routePositions.get(nextRoutePositionIndex).getLatitude(), currentPosition.getLongitude()
                    );
                }
                publishPosition();
                nextRoutePositionIndex++;
            } catch (InterruptedException exc) {
                exc.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    private void accelerate() {
        if (velocity <= maxVelocity) {
            velocity++;
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
                //Connection to other V2 (Questanable if this even work @captainblubb)
                URL v2Url = new URL(v2Info.SOAPURL);
                QName qName = new QName("TODO add namespace", String.valueOf(v2Info.V2id));
                Service service = Service.create(v2Url, qName);
                IV2 otherV2Service = (IV2) service.getPorts(IV2.class); // inspections says getPorts() can´t be applied
                otherV2Service.reduceSpeed();
            }
        }
    }

    public List<Position> retrieveNeighbours() {
        return new ArrayList<>();
    }

    private void diceBraking() {
        if (!retrieveNeighbours().isEmpty()) {
            Random r = new Random();
            try {
                Thread.sleep(r.nextInt(101) * 1000);
            } catch (InterruptedException exc) {
                exc.printStackTrace();
            }
        }
    }

    // happens in one second
    public void drive(double x1, double y1, double x2, double y2) {
        System.out.println("Driving");
        double distance = distance(x1, y1, x2, y2);
        double distanceV2CanDrive = velocity / 3.6;
        if (distance <= distanceV2CanDrive) {
            currentPosition.latitude = x2;
            currentPosition.longitude = y2;
        } else {
            if (x1 == x2) {
                if (y1 < y2) {
                    currentPosition = positionPlusDistance(x1, y2, distance, 90);
                } else {
                    currentPosition = positionPlusDistance(x1, y2, distance, 270);
                }
            } else {
                if (x1 < x2) {
                    currentPosition = positionPlusDistance(x1, y2, distance, 180);
                } else {
                    currentPosition = positionPlusDistance(x1, y2, distance, 0);
                }
            }

        }
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {

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

    public static Position positionPlusDistance(double latitude, double longitude, double distance, double bearing) {
        double R = 6378.1; //Radius of the Earth
        bearing = Math.toRadians(bearing); //Bearing is 90 degrees converted to radians.
        double d = distance * 3.6; //Distance in km

        latitude = Math.toRadians(latitude); //Current lat point converted to radians
        longitude = Math.toRadians(longitude); //Current long point converted to radians

        double latitude2 = Math.asin(Math.sin(latitude) * Math.cos(d / R) + Math.cos(latitude) * Math.sin(d / R) * Math.cos(bearing));
        double longitude2 = longitude + Math.atan2(Math.sin(bearing) * Math.sin(d / R) * Math.cos(latitude), Math.cos(d / R) - Math.sin(latitude) * Math.sin(latitude2));

        return new Position(longitude2, latitude2);
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

    public boolean publishPositionToWebserver(String wsdl) {
        // TODO: cant implement yet
        return true;
    }

    //GET URL TO INFOSERVIE @PARAM url of nameservice and current position
    public String getInformationService(String wsdl, Position position) throws MalformedURLException {
        NameServerSOAP nameServerSOAP = new NameServerSOAP(wsdl);
        return nameServerSOAP.getInfoServer(position);
    }

    //Publish position to @Param url to Inforservice return false => get new Infoservice
    public boolean publishPositionToInfoserver(String wsdl) throws MalformedURLException {
        InformationSOAP informationSOAP = new InformationSOAP(wsdl);
        return informationSOAP.receivePosition(getV2Information());
    }

    public ArrayList<V2Info> getNeighboursFromInfo(String wsdl) throws MalformedURLException {
        InformationSOAP informationSOAP = new InformationSOAP(wsdl);
        return informationSOAP.getNeighbours(getV2Information());
    }

    private void publishPosition() throws MalformedURLException {
        if (!publishPositionToInfoserver(infoWsdl)) {
            getInformationService(infoWsdl, currentPosition);
        }
    }
}
