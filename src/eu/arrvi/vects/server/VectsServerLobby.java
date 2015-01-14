package eu.arrvi.vects.server;

import eu.arrvi.common.UIUtilities;

import javax.swing.*;

/**
 * Created by Kris on 2015-01-14.
 */
public class VectsServerLobby {
    public static void main(String[] args) {
        UIUtilities.setSystemLookAndFeel();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LobbyWindow();
            }
        });
    }
}
