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

            if (i == 0) {
                this.topLeft = new Position(Double.parseDouble(posArr[0]), Double.parseDouble(posArr[1]));
            } else if (i == 1) {
                this.topRight = new Position(Double.parseDouble(posArr[0]), Double.parseDouble(posArr[1]));
            } else if (i == 2) {
                this.bottomLeft = new Position(Double.parseDouble(posArr[0]), Double.parseDouble(posArr[1]));
            } else if (i == 3) {
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
        if (topLeft.latitude >= p.latitude && p.latitude >= bottomLeft.latitude) {
            if (topLeft.longitude <= topRight.longitude && topLeft.longitude <= p.longitude && p.longitude <= topRight.longitude) {
                return true;
            } else if (topLeft.longitude > topRight.longitude && (topLeft.longitude <= p.longitude || p.longitude <= topRight.longitude)) {
                return true;
            }
        }
        return false;
    }
}
