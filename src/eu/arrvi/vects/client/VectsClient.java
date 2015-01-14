package eu.arrvi.vects.client;

import eu.arrvi.common.UIUtilities;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Main client bootstrap. Takes no parameters.
 */
public class VectsClient {

	public static void main(String[] args) {
		UIUtilities.setSystemLookAndFeel();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ClientWindow();
			}
		});
	}

}
