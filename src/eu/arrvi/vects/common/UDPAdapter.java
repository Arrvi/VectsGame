package eu.arrvi.vects.common;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

/**
 * Helper for dealing with non-human usable datagram packets. Also splits longer messages. 
 * Binary data is not supported. <br>
 * 
 * Each segment has following structure:
 *
 * <table border="1">
 *     <tr>
 *         <th>+</th>
 *         <th>byte 0</th>
 *         <th>byte 1</th>
 *     </tr>
 *     <tr>
 *         <th>0</th>
 *         <td>number of chunks</td>
 *         <td>current chunk number<br>(numbering starts at 0)</td>
 *     </tr>
 *     <tr>
 *         <th>2</th>
 *         <td colspan="2" align="center">data as string</td>
 *     </tr>
 * </table>
 */
@SuppressWarnings("UnusedDeclaration")
public class UDPAdapter {
    private final InetAddress address;
    private final int port;
    private int chunkTimeout = 2000;
    private DatagramSocket socket;
    private int chunkSize = 1024;

    /**
     * Creates helper for UDP communication on specified address and port.
     * @param address destination address
     * @param lport local port to use
     * @param dport destination port
     * @throws SocketException on socket creation errors (especially when given port is unavailable)
     */
    public UDPAdapter(InetAddress address, int lport, int dport) throws SocketException {
        this.address = address;
        port = dport;
        try {
            socket = new DatagramSocket(lport, Inet4Address.getByName("0.0.0.0"));
            socket.connect(address, dport);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates helper for UDP broadcast communication on specified port.
     * @param lport local port to receive broadcasts
     * @param dport remote port to send broadcasts
     * @throws SocketException on socket creation errors (especially when given port is unavailable)
     */
    public UDPAdapter(int lport, int dport) throws SocketException {
        address = getBradcastAddress();
        port = dport;
        try {
            socket = new DatagramSocket(lport, Inet4Address.getByName("0.0.0.0"));
            socket.setBroadcast(true);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends data through UDP socket. Long messages will be split into chunks and sent separately. 
     * @param message message to send
     * @throws IOException on write error
     */
    public void write(String message) throws IOException {
        byte[] bytes = message.getBytes();
        byte chunks = (byte) ((bytes.length + chunkSize - 1) / chunkSize);
        
        DatagramPacket packet = null;
        for (byte chunk = 0; chunk < chunks; ++chunk) {
            int offset = chunk* chunkSize;
            int length = (chunk==chunks-1) ? bytes.length-offset : chunkSize;
            byte[] buff = new byte[length+2];
            buff[0] = chunks;
            buff[1] = chunk;
            System.arraycopy(bytes, offset, buff, 2, chunkSize);
            if ( packet == null ) {
                packet = new DatagramPacket(buff, 0, buff.length, address, port);
            }
            else {
                packet.setData(buff, 0, buff.length);
            }

            socket.send(packet);
        }
    }

    /**
     * Receives data on UDP socket. No timeout on first packet.
     * @return combined message
     * @throws IOException on socket error or timeout between chunks
     * @see #read(int)
     */
    public String read() throws IOException {
        return read(0);
    }

    /**
     * Receives data on UDP socket. It accepts and combines chunks if there is more than one (specified in header).
     * @param timeout read timeout on first packet (timeout between chunks is set by {@link #setChunkTimeout(int)} method)
     * @return combined message
     * @throws IOException on socket read error or timeout
     */
    @SuppressWarnings("OverlyBroadThrowsClause")
    public String read(int timeout) throws IOException {
        StringBuilder sb = new StringBuilder();
        DatagramPacket packet = new DatagramPacket(new byte[chunkSize+2], chunkSize+2);
        
        socket.setSoTimeout(timeout);
        socket.receive(packet);
        
        byte[] data = packet.getData();
        byte totalChunks = data[0];
        byte chunksReceived = 0;
        
        do {
            String part = new String(data, 2, packet.getLength()-2);
            sb.insert(data[1]*chunkSize, part);
            chunksReceived++;
            if (chunksReceived<totalChunks) {
                socket.setSoTimeout(chunkTimeout);
                socket.receive(packet);
            }
        } while(chunksReceived<totalChunks);
        
        return sb.toString();
    }

    public int getChunkTimeout() {
        return chunkTimeout;
    }

    public void setChunkTimeout(int chunkTimeout) {
        this.chunkTimeout = chunkTimeout;
    }
    
    private static InetAddress broadcastAddress=null; // Cache
    public static InetAddress getBradcastAddress() throws SocketException {
        if ( broadcastAddress != null ) {
            return broadcastAddress;
        }
        
        /* Code: http://enigma2eureka.blogspot.com/2009/08/finding-your-ip-v4-broadcast-address.html */
        Enumeration<NetworkInterface> interfaces =
                NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isLoopback())
                continue;    // Don't want to broadcast to the loopback interface
            for (InterfaceAddress interfaceAddress :
                    networkInterface.getInterfaceAddresses()) {
                InetAddress broadcast = interfaceAddress.getBroadcast();
                if (broadcast == null)
                    continue;
                
                broadcastAddress = broadcast;
                return broadcast;
            }
        }
        
        return null;
    }

    public int getPort() {
        return socket.getLocalPort();
    }
}
