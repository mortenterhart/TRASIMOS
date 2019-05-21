package org.dhbw.mosbach.ai.v2.provider;

import java.util.Map;
import java.util.Vector;


import org.dhbw.mosbach.ai.v2.api.IV2;
import org.dhbw.mosbach.ai.base.Position;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(name = "v2", service = IV2.class)
public class V2Impl implements IV2 {
    private int id;
    private Position origin;
    private Position destination;
    private Position currentPosition;
    private Vector<Long> direction;
    private double velocity;
    private List<Position> routePositions;

    public V2Impl(double originLongitude, double originLatitude, double destinationLongitude, double destinationLatitude) {
        origin = new Position(originLongitude, originLatitude);
        destination = new Position(destinationLongitude, destinationLatitude);
        currentPosition = origin;
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

    public Position getDestination() {
        return destination;
    }

    public Position getOrigin() {
        return origin;
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public int getId() {
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
                Thread.sleep(TIMEOUT);
                publishPosition();
                retrieveNeighbours();
                coinFlipBraking();
                drive();
            } catch (InterruptedException exc) {
                exc.printStackTrace();
            }
        }
    }

    public void publishPosition() {

    }

    public void retrieveNeighbours() {

    }

    public void coinFlipBraking() {

    }

    public void drive() {
        System.out.println("Driving");
    }
}
