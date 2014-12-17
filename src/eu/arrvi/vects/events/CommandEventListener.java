package eu.arrvi.vects.events;

import java.util.EventListener;

/**
 * Created by Arrvi on 2014-12-17.
 */
public interface CommandEventListener extends EventListener {
    public void commandReceived(CommandEvent event);
}
