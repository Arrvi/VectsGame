package eu.arrvi.vects.server;

/**
 * Created by Arrvi on 2014-12-18.
 */
public class IllegalMoveException extends GameException {
    public IllegalMoveException(int target) {
        super(target, "Illegal move");
    }
}
