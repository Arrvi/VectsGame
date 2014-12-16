package eu.arrvi.vects.spectator;

import javax.swing.*;

/**
 * Vects spectator app launcher. Should display server select window after running
 */
public class VectsSpectator {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ServerSelectWindow();
            }
        });
    }
}
