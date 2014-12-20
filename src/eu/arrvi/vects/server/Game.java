package eu.arrvi.vects.server;

import eu.arrvi.vects.common.*;
import eu.arrvi.vects.events.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.*;

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
	private final CommandEventSupport ces = new CommandEventSupport(this);

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
//	private List<Vehicle> vehicles = new ArrayList<>();
	private Map<ServerSocketHandler, Vehicle> vehicles = new HashMap<>();

    /**
     * List of clients connected to server.
     */
	private List<ServerSocketHandler> broadcastSockets = new ArrayList<>();
	
	private List<ServerSocketHandler> clients = new ArrayList<>();

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
	private int status = 0;

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
	public void addVehicle(ServerSocketHandler socket, Vehicle v) {
		if ( vehicles.size() >= numberOfPlayers ) {
			throw new GameFullException(v.getID());
		}
		vehicles.put(socket, v);
		inGame++;

		pcs.firePropertyChange("playerList", null, broadcastSockets);
	}

    /**
     * Removes vehicle from the game.
     *
     * @param v vehicle to be removed.
     */
	@Deprecated
	public void removeVehicle(Vehicle v) {
		// vehicles.remove(v); // FIXME - wtf?
		pcs.firePropertyChange("playerList", null, broadcastSockets);
	}
	
	@Deprecated
	public void addBroadcast(ServerSocketHandler socket) {
		socket.write("ACK " + socket.getPort());
		socket.write("TRK " + track.getTrackPath() + ";" + track.getResolution());
		broadcastSockets.add(socket);
		pcs.firePropertyChange("playerList", null, broadcastSockets);
	}

	@Deprecated
	public void removeBroadcast(ServerSocketHandler socket) {
		broadcastSockets.remove(socket);
		pcs.firePropertyChange("playerList", null, broadcastSockets);
	}
	
	public void addClient(ServerSocketHandler socket) {
		clients.add(socket);
		ces.addCommandEventListener(socket.getPort(), socket);
		socket.addCommandEventListener(commandHandler);
		pcs.firePropertyChange("connectedSockets", null, clients);
	}
	
	public void removeClient(ServerSocketHandler socket) {
		clients.remove(socket);
		ces.removeCommandEventListener(socket.getPort(), socket);
		socket.removeCommandEventListener(commandHandler);
		pcs.firePropertyChange("connectedSockets", null, clients);
	}

	public boolean ready() {
		int actives = 0;
		for (Vehicle vehicle : vehicles.values()) {
			if ( vehicle.isReady() ) actives++;
		}
		System.out.println(actives+" of "+numberOfPlayers+" active");
		
		Command command = new Command("CHT", new ChatMessage(ChatMessage.SERVER, actives+" of "+numberOfPlayers+" ready"));
		ces.fireCommand(command);

		return actives >= numberOfPlayers;

	}

	private void shufflePlayers() {
		Collections.shuffle(clients);
	}

	private void startGame() {
		System.out.println("Starting game");
		shufflePlayers();
		setStatus(STARTED);
		nextPlayer();
	}

	public void nextPlayer() {
		Vehicle currentVehicle;
		if ( currentPlayer != -1 ) {
			if (status != STARTED) {
				return;
			}
			if (vehicles.size() == 0) {
				return;
			}
			currentVehicle = vehicles.get(clients.get(currentPlayer));
			if (currentVehicle.isActive() && currentVehicle.isDestroyed()) {
				Command command = new Command(
						"CHT", 
						new ChatMessage(ChatMessage.SERVER, "Player " + currentVehicle.getID() + " crashed")
				);
				ces.fireCommand(command);
				inGame--;
			}
			if (numberOfPlayers >= 2 && inGame <= 1) {
				for (Vehicle v : vehicles.values()) {
					if (!v.isDestroyed()) {
						Command command = new Command(v.getID(), "WIN", new SimpleInfo("All other players crashed"));
						ces.fireCommand(command);
						end(v);
						return;
					}
				}
				end(null);
				return;
			}
			for (Vehicle vehicle : vehicles.values()) {
				vehicle.setActive(false);
			}
		}
		currentPlayer = (currentPlayer+1)%vehicles.size();
		currentVehicle = vehicles.get(clients.get(currentPlayer));
		if ( currentVehicle.isDestroyed() ) nextPlayer();

		System.out.println("Player "+currentVehicle.getID()+" active");
		broadcastPositions();
		currentVehicle.startMove();

		pcs.firePropertyChange("playerList", null, broadcastSockets);
	}
	
	private void moveVehicle() {
		
	}
	
	public TrackPoint getStartPoint() {
		return track.getStartPoint();
	}

	public int getTile(int x, int y) {
		return track.getTile(x, y);
	}

	public int getTile(TrackPoint p) {
		return getTile((int)p.getX(), (int)p.getY());
	}
	
	public int getTile(Vehicle v) {
		return getTile(v.getPosition());
	}
	
	public List<Vehicle> getVehicles() {
		return vehicles;
	}
	
	public void broadcastPositions() {
		Command positionsCommand = new Command("POS");
		for (Vehicle vehicle : vehicles) {
			positionsCommand.addParam(vehicle.getVehiclePosition());
		}
		ces.fireCommand(positionsCommand);
	}
	
	public Set<TrackPoint> getPositions() {
		Set<TrackPoint> pointSet = new HashSet<>();
		for (Vehicle v : vehicles) {
			pointSet.add(v.getPosition());
		}
		return pointSet;
	}

	@Deprecated
	public void broadcastCommand(String command) {
		System.out.println("BROADCAST: "+command);
//		for (Vehicle vehicle : vehicles) {
//			vehicle.doCommand(command);
//		}
		for (ServerSocketHandler socket : broadcastSockets) {
			socket.write(command);
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

	public void addCommandEventListener(CommandEventListener listener) {
		ces.addCommandEventListener(listener);
	}

	public void addCommandEventListener(String command, CommandEventListener listener) {
		ces.addCommandEventListener(command, listener);
	}

	public void removeCommandEventListener(CommandEventListener listener) {
		ces.removeCommandEventListener(listener);
	}

	public void removeCommandEventListener(String command, CommandEventListener listener) {
		ces.removeCommandEventListener(command, listener);
	}

	public void end(Vehicle vehicle) {
		setStatus(FINISHED);

		CommandParameter info;
		if (vehicle != null) {
			info = new SimpleInfo("Player "+vehicle.getID()+" has won");
		}
		else {
			info = new SimpleInfo("All players have crashed");
		}

		broadcastPositions();
		for ( Vehicle v : vehicles ) {
			if ( v == vehicle || v.isDestroyed() ) continue;
			ces.fireCommand(new Command(v.getID(), "LOS", info));
		}
	}
	
	
	private CommandEventListener commandHandler = new AdvancedCommandEventAdapter() {
		@Override
		protected void unknownCommand(CommandEvent command) {
			System.err.println("Unimplemented command: "+command.toString());
		}
		
		@BindCommand("ECH")
		public void receiveEcho(CommandEvent evt) {
			ces.fireCommand(new Command(
					((ServerSocketHandler)evt.getSource()).getPort(),
					"ACK",
					evt.getCommand().getParams()
			));
		}
		
		@BindCommand("RDY")
		public void receiveReady(CommandEvent evt) {
			try {
				addVehicle(new Vehicle(getStartPoint()));
			}
			catch (GameFullException e){
				ces.fireCommand(e.getDenialCommand());
			}
		}
		
		@BindCommand("BYE")
		public void receiveBye(CommandEvent evt) {
			try {
				((ServerSocketHandler) evt.getSource()).close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@BindCommand("MOV")
		public void receiveMove(CommandEvent evt) {
			
		}
	};

}
