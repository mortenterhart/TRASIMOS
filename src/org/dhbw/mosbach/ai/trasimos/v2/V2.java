package org.dhbw.mosbach.ai.trasimos.v2;

import org.dhbw.mosbach.ai.trasimos.core.IV2;
import org.dhbw.mosbach.ai.trasimos.core.model.Position;

import java.util.Vector;

public class V2 implements IV2 {
    private int id;
    private Position origin;
    private Position destination;
    private Position currentPosition;
    private Vector<Long> direction;
    private double velocity;


    public V2(double originLongitude, double originLatitude, double destinationLongitude, double destinationLatitude) {
        origin = new Position(originLongitude, originLatitude);
        destination = new Position(destinationLongitude, destinationLatitude);
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public Position getDestination() {
        return destination;
    }

    public Position getOrigin() {
        return origin;
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

    @Override
    public void getCurrentPosition(Position position, Vector<Long> direction, double velocity) {

    }
}

}
