package eu.arrvi.vects.server;

/**
 * Created by Arrvi on 2014-12-18.
 */
public class VehicleNotActiveException extends GameException {
    public VehicleNotActiveException(int target) {
        super(target, "Not your turn");
    }
}
