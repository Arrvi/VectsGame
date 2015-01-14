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
}
