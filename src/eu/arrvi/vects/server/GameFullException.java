package eu.arrvi.vects.server;

/**
 * Created by Kris on 2014-12-18.
 */
public class GameFullException extends GameException {
    public GameFullException(int source) {
        this(source, null);
    }

    public GameFullException(int source, Throwable cause) {
        super(source, "No more slots left", cause);
    }
}
