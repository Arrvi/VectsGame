package eu.arrvi.vects.server;

import eu.arrvi.common.UIUtilities;

import javax.swing.*;
import java.io.File;

/**
 * Created by Kris on 2014-11-14.
 */
public class VectsServer {
    private Game game;
    private Server server;

    public static void main(String[] args) {
        new VectsServer();
    }

    public VectsServer() {
        UIUtilities.setSystemLookAndFeel();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ServerSettingsWindow(VectsServer.this);
            }
        });
    }

    public void startGame(int port, String trackPath, int resolution, int players) throws Exception {
        game = new Game(new Track(new File(trackPath), resolution));
        game.setNumberOfPlayers(players);

        server = new Server(game);
        server.setPort(port);

        server.start();

        ServerWindow gui = new ServerWindow();
        game.addPropertyChangeListener(gui);
    }
}
