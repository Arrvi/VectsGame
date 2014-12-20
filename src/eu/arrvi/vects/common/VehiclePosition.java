package eu.arrvi.vects.common;

/**
 * Point on track representing position of given vehicle 
 */
public class VehiclePosition extends TrackPoint {
    public final static String PATTERN = "^[0-9]+;[0-9]+,[0-9]+$";
    private int vehicleId;

    /**
     * Creates point on track with given coordinates. [0, 0] is top left corner.
     *
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
    
    public static CommandParameter getFromString(String str) throws NumberFormatException {
        String[] parts = str.split("[;,]");
        if ( parts.length != 3 ) {
            return null;
        }
        
        return new VehiclePosition(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2])
        );
    }

    @Override
    public String toString() {
        return String.format("%d;%d,%d", getVehicleId(), getX(), getY());
    }
}
