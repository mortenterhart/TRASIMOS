package org.dhbw.mosbach.ai.base;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Position {

    @XmlAttribute(required = true)
    public double longitude=0;
    @XmlAttribute(required = true)
    public double latitude=0;

    public Position(){

    }

    public Position(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String toString() {
        return longitude + "," + latitude;
    }

    public Position midPoint(Position pos) {
        double mLat = (pos.latitude + this.latitude) / 2;
        double mLong = (pos.longitude + this.longitude) / 2;
        return new Position(mLong, mLat);
    }

    public double getLongitude(){
        return longitude;
    }

    public double getLatitude(){
        return latitude;
    }
}
