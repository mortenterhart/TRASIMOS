package org.dhbw.mosbach.ai.information_system.provider;

import org.dhbw.mosbach.ai.base.MapChunk;
import org.dhbw.mosbach.ai.base.Position;
import org.dhbw.mosbach.ai.information_system.api.IInformationSystem;
import org.dhbw.mosbach.ai.information_system.api.IPublishPosition;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@WebService(endpointInterface = "org.dhbw.mosbach.ai.information_system.api.IInformationSystem")
@Component(name = "information-system", service = {IInformationSystem.class, IPublishPosition.class})
public class InformationSystemImpl implements IPublishPosition, IInformationSystem {
    private HashMap<Long, Position> vehiclesToObserve;
    private MapChunk areaBoundaries;

    @Activate
    public void activate(ComponentContext context, BundleContext bundleContext, Map<String, ?> properties) {
        System.out.println("Information system booting ...");
        System.out.println("Try to register at name server ...");
        vehiclesToObserve = new HashMap<>();
    }

    @Deactivate
    public void deactivate() {
        System.out.println("Information system shutting down ...");
    }


    @Override
    @WebMethod
    public boolean receivePosition(long v2Id, Position position) {

        System.out.println("SERVER: I RECEIVED A RECEIVE POSITION REQUEST");
        System.out.println("SERVER: CAR ID "+v2Id);
        System.out.println("SERVER: Position "+position.latitude+"|"+position.longitude);
        // Add/Update vehicle in Map
        // if vehicle id already exists the position of vehicle will be overridden
        if (isVehicleInBoundary(position)) {
            vehiclesToObserve.put(v2Id, position);
            return true;
        }
        return false;
    }

    /**
     * get neighbours of vehicle by its id and speed.
     * requres that vehicle is observed by this information system
     *
     * @param v2Id  id of v2 vehicle
     * @param speed speed of v2 vehicle
     * @return list of positions of neighbours
     */
    @Override
    @WebMethod
    public ArrayList<Position> getNeighbours(long v2Id, double speed) {
        // If vehicle is new, neighbours can not be resolved
        // Vehicle needs to publish its position first
        if (isVehicleKnown(v2Id)) {
            ArrayList<Position> positionOfNeighbours = new ArrayList<>();
            // Check if vehicle is too close at boundary. If confirmed getNeighbours from other information systems

            Position positionOfVehicle = vehiclesToObserve.get(v2Id);

            if (isVehicleNearBoundary(positionOfVehicle, speed)) {
                // Vehicle to close at boundary
                // TODO: Ask other servers
            }
            // Add neighbours in boundary
            for (Position pos : vehiclesToObserve.values()) {
                if (distanceBetweenPositions(positionOfVehicle, pos) <= calcStoppingDistance(speed)) {
                    positionOfNeighbours.add(pos);
                }
            }
            return positionOfNeighbours;
        }
        return null;
    }

    /**
     * get neighbours of position in a specific radius
     *
     * @param position centre position
     * @param radius   radius around position
     * @return list of positions of neighbours
     */
    @Override
    @WebMethod
    public ArrayList<Position> getNeighbours(Position position, double radius) {
        ArrayList<Position> positionOfNeighbours = new ArrayList<>();

        for (Position pos : vehiclesToObserve.values()) {
            if (distanceBetweenPositions(position, pos) <= radius) {
                positionOfNeighbours.add(pos);
            }
        }

        return positionOfNeighbours;
    }

    /**
     * remove vehicle from observing
     *
     * @param v2Id id of v2 vehicle
     */
    @Override
    @WebMethod
    public void receiveFinished(long v2Id) {
        vehiclesToObserve.remove(v2Id);
    }


    @WebMethod
    @Override
    public void publish(Position position) {

    }

    private double distanceBetweenPositions(Position a, Position b) {
        return Math.sqrt(
                (b.latitude - a.latitude) * (b.latitude - a.latitude) +
                        (b.longitude - a.longitude) * (b.longitude - a.longitude));
    }

    private boolean isVehicleKnown(long vehicleId) {
        return vehiclesToObserve.containsKey(vehicleId);
    }

    private boolean isVehicleInBoundary(Position position) {
        return position.longitude > areaBoundaries.getBottomLeft().longitude &&
                position.longitude < areaBoundaries.getBottomRight().longitude &&
                position.latitude < areaBoundaries.getTopLeft().latitude &&
                position.latitude > areaBoundaries.getBottomLeft().latitude;
    }

    private boolean isVehicleNearBoundary(Position position, double speed) {
        double stoppingDistance = calcStoppingDistance(speed);
        return position.latitude > (areaBoundaries.getBottomLeft().longitude + stoppingDistance) &&
                position.latitude < (areaBoundaries.getBottomRight().longitude - stoppingDistance) &&
                position.latitude < (areaBoundaries.getTopLeft().longitude - stoppingDistance) &&
                position.latitude > (areaBoundaries.getBottomLeft().longitude + stoppingDistance);
    }

    private double calcStoppingDistance(double speed) {
        return (speed / 10 * 3) + (speed / 10 * speed / 10);
    }

    public MapChunk getAreaBoundaries() {
        return areaBoundaries;
    }

    public void setAreaBoundaries(MapChunk areaBoundaries) {
        this.areaBoundaries = areaBoundaries;
    }



    /*
        Fabi test zum starten von Zervice
    */

    public static void main(String args[]){

        //START SERVICES
        IInformationSystem impl = new InformationSystemImpl();
        Object implementor = impl;
        String address = "http://localhost:9001/extremeCoolSoapApi";
        Endpoint.publish(address,implementor);

        //WAIT FOR BETTER WETTER
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //CREATE KLIENTÉL
        URL wsdlUrl = null;
        try {
            wsdlUrl = new URL(address);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        QName qname = new QName("http://provider.information_system.ai.mosbach.dhbw.org/", "InformationSystemImplService");
        Service service = Service.create(wsdlUrl, qname);
        IInformationSystem iInformationSystem = service.getPort(IInformationSystem.class);


        //Make some cool Queries
        while (true){

            System.out.println("CLIENT: TRYING TO PUBLISH MY POSITION ID= "+392193129);
            Position position = new Position();
            position.longitude = 2313;
            position.latitude = 2323;
            boolean succes= iInformationSystem.receivePosition(392193129,position);
            System.out.println("CLIENT: Publish Position worked out: "+succes);
        }
    }

}
