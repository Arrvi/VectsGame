package eu.arrvi.vects.spectator;

import eu.arrvi.common.UIUtilities;

import javax.swing.*;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Vects spectator app launcher. Should display server select window after running
 */
public class VectsSpectator {
    public static void main(String[] args) {
        UIUtilities.setSystemLookAndFeel();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new ServerSelectWindow();
                } catch (SocketException | UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
