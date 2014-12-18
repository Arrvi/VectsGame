package eu.arrvi.vects.server;

import eu.arrvi.vects.common.Command;
import eu.arrvi.vects.common.SimpleInfo;

/**
 * Created by Kris on 2014-12-18.
 */
public class GameException extends RuntimeException {
    private final int source;
    
    public GameException(int source, String message, Throwable cause) {
        super(message, cause);
        this.source = source;
    }

    public GameException(int source, String message) {
        super(message);
        this.source = source;
    }
    
    public GameException(int source) {
        this.source = source;
    }

    public int getSource() {
        return source;
    }

    public Command getDenialCommand() {
        return new Command(source, "DEN", new SimpleInfo(getMessage()));
    }
}
