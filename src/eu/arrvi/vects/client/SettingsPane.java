package eu.arrvi.vects.client;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

class SettingsPane extends JPanel implements ActionListener {
	/**
	 * Server address
	 */
	JTextField host;
	/**
	 * Server port
	 */
	JTextField port;
	/**
	 * Connect to server
	 */
	JButton connect;
	/**
	 * Send 'ready' command
	 */
	JButton ready;

	/**
	 * Parent reference for communication
	 *
	 * TODO #1 Refactor to event-driven model - remove parent reference
	 */
	private ClientWindow contr;

	/**
	 * Creates and configures settings panel
	 *
	 * TODO #1 Refactor to event-driven model - remove parent reference
	 *
	 * @param contr parent reference
	 */
	public SettingsPane(ClientWindow contr) {
		super();
		this.contr = contr;

		// Panel setup
		setBorder(new EmptyBorder(10,10,10,10));
		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(300,300));

		// Inner panel (centered and non-scalable)
		JPanel inner = new JPanel(new GridLayout(3, 2));
		inner.setBorder(new TitledBorder("Connection settings"));

		// Setup components
		inner.add(new JLabel("Host:"));
		inner.add(host = new JTextField("::1"));
		inner.add(new JLabel("Port:"));
		inner.add(port = new JTextField("9595"));
		inner.add(connect = new JButton("Connect"));
		inner.add(ready = new JButton("Ready"));

		add(inner);

		// Setup listeners
		connect.addActionListener(this);
		ready.addActionListener(this);
		ready.setEnabled(false);
	}

	/**
	 * Enables or disables settings. When connected, settings should not be enabled.
	 *
	 * FIXME State chaos
	 *
	 * @param enabled true if setting enabled, false otherwise
	 */
	public void setSettingsEnabled(boolean enabled) {
		host.setEnabled(enabled);
		port.setEnabled(enabled);
		connect.setEnabled(enabled);
	}

	/**
	 * Changes state of settings. When connected only 'ready' button should be enabled.
	 *
	 * FIXME State chaos
	 *
	 * @param connected
	 */
	public void setConnected(boolean connected) {
		ready.setEnabled(connected);
		setSettingsEnabled(!connected);
	}

	/**
	 * Perform button action
	 *
	 * FIXME Else-if chain
	 *
	 * @param e action event
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == connect ) {
			int port = Integer.parseInt(this.port.getText());
			contr.connect(host.getText(), port);
		}
		else if ( e.getSource() == ready ) {
			contr.setReady(true);
		}
	}
}
