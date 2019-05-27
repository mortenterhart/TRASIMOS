package org.dhbw.mosbach.ai.information_system.provider;

import org.dhbw.mosbach.ai.base.MapChunk;
import org.dhbw.mosbach.ai.base.Position;
import org.dhbw.mosbach.ai.base.Radio.BroadcastConsumer;
import org.dhbw.mosbach.ai.base.Radio.Configuration;
import org.dhbw.mosbach.ai.base.V2Info;
import org.dhbw.mosbach.ai.information_system.api.IInformationSystem;
import org.dhbw.mosbach.ai.information_system.api.IPublishPosition;
import org.dhbw.mosbach.ai.name_server.api.NameServerSOAP;
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
import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@WebService(endpointInterface = "org.dhbw.mosbach.ai.information_system.api.IInformationSystem")
@Component(name = "information-system", service = {IInformationSystem.class, IPublishPosition.class})
public class InformationSystemImpl implements IPublishPosition, IInformationSystem {

    public volatile static int id = 0;
    private HashMap<Long, V2Info> vehiclesToObserve;
    private MapChunk areaBoundaries;
    private BroadcastConsumer nameListener;
    private String nameserviceURL;
    private String serviceURL;
    private boolean activationDone=false;


    private synchronized int getCurrID(){
        id++;
        return id;
    }

    public InformationSystemImpl(){
        int id = getCurrID();
        System.out.println("Information system booting ...");
        System.out.println("Try to register at name server ...");
        vehiclesToObserve = new HashMap<>();
        startListener();
        try {
            serviceURL = "http://" + Inet4Address.getLocalHost().getHostAddress() + ":12002/"+id+"/InformationServer";

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        activationDone=true;

        while (nameListener.isServiceFound()==false){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        nameserviceURL = nameListener.getServiceURLs().get(0);
        try {
            NameServerSOAP nameServerSOAP = new NameServerSOAP(nameserviceURL);
            String bounds =  nameServerSOAP.registerInfoServer(serviceURL);

            setAreaBoundaries(new MapChunk(bounds));

            IInformationSystem impl = this;
            Object implementor = impl;
            String address = "http://0.0.0.0:12002/"+id+"/InformationServer";
            Endpoint.publish(address, implementor);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Activate
    public void activate(ComponentContext context, BundleContext bundleContext, Map<String, ?> properties) {

    }


    public void startListener(){
        //Listen to Nameserver
        nameListener = new BroadcastConsumer(Configuration.NameService_multiCastAddress,Configuration.NameService_multiCastPort);
        Thread nameListenerThread = new Thread(nameListener);
        nameListenerThread.start();
    }



    @Deactivate
    public void deactivate() {
        System.out.println("Information system shutting down ...");
    }


    @Override
    @WebMethod
    public boolean receivePosition(V2Info v2Info) {

        try{

            System.out.println("SERVER: I RECEIVED A RECEIVE POSITION REQUEST");
            System.out.println("SERVER: CAR ID " + v2Info.V2id);
            System.out.println("SERVER: Position " + v2Info.position.latitude + "|" + v2Info.position.longitude);
            // Add/Update vehicle in Map
            // if vehicle id already exists the position of vehicle will be overridden
            if (isVehicleInBoundary(v2Info.position)) {
                vehiclesToObserve.put(v2Info.V2id, v2Info);
                return true;
            }

        }catch (Exception exp){
            System.out.println("Failed receive Position");
        }
        return false;
    }

    /**
     * get neighbours of vehicle by its id and speed.
     * requres that vehicle is observed by this information system
     *
     * @param v2Info  info of v2 vehicle
     * @return list of positions of neighbours
     */
    @Override
    @WebMethod
    public ArrayList<V2Info> getNeighbours(V2Info v2Info) {
        // If vehicle is new, neighbours can not be resolved

        if(v2Info.speed != 0.0) {
            // Vehicle needs to publish its position first
            if (isVehicleKnown(v2Info.V2id)) {
                ArrayList<V2Info> positionOfNeighbours = new ArrayList<>();
                // Check if vehicle is too close at boundary. If confirmed getNeighboursRemote from other information systems

                if (isVehicleNearBoundary(v2Info.position, v2Info.speed)) {
                    // Vehicle to close at boundary
                    // TODO: Ask other servers
                }
                // Add neighbours in boundary
                for (V2Info info : vehiclesToObserve.values()) {
                    if (distanceBetweenPositions(v2Info.position, info.position) <= calcStoppingDistance(v2Info.speed)) {
                        positionOfNeighbours.add(info);
                    }
                }
                return positionOfNeighbours;
            }
        }
        return new ArrayList<>();
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
    public ArrayList<V2Info> getNeighboursRemote(Position position, double radius) {
        ArrayList<V2Info> positionOfNeighbours = new ArrayList<>();

        for (V2Info info : vehiclesToObserve.values()) {
            if (distanceBetweenPositions(position, info.position) <= radius) {
                positionOfNeighbours.add(info);
            }
        }

        return positionOfNeighbours;
    }

    /**
     * remove vehicle from observing
     *
     * @param v2Info id of v2 vehicle
     */
    @Override
    @WebMethod
    public void receiveFinished(V2Info v2Info) {
        vehiclesToObserve.remove(v2Info.V2id);
        System.out.println("FINISH RECEIVED ___ REMOVE CAR WITH ID "+v2Info.V2id);
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
        return position.latitude > (areaBoundaries.getTopLeft().latitude - stoppingDistance) ||
                position.latitude < (areaBoundaries.getBottomLeft().latitude + stoppingDistance) ||
                position.longitude < (areaBoundaries.getTopLeft().longitude + stoppingDistance) ||
                position.longitude > (areaBoundaries.getTopRight().longitude - stoppingDistance);
    }

    private double calcStoppingDistance(double speed) {
        if (speed!=0) {
            return (speed / 10 * 3) + (speed / 10 * speed / 10);
        }else return 0;
    }

    public MapChunk getAreaBoundaries() {
        return areaBoundaries;
    }

    public void setAreaBoundaries(MapChunk areaBoundaries) {
        this.areaBoundaries = areaBoundaries;
    }

}