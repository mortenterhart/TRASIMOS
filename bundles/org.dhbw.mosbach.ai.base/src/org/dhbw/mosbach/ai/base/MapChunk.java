package org.dhbw.mosbach.ai.base;

public class MapChunk {

    private Position topLeft;
    private Position topRight;
    private Position bottomLeft;
    private Position bottomRight;

    public MapChunk() {

    }

    public MapChunk(String boundaries) {

        String[] boundariesArr = boundaries.split(":");

        for (int i = 0; i < boundariesArr.length; i++) {
            String[] posArr = boundariesArr[i].split(",");

            if (i == 2) {
                this.topLeft = new Position(Double.parseDouble(posArr[0]), Double.parseDouble(posArr[1]));
            } else if (i == 3) {
                this.topRight = new Position(Double.parseDouble(posArr[0]), Double.parseDouble(posArr[1]));
            } else if (i == 0) {
                this.bottomLeft = new Position(Double.parseDouble(posArr[0]), Double.parseDouble(posArr[1]));
            } else if (i == 1) {
                this.bottomRight = new Position(Double.parseDouble(posArr[0]), Double.parseDouble(posArr[1]));
            }
        }
    }

    public Position getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(Position topLeft) {
        this.topLeft = topLeft;
    }

    public Position getTopRight() {
        return topRight;
    }

    public void setTopRight(Position topRight) {
        this.topRight = topRight;
    }

    public Position getBottomLeft() {
        return bottomLeft;
    }

    public void setBottomLeft(Position bottomLeft) {
        this.bottomLeft = bottomLeft;
    }

    public Position getBottomRight() {
        return bottomRight;
    }

    public void setBottomRight(Position bottomRight) {
        this.bottomRight = bottomRight;
    }

    public boolean isWithin(Position p) {

        System.out.println("_______POINT________");
        System.out.println(posToString(p));
        System.out.println("_______Bounds_______");
        System.out.println(posToString(topLeft)+"_____"+posToString(topRight));
        System.out.println("................................................");
        System.out.println(posToString(bottomLeft)+"_____"+posToString(bottomRight));
        System.out.println("____________________________________________________-");


        boolean latitude = topLeft.latitude <= p.latitude && bottomRight.latitude >= p.latitude;
        boolean longtitude = topLeft.longitude <= p.longitude && bottomRight.longitude >= p.longitude;

        System.out.println("RESULT Fabian : INBOUNDS "+(latitude&&longtitude));

        boolean a = p.getLatitude()  >= this.bottomLeft.getLatitude();
        boolean b =p.getLatitude()  <= this.topRight.getLatitude()    ;
        boolean c =       p.getLongitude() >= this.bottomLeft.getLongitude() ;
        boolean d=     p.getLongitude() <= this.topRight.getLongitude();

        System.out.println("Result MICHA: INBOUNDS"+(a&&b&&c&&d));

        return (latitude&&longtitude);
    }

    public String posToString(Position p){
        return "Lat: "+p.getLatitude()+" Long "+p.getLongitude();
    }
}
