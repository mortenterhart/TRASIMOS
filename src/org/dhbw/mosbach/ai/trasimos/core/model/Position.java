package org.dhbw.mosbach.ai.trasimos.core.model;

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
}
