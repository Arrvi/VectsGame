package eu.arrvi.vects.spectator;

import java.io.IOException;
import java.net.*;

/**
 * Vects spectator protocol handler
 */
public class DatagramHandler {
    /**
     * Broadcast address for sending requests for server information
     */
    private InetAddress broadcast;

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

        broadcast = networkInterface.getInterfaceAddresses().get(0).getBroadcast();
        
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
        output.setAddress(broadcast);
        output.setPort(broadcastPort);
        output.setData(data.getBytes());
        socket.send(output);
    }
}
