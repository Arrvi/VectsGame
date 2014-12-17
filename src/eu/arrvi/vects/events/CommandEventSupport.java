package eu.arrvi.vects.events;

import eu.arrvi.vects.common.Command;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Support for accepting CommandEventListeners. Listeners may be added for all events or filtered by target or command.
 */
public class CommandEventSupport {
    private Object source;
    private Map<String,Set<CommandEventListener>> listenersByCommand = new HashMap<>();
    private Map<Integer,Set<CommandEventListener>> listenersByTarget = new HashMap<>();

    public CommandEventSupport(Object source) {
        this.source = source;
        listenersByCommand.put(null, new HashSet<CommandEventListener>());
        listenersByTarget.put(Command.TARGET_BROADCAST, new HashSet<CommandEventListener>());
    }
    
    public void addCommandEventListener(CommandEventListener listener) {
        addCommandEventListener(Command.TARGET_BROADCAST, null, listener);
    }
    
    public void addCommandEventListener(String command, CommandEventListener listener) {
        addCommandEventListener(Command.TARGET_BROADCAST, command, listener);
    }
    
    public void addCommandEventListener(int target, CommandEventListener listener) {
        addCommandEventListener(target, null, listener);
    }
    
    public void addCommandEventListener(int target, String command, CommandEventListener listener) {
        if ( !listenersByCommand.containsKey(command) ) {
            listenersByCommand.put(command, new HashSet<CommandEventListener>());
        }
        listenersByCommand.get(command).add(listener);
        
        if ( !listenersByTarget.containsKey(command) ) {
            listenersByTarget.put(target, new HashSet<CommandEventListener>());
        }
        listenersByTarget.get(target).add(listener);
    }
    
    public void removeCommandEventListener(CommandEventListener listener) {
        removeCommandEventListener(Command.TARGET_BROADCAST, null, listener);
    }
    
    public void removeCommandEventListener(String command, CommandEventListener listener) {
        removeCommandEventListener(Command.TARGET_BROADCAST, command, listener);
    }
    
    public void removeCommandEventListener(int target, CommandEventListener listener ) {
        removeCommandEventListener(target, null, listener);
    }
    
    public void removeCommandEventListener(int target, String command, CommandEventListener listener) {
        if ( listenersByCommand.containsKey(command)) {
            listenersByCommand.get(command).remove(listener);
        }
        if ( listenersByTarget.containsKey(target) ) {
            listenersByTarget.get(target).remove(listener);
        }
    }
    
    public void fireCommand(Command command) {
        fire(new CommandEvent(source, command));
    }
    
    private void fire(CommandEvent event) {
        Set<CommandEventListener> listenerSet = new HashSet<>();
        
        listenerSet.addAll(listenersByCommand.get(null));
        listenerSet.addAll(listenersByTarget.get(Command.TARGET_BROADCAST));
        
        if ( listenersByCommand.containsKey(event.getCommand().getName()) ) {
            listenerSet.addAll(listenersByCommand.get(event.getCommand().getName()));
        }
        if ( listenersByTarget.containsKey(event.getCommand().getTarget()) ) {
            listenerSet.addAll(listenersByTarget.get(event.getCommand().getTarget()));
        }

        for (CommandEventListener listener : listenerSet) {
            listener.commandReceived(event);
        }
    }
}
