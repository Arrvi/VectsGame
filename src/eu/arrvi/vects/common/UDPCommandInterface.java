package eu.arrvi.vects.common;

import eu.arrvi.vects.events.CommandEvent;
import eu.arrvi.vects.events.CommandEventListener;
import eu.arrvi.vects.events.CommandEventSupport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Interface for UDP command communication. Uses {@link eu.arrvi.vects.common.UDPAdapter} which means that commands
 * can be virtually infinitely long.
 */
@SuppressWarnings({"UnusedDeclaration", "JavaDoc"})
public class UDPCommandInterface implements CommandEventListener {
    private final UDPAdapter adapter;
    private final CommandEventSupport ces = new CommandEventSupport(this);

    /**
     * Creates command interface for UDP communication.
     * @param address destination address
     * @param lport local port to be listened on
     * @param dport destination port
     * @throws SocketException on socket creation errors (especially when given port is unavailable)
     */
    public UDPCommandInterface(InetAddress address, int lport, int dport) throws SocketException {
        adapter = new UDPAdapter(address, lport, dport);
    }

    /**
     * Creates command interface for UDP broadcast communication.
     * @param lport local port to be listened on
     * @param dport destination port
     * @throws SocketException on socket creation errors (especially when given port is unavailable)
     */
    public UDPCommandInterface(int lport, int dport) throws SocketException {
        adapter = new UDPAdapter(lport, dport);
    }
    
    private void startListeningThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                while (true) {
                    try {
                        message = adapter.read();
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                    Command command = Command.getCommandFromString(adapter.getPort(), message);
                    
                    if (command == null) continue;
                    
                    ces.fireCommand(command);
                }
            }
        });
    }

    @Override
    public void commandReceived(CommandEvent event) {
        try {
            adapter.write(event.getCommand().getCommandString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public void addCommandEventListener(CommandEventListener listener) {
        ces.addCommandEventListener(listener);
    }

    public void addCommandEventListener(String command, CommandEventListener listener) {
        ces.addCommandEventListener(command, listener);
    }

    public void addCommandEventListener(int target, CommandEventListener listener) {
        ces.addCommandEventListener(target, listener);
    }

    public void addCommandEventListener(int target, String command, CommandEventListener listener) {
        ces.addCommandEventListener(target, command, listener);
    }

    public void removeCommandEventListener(CommandEventListener listener) {
        ces.removeCommandEventListener(listener);
    }

    public void removeCommandEventListener(String command, CommandEventListener listener) {
        ces.removeCommandEventListener(command, listener);
    }

    public void removeCommandEventListener(int target, CommandEventListener listener) {
        ces.removeCommandEventListener(target, listener);
    }

    public void removeCommandEventListener(int target, String command, CommandEventListener listener) {
        ces.removeCommandEventListener(target, command, listener);
    }
}
