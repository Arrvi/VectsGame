package eu.arrvi.vects.server;

import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Main game logic class
 */
class Game {
    /**
     * Status constants.
     *
     * WAITING - server listens for new clients or waiting for ready messages
     * STARTED - game is running, mainly waiting for player to move.
     * FINISHED - game ended
     * ERROR - an error occurred during game
     */
	public final static int WAITING = 1, STARTED = 2, FINISHED = 3, ERROR = 4;

    /**
     * Strings for status constants.
     */
	public final static String[] statusMessages = {"Unknown", "Waiting", "Started", "Finished", "Error"};

    /**
     * PCS for server state and player list.
     */
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Number of players currently in game
     */
	int inGame;

    /**
     * Track being played
     */
	private Track track;

    /**
     * List of Vehicles that play this game.
     */
	private List<Vehicle> vehicles = new ArrayList<Vehicle>();

    /**
     * List of clients connected to server.
     */
	private List<ServerSocketHandler> broadcastSockets = new ArrayList<ServerSocketHandler>();

    /**
     * Number of players required to start a game.
     */
	private int numberOfPlayers=2;

    /**
     * Index of a current vehicle in list of vehicles. This is NOT player ID.
     */
	private int currentPlayer = -1;

    /**
     * Status of a game.
     *
     * WAITING - server listens for new clients or waiting for ready messages
     * STARTED - game is running, mainly waiting for player to move.
     * FINISHED - game ended
     * ERROR - an error occurred during game
     */
	private int status = WAITING;

    /**
     * Creates new game that waits for new players and eventually starts.
     *
     * @param track track to be played
     * @throws IllegalArgumentException if given track cannot be played (problem with files or not enough of each field type).
     */
	public Game(Track track) throws IllegalArgumentException {
		this.track = track;
		if ( !track.canRace() ) {
			throw new IllegalArgumentException("Can't race this track");
		}
		setStatus(WAITING);
	}

    /**
     * Returns human-readable form of given game status.
     *
     * @param i status constant
     * @return hr game status
     */
	public static String getStatusString (int i) {
		return statusMessages[i];
	}

    /**
     * Adds new ready vehicle to the game. If game is already full, vehicle won't be added. No exceptions are thrown.
     *
     * @param v vehicle to be added
     */
	public void addVehicle(Vehicle v) {
		if ( vehicles.size() >= numberOfPlayers ) {
			v.doCommand("DEN No more slots left");
			return;
		}
		vehicles.add(v);
		inGame++;

		pcs.firePropertyChange("playerList", null, broadcastSockets);
	}

    /**
     * Removes vehicle from the game.
     *
     * @param v vehicle to be removed.
     */
	public void removeVehicle(Vehicle v) {
		// vehicles.remove(v); // FIXME - wtf?
		pcs.firePropertyChange("playerList", null, broadcastSockets);
	}
	
	public void addBroadcast(ServerSocketHandler socket) {
		socket.sendCommand("ACK "+socket.getPort());
		socket.sendCommand("TRK "+track.getTrackPath()+";"+track.getResolution());
		broadcastSockets.add(socket);
		pcs.firePropertyChange("playerList", null, broadcastSockets);
	}

	public void removeBroadcast(ServerSocketHandler socket) {
		broadcastSockets.remove(socket);
		pcs.firePropertyChange("playerList", null, broadcastSockets);
	}

	public boolean ready() {
		int actives = 0;
		for (Vehicle vehicle : vehicles) {
			if ( vehicle.isReady() ) actives++;
		}
		System.out.println(actives+" of "+numberOfPlayers+" active");
		broadcastCommand("CHT SERVER;"+actives+" of "+numberOfPlayers+" ready");

		return actives >= numberOfPlayers;

	}

	private void shufflePlayers() {
		Collections.shuffle(vehicles);
	}

	private void startGame() {
		System.out.println("Starting game");
		shufflePlayers();
		setStatus(STARTED);
		nextPlayer();
	}

	public void nextPlayer() {
		if ( currentPlayer != -1 ) {
			if (status != STARTED) {
				return;
			}
			if (vehicles.size() == 0) {
				return;
			}
			if (vehicles.get(currentPlayer).isActive() && vehicles.get(currentPlayer).isDestroyed()) {
				broadcastCommand("CHT SERVER;Player " + vehicles.get(currentPlayer).getID() + " crashed");
				inGame--;
			}
			if (numberOfPlayers >= 2 && inGame <= 1) {
				for (Vehicle v : vehicles) {
					if (!v.isDestroyed()) {
						v.doCommand("WIN All other players crashed");
						end(v);
						return;
					}
				}
			}
			for (Vehicle vehicle : vehicles) {
				vehicle.setActive(false);
			}
		}
		currentPlayer = (currentPlayer+1)%vehicles.size();
		if ( vehicles.get(currentPlayer).isDestroyed() ) nextPlayer();

		System.out.println("Player "+vehicles.get(currentPlayer).getID()+" active");
		broadcastPositions();
		vehicles.get(currentPlayer).startMove();

		pcs.firePropertyChange("playerList", null, broadcastSockets);
	}
	
	public Point getStartPoint() {
		return track.getStartPoint();
	}

	public int getTile(int x, int y) {
		return track.getTile(x, y);
	}

	public int getTile(Point p) {
		return getTile((int)p.getX(), (int)p.getY());
	}
	
	public int getTile(Vehicle v) {
		return getTile(v.getPosition());
	}
	
	public List<Vehicle> getVehicles() {
		return vehicles;
	}
	
	public void broadcastPositions() {
		StringBuilder builder = new StringBuilder();
		builder.append("POS ");
		for (Vehicle vehicle : vehicles) {
			builder
				.append(vehicle.getID())
				.append(";")
				.append((int)vehicle.getPosition().getX())
				.append(",")
				.append((int)vehicle.getPosition().getY())
				.append("|");
		}
		builder.deleteCharAt(builder.length()-1);

		broadcastCommand(builder.toString());
	}
	
	public List<Point> getPositions() {
		ArrayList<Point> list = new ArrayList<Point>();
		for (Vehicle v : vehicles) {
			list.add(v.getPosition());
		}
		return list;
	}

	public void broadcastCommand(String command) {
		System.out.println("BROADCAST: "+command);
//		for (Vehicle vehicle : vehicles) {
//			vehicle.doCommand(command);
//		}
		for (ServerSocketHandler socket : broadcastSockets) {
			socket.sendCommand(command);
		}
	}

	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public void setNumberOfPlayers(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}

	public void updateReady() {
		if ( ready() ) {
			startGame();
		}
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		int oldStatus = this.status;
		this.status = status;
		pcs.firePropertyChange("status", oldStatus, this.status);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(propertyName, listener);
	}
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(propertyName, listener);
	}

	public void end(Vehicle vehicle) {
		setStatus(FINISHED);

		broadcastPositions();
		for ( Vehicle v : vehicles ) {
			if ( v == vehicle || v.isDestroyed() ) continue;
			v.doCommand("LOS Player "+vehicle.getID()+" has won");
		}

		broadcastCommand("CHT SERVER;Player "+vehicle.getID()+" has won");
	}
}
