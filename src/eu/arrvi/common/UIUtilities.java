package eu.arrvi.common;

import javax.swing.*;

/**
 * Created by Kris on 2015-01-14.
 */
public class UIUtilities {
    public static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            System.err.println("Cannot set system look and feel. Trying cross-platform.");
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e1) {
                System.err.println("Cannot set cross-platform look and feel neither. Leaving default.");
            }
        }
    }
    
    public static void packAndShow(JFrame frame) {
        packAndShow(frame, true);
    }
    public static void packAndShow(JFrame frame, boolean exitOnClose) {
        frame.pack();
        frame.setLocationRelativeTo(null);
        if ( exitOnClose ) frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
