package eu.arrvi.vects.spectator;

import eu.arrvi.vects.common.Command;
import eu.arrvi.vects.events.CommandEvent;
import eu.arrvi.vects.events.CommandEventListener;
import eu.arrvi.vects.events.CommandEventSupport;

import java.io.IOException;
import java.net.*;

/**
 * Vects spectator protocol handler
 */
public class DatagramHandler implements CommandEventListener {
    /**
     * Broadcast address for sending requests for server information
     */
    private InetAddress broadcastAddress;

    /**
     * Broadcast port for sending requests for server information
     */
    private int broadcastPort = 11531;
    
    /**
     * Socket for communication
     */
    DatagramSocket socket;

    /**
     * Input datagram
     */
    DatagramPacket input = new DatagramPacket(new byte[512], 512);
    
    CommandEventSupport ces = new CommandEventSupport(this);

    /**
     * Creates handler for spectator protocol. Configures socket and broadcast then starts to listen on given port.
     * 
     * @param port port to be listened to
     * @throws SocketException on problems with creating socket
     * @throws UnknownHostException on problems with getting localhost address
     */
    public DatagramHandler(int port) throws SocketException, UnknownHostException {
        InetAddress localhost = InetAddress.getLocalHost();
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localhost);

        socket = new DatagramSocket(port);

        broadcastAddress = networkInterface.getInterfaceAddresses().get(0).getBroadcast();
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        socket.receive(input);
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }).start();
    }

    /**
     * Send broadcast command. Mainly for getting server information.
     * 
     * @param data data to be sent via broadcast
     * @throws IOException on problems with sending datagram
     */
    private void sendBroadcast(String data) throws IOException {
        DatagramPacket output = new DatagramPacket(data.getBytes(), data.length()+1);
        output.setAddress(broadcastAddress);
        output.setPort(broadcastPort);
        output.setData(data.getBytes());
        socket.send(output);
    }
    
    public void close() {
        socket.close();
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

    @Override
    public void commandReceived(CommandEvent event) {
        
    }
    
    public void sendCommand(Command command) {
        
    }
}
