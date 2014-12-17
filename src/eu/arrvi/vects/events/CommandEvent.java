package eu.arrvi.vects.events;

import eu.arrvi.vects.common.Command;

import java.util.EventObject;

/**
 * Event that represents vects protocol command.
 */
public class CommandEvent extends EventObject {

    private final Command command;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public CommandEvent(Object source, Command command) {
        super(source);
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }
}
