package org.dhbw.mosbach.ai.v2.provider;


import org.dhbw.mosbach.ai.base.Position;

import java.util.ArrayList;

public class Main {

    public static void main(String args[]) {

        ArrayList<Position> positions = new ArrayList<>();
        Position pos1 = new Position(49.303717, 9.002668);
        Position pos2 = new Position(49.308762, 9.001426);

        double distance = distance(pos1.latitude, pos1.longitude, pos2.latitude, pos2.longitude);
        System.out.println(distance);
    }


    public static void testCar() {

        ArrayList<Position> route = getRouteDummy();

        Position orgin = route.get(0);
        Position dest = route.get(2);
        Position currPos = new Position();
        currPos.latitude = orgin.latitude;
        currPos.longitude = orgin.longitude;

        int routeIndex = 1;

        while (currPos.latitude != dest.latitude && currPos.longitude != dest.longitude) {


            while (currPos.latitude != route.get(routeIndex).latitude && currPos.longitude != route.get(routeIndex).longitude) {

                System.out.println("------------------------------------");
                System.out.println("Curr pos: " + currPos.latitude + " | " + currPos.longitude);
                System.out.println("Next pos" + route.get(routeIndex).latitude + "|" + route.get(routeIndex).longitude);
                System.out.println("------------------------------------");
                currPos = drive(currPos.latitude, currPos.longitude, route.get(routeIndex).latitude, route.get(routeIndex).longitude);


            }

            routeIndex++;

        }

        System.out.println("FINISHED");

    }

    public static Position drive(double x1, double y1, double x2, double y2) {
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


    public static ArrayList<Position> getRouteDummy() {
        ArrayList<Position> positions = new ArrayList<>();

        positions.add(new Position(9.002668, 49.303717));
        positions.add(new Position(9.016426, 49.400762));
        positions.add(new Position(9.084893, 49.551546));

        return positions;

    }

}
