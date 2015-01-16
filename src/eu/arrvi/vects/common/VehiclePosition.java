package eu.arrvi.vects.common;

/**
 * Point on track representing position of given vehicle 
 */
public class VehiclePosition extends TrackPoint {
    public final static String PATTERN = "^[0-9]+;[0-9]+,[0-9]+$";
    private int vehicleId;

    /**
     * Creates point on track with given coordinates for specified vehicle. [0, 0] is top left corner.
     *
     * @param vehicleId id of a vehicle with this position
     * @param x horizontal distance to left edge
     * @param y vertical distance to top edge
     */
    public VehiclePosition(int vehicleId, int x, int y) {
        super(x, y);
        this.vehicleId = vehicleId;
    }

    public VehiclePosition(int id, TrackPoint position) {
        this(id, position.getX(), position.getY());
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    /**
     * Parses a string and returns corresponding command parameter.
     * 
     * @param str string representation of this parameter
     * @return null on parse error, parameter object representation of given string otherwise
     */
    public static CommandParameter getFromString(String str) {
        String[] parts = str.split("[;,]");
        
        if ( parts.length != 3 ) {
            return null;
        }
        
        int vid,x,y;
        
        try {
            vid = Integer.parseInt(parts[0]);
            x = Integer.parseInt(parts[1]);
            y = Integer.parseInt(parts[2]);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
        
        return new VehiclePosition(vid, x, y);
    }

    @Override
    public String toString() {
        return String.format("%d;%d,%d", getVehicleId(), getX(), getY());
    }
}
