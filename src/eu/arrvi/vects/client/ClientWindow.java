package eu.arrvi.vects.client;

import eu.arrvi.common.UIUtilities;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.*;

class ClientWindow extends JFrame {
	/**
	 * Settings panel for setting connection parameters. Interchangeable.
	 */
	private SettingsPane settings;

	/**
	 * Game panel for game control. Shows track and allows doing moves.
	 */
	private GamePane game;

	/**
	 * Chat panel. Displays chat and system messages.
	 */
	private ChatPane chat;

	/**
	 * Vects protocol handler
	 */
	private ClientSocketHandler socket;

	/**
	 * Is client connected to server.
	 */
	private boolean connected = false;

	/**
	 * Main panel. Stored as field because of interchangeable elements in center section.
	 */
	private final JPanel mainPane = new JPanel(new BorderLayout());

	/**
	 * Scrolling element for game panel. Interchangeable.
	 */
	private final JScrollPane gameScroll;

	/**
	 * Current component at center section in main panel.
	 * Stored only to be removed // poor guy :(
	 */
	private JComponent activeCenter;

	/**
	 * Chat message handler.
	 */
	private PropertyChangeListener chatListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            socket.sendCommand("CHT " + evt.getNewValue());
        }
    };

	/**
	 * Creates, configures and displays client window as main user interface.
	 */
	public ClientWindow() {
		super("Vects, the Online Game");

		// Init components
		settings = new SettingsPane(this);
		game = new GamePane(this);
		chat = new ChatPane();
		gameScroll = new JScrollPane(game);
		gameScroll.setPreferredSize(new Dimension(600, 0));

		// chat is disabled because client is not connected
		chat.setChatEnabled(false);

		// add chat
		mainPane.add(chat, BorderLayout.EAST);

		// set settings as default center component
		setCenterComponent(settings);

		// set everything in window
		setContentPane(mainPane);

		// display window
		UIUtilities.packAndShow(this);
	}

	/**
	 * Sets new component in center section of main pane, firstly removing current one.
	 *
	 * @param component component to be displayed in the center
	 */
	private void setCenterComponent(JComponent component) {
		if ( activeCenter != null )
			mainPane.remove(activeCenter);
		activeCenter = component;
		mainPane.add(activeCenter, BorderLayout.CENTER);
		revalidate();
	}

	/**
	 * Creates socket and connects to specified host in separate thread.
	 *
	 * @param host server host address
	 * @param port server port
	 */
	public void connect(String host, int port) {
		setSettingsEnabled(true);
		(new Thread(socket = new ClientSocketHandler(this, host, port))).start();
	}

	/**
	 * FIXME dafuq is going on here?
	 * @param lock
	 */
	public void setSettingsEnabled(boolean lock) {
		settings.setSettingsEnabled(lock);
	}

	/**
	 * Displays message in chat panel
	 * @param message message to be displayed. HTML allowed
	 */
	public void chatMessage(String message) {
		chat.message(message);
	}

	/**
	 * Displays message from specific author in chat panel
	 * @param author author of the message
	 * @param message message to be displayed. HTML not allowed
	 */
	public void chatMessage(String author, String message) {
		chat.message(author, message);
	}

	/**
	 * Sets current track and it's resolution. Throws exception if cannot read image file.
	 *
	 * TODO #2 non square image support
	 *
	 * @param path track image file path
	 * @param resolution track resolution
	 * @throws IOException if image cannot be read
	 */
	public void setTrack(String path, int resolution) throws IOException {
		game.setTrack(path, resolution);
	}

	/**
	 * Sends ready command to server
	 *
	 * TODO #1.3 remove protocol elements from classes other than protocol handler
	 * FIXME state chaos
	 *
	 * @param ready true if client is ready, false otherwise
	 */
	public void setReady(boolean ready) {
		if ( ready ) {
			socket.sendCommand("RDY");
			setCenterComponent(gameScroll);
		} else {
			socket.sendCommand("NOT");
		}
	}

	/**
	 * ACK command handling
	 *
	 * TODO #1 Refactor to event-driven model
	 *
	 * @param value
	 * @param info
	 */
	public void serverResponse(boolean value, String info) {
		if ( !connected )  {
			setConnected(value);
			chatMessage("<html><font color=#009900>Connected");
		}
	}

	/**
	 * Sets connection states
	 *
	 * TODO #1 Refactor to event-driven model
	 *
	 * @param connected true is connected, false otherwise
	 */
	public void setConnected(boolean connected) {
		this.connected = connected;
		settings.setConnected(connected);
		if ( connected ) {
			chat.addPropertyChangeListener(chatListener);
		}
		else {
			chat.removePropertyChangeListener(chatListener);
		}
		chat.setChatEnabled(true);
	}

	/**
	 * Update positions on game panel.
	 *
	 * TODO #1 Refactor to event-driven model
	 *
	 * @param map Current state of all vehicles on board (this is NOT a history of positions)
	 */
	public void updatePositions(Map<Integer, Point> map) {
		game.updatePositions(map);
		remove(settings);
	}

	/**
	 * Sets current move targets and start move.
	 *
	 * TODO #1 Refactor to event-driven model
	 *
	 * @param pts Target points to be shown
	 */
	public void setTargets(ArrayList<Point> pts) {
		game.setTargets(pts);
	}

	/**
	 * Do move to given point (send command to server).
	 *
	 * TODO #1.3 remove protocol elements from classes other than protocol handler
	 * @param point
	 */
	public void moveTo(Point point) {
		socket.sendCommand("MOV "+(int)point.getX()+","+(int)point.getY());
	}

	/**
	 * Get this client ID
	 *
	 * @return id of this client (server port that client is connected to)
	 */
	public int getID() {
		return socket.getPort();
	}

	// TODO #7 client name support
}
