package org.dhbw.mosbach.ai.base;

public class Position {

    private double longitude;
    private double latitude;

    public Position(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String toString() {
        return longitude + "," + latitude;
    }

    public Position midPoint(Position pos) {
        double mLat = (pos.getLatitude() + this.getLatitude()) / 2;
        double mLong = (pos.getLongitude() + this.getLongitude()) / 2;
        return new Position(mLong, mLat);
    }
}
