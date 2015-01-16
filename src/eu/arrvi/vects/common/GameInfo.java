package eu.arrvi.vects.common;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.lang.Integer.parseInt;

/**
 * Created by Arrvi on 2014-12-20.
 */
public class GameInfo extends CommandParameter {
    public final static String PATTERN = "^[0-9\\.]{7,15}:[0-9]{1,5};[0-9]+;[0-9]+;[^,]+?,[0-9]+$";
    
    private final InetAddress address;
    private final int port;
    private final int maxPlayers;
    private final int players;
    private final File track;
    private final int resolution;

    public GameInfo(InetAddress address, int port, int maxPlayers, int players, File track, int resolution) {
        this.address = address;
        this.port = port;
        this.maxPlayers = maxPlayers;
        this.players = players;
        this.track = track;
        this.resolution = resolution;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getPlayers() {
        return players;
    }

    public File getTrack() {
        return track;
    }

    public int getResolution() {
        return resolution;
    }

    public static CommandParameter getFromString(String command) throws UnknownHostException, IllegalArgumentException {
        String[] parts = command.split(";");
        String[] ip = parts[0].split(":");
        String[] track = parts[3].split(",");
        if ( parts.length != 4 || ip.length != 2 ) return null;
        return new GameInfo(
                InetAddress.getByName(ip[0]), 
                parseInt(ip[1]), 
                parseInt(parts[1]), 
                parseInt(parts[2]), 
                new File(track[0]),
                parseInt(track[1])
        );
    }

    @Override
    public String toString() {
        return address.toString() + ":" + port + ";" + maxPlayers + ";" + players + ";" + track.getPath() + "," + resolution;
    }

    @Override
    public boolean equals(Object obj) {
        if ( !(obj instanceof GameInfo) ) return false;
        GameInfo giObject = ((GameInfo) obj);
        return giObject.getAddress().equals(getAddress()) && giObject.getPort() == getPort();
    }

    @Override
    public int hashCode() {
        byte[] parts = getAddress().getAddress();
        int add = 0;
        for (int i = 0; i < 3; i++) {
            add = add << 8 + parts[i];
        }
        add = add << 16 + port & 0xffff;
        return add;
    }
}
