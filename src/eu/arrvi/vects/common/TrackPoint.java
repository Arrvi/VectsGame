package eu.arrvi.vects.common;

/**
 * Point on track. Integer coordinates
 */
public class TrackPoint extends CommandParameter {
    public final static String PATTERN = "^[0-9]+,[0-9]+";
    private int x;
    private int y;

    /**
     * Creates point on track with given coordinates. [0, 0] is top left corner.
     * 
     * @param x horizontal distance to left edge
     * @param y vertical distance to top edge
     */
    public TrackPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get first coordinate of this point.
     * 
     * @return horizontal distance to left edge of track
     */
    public int getX() {
        return x;
    }


    /**
     * Set first coordinate of this point.
     *
     * @param x horizontal distance to left edge of track
     */
    public void setX(int x) {
        this.x = x;
    }


    /**
     * Get second coordinate coordinate of this point.
     *
     * @return vertical distance to top edge of track
     */
    public int getY() {
        return y;
    }


    /**
     * Set second coordinate of this point.
     *
     * @param y vertical distance to top edge of track
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Creates point based on string in form `x,y` (used in protocol commands)
     * @param str string representation of point in form `x,y`
     * @return `null` if string has incorrect syntax, new point with given coordinates otherwise
     * @throws java.lang.NumberFormatException if any part of given string does not represent integer
     */
    public static TrackPoint getPointFromString(String str) throws NumberFormatException {
        String[] coordinates = str.split(",");
        if ( coordinates.length != 2) return null;
        return new TrackPoint(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]));
    }

    @Override
    public String toString() {
        return getX()+","+getY();
    }
}

