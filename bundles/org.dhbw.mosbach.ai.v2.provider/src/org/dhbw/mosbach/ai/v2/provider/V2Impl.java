package org.dhbw.mosbach.ai.v2.provider;

import java.util.Map;
import java.util.Vector;


import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import javax.swing.text.Position;

@Component(name = "v2", service = IV2.class)
public class V2Impl implements IV2 {
    private int id;
    private Position origin;
    private Position destination;
    private Position currentPosition;
    private Vector<Long> direction;
    private double velocity;

    public V2Impl(double originLongitude, double originLatitude, double destinationLongitude, double destinationLatitude) {
        origin = new Position(originLongitude, originLatitude);
        destination = new Position(destinationLongitude, destinationLatitude);
    }

    @Activate
    public void activate(ComponentContext context, BundleContext bundleContext, Map<String, ?> properties) {
        System.out.println("V2 booting ...");
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
    public void run() {
        boolean isDriving = true;
        while (isDriving) {
            try {
                Thread.sleep(TIMEOUT);
                System.out.println("Driving");
            } catch (InterruptedException exc) {
                exc.printStackTrace();
            }
        }
    }
}
