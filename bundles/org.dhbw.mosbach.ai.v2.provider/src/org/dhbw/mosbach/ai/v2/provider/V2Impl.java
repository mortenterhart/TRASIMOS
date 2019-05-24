package org.dhbw.mosbach.ai.v2.provider;

import org.dhbw.mosbach.ai.base.Radio.Configuration;
import org.dhbw.mosbach.ai.base.*;
import org.dhbw.mosbach.ai.base.V2Info;
import org.dhbw.mosbach.ai.v2.api.IV2;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;

//import org.dhbw.mosbach.ai.base.Position;


@WebService(endpointInterface = "org.dhbw.mosbach.ai.v2.api.IV2")
@Component(name = "v2", service = IV2.class)
public class V2Impl implements IV2,Runnable {

    private long id;
    private Position origin;
    private Position destination;
    private Position currentPosition;
    private Vector<Long> direction;
    private double velocity;
    private ArrayList<Position> routePositions;
    private int nextRoutePositionIndex;
    private String SOAPURL;

    private int TIMEOUT = 1;

    public V2Impl(long id,double originLongitude, double originLatitude, double destinationLongitude, double destinationLatitude) throws UnknownHostException {
        this.id=id;
        velocity=0.0;
        origin = new Position(originLongitude, originLatitude);
        destination = new Position(destinationLongitude, destinationLatitude);
        currentPosition = origin;
        routePositions = getRoutePositions();
        nextRoutePositionIndex = 0;

        //Start SOAP
        //generate random port
        int port = (int)(Math.random()*(Configuration.V2PortMax-Configuration.V2PortMin))+Configuration.V2PortMin;
        //build URL
        String localIp = Inet4Address.getLocalHost().getHostAddress();
        SOAPURL= Configuration.general_https+localIp+":"+port+"/"+id+"/"+Configuration.V2SOAP;
        String SOAPPublish = Configuration.general_https+"0.0.0.0:"+port+"/"+id+"/"+Configuration.V2SOAP;
        //Start Service
        Object implementor = this;
        Endpoint.publish(SOAPPublish, implementor);
        System.out.println("OPENING V2-SOAP on "+SOAPURL);

    }

    @Activate
    public void activate(ComponentContext context, BundleContext bundleContext, Map<String, ?> properties) {
        System.out.println("V2 booting ...");
        // RadioClient -> NameServer -> InformationServer
    }

    @Deactivate
    public void deactivate() {
        System.out.println("V2 shutting down ...");
    }

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
    @Override
    public V2Info getV2Information() {
        V2Info v2Info = new V2Info();
        v2Info.position = currentPosition;
        v2Info.speed = velocity;
        v2Info.SOAPURL= SOAPURL;
        v2Info.V2id =id;
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
                    publishPosition();
                    diceBraking();
                    drive(
                            currentPosition.getLatitude(), currentPosition.getLongitude(),
                            routePositions.get(nextRoutePositionIndex).getLatitude(), currentPosition.getLongitude()
                    );
                }
                nextRoutePositionIndex++;
            } catch (InterruptedException exc) {
                exc.printStackTrace();
            }
        }
    }


    public void publishPosition() {

    }

    public List<Position> retrieveNeighbours() {
        return new ArrayList<>();
    }

    public void diceBraking() {
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
            currentPosition.latitude=x2;
            currentPosition.longitude=y2;
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

        double latitude2 = Math.asin(Math.sin(latitude) * Math.cos(d/R) + Math.cos(latitude) * Math.sin(d/R) * Math.cos(bearing));
        double longitude2 = longitude + Math.atan2(Math.sin(bearing) * Math.sin(d/R) * Math.cos(latitude), Math.cos(d/R) - Math.sin(latitude) * Math.sin(latitude2));

        return new Position(longitude2, latitude2);
    }




}
