package eu.arrvi.vects.events;

import eu.arrvi.vects.common.Command;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Arrvi on 2014-12-17.
 */
public class CommandEventSupport {
    private Object source;
    private Map<String,Set<CommandEventListener>> listeners = new HashMap<>();

    public CommandEventSupport(Object source) {
        this.source = source;
    }
    
    public void addCommandEventListener(CommandEventListener listener) {
        addCommandEventListener(null, listener);
    }
    
    public void addCommandEventListener(String command, CommandEventListener listener) {
        if ( !listeners.containsKey(command) ) {
            listeners.put(command, new HashSet<CommandEventListener>());
        }
        listeners.get(command).add(listener);
    }
    
    public void removeCommandEventListener(CommandEventListener listener) {
        removeCommandEventListener(null, listener);
    }
    
    public void removeCommandEventListener(String command, CommandEventListener listener) {
        if ( !listeners.containsKey(command)) {
            return;
        }
        listeners.get(command).remove(listener);
    }
    
    public void fireCommand(Command command) {
        fire(new CommandEvent(source, command));
    }
    
    private void fire(CommandEvent event) {
        if ( listeners.containsKey(null) ) {
            for (CommandEventListener commandEventListener : listeners.get(null)) {
                commandEventListener.commandReceived(event);
            }
        }
        if ( listeners.containsKey(event.getCommand().getName()) ) {
            for (CommandEventListener commandEventListener : listeners.get(event.getCommand().getName())) {
                commandEventListener.commandReceived(event);
            }
        }
    }
}
