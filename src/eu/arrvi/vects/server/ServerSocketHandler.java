package eu.arrvi.vects.server;

import eu.arrvi.vects.common.Command;
import eu.arrvi.vects.common.TrackPoint;
import eu.arrvi.vects.events.CommandEvent;
import eu.arrvi.vects.events.CommandEventListener;
import eu.arrvi.vects.events.CommandEventSupport;

import java.awt.*;
import java.io.*;
import java.net.Socket;

class ServerSocketHandler implements Runnable, CommandEventListener {
	private Socket socket;
	private BufferedWriter writer;
	private Game game;
	private Vehicle vehicle;
	
	private CommandEventSupport ces = new CommandEventSupport(this);

	public ServerSocketHandler(Socket socket, Game game) {
		this.socket = socket;
		this.game = game;
		(new Thread(this)).start();
		try {
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
		game.addBroadcast(this);
	}
	
	public int getPort() {
		return socket.getPort();
	}
	

	public void sendCommand(String command) {
		try {
			writer.write(command+"\r\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Game getGame() {
		return game;
	}
	

	public boolean runCommand(String command) {
		if (command.length() < 3) {
			chatMessage(command);
			return false;
		}
		switch (command.substring(0, 3).toUpperCase()) {
		
		/*
		 * NOP - Noop / i'm bored
		 * Do nothing
		 */
		case "NOP":
			return false;
			
		/*
		 * ECH - Echo / are you still there?
		 * Simple response
		 */
		case "ECH":
			vehicle.doCommand("ACK "+command.substring(4));
			return false;
			
		/*
		 * RDY - Ready
		 * Declare ready to race
		 */
		case "RDY":
			createVehicle();
			vehicle.setReady(true);
			game.updateReady();
			return false;
		
		/*
		 * DEN - Deny / nope
		 * Not used
		 */
		case "DEN":
			return false;
			
		/*
		 * NOT - Not / changed my mind
		 * Declare not ready to race
		 */
		case "NOT":
			vehicle.setReady(false);
			return false;
		
		/*
		 * BYE - Bye / Quit / I'm outa here
		 * Close connection to server 
		 */
		case "BYE":
			sendCommand("BYE");
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			game.removeVehicle(vehicle);
			return true;
		
		/*
		 * MOV - Move
		 * Do your move
		 */
		case "MOV":
			String[] pos = command.substring(4).split(",");
			return vehicle.moveTo(new Point(Integer.parseInt(pos[0]), Integer.parseInt(pos[1])));
		
		/*
		 * CHT - Chat
		 * Send message
		 */
		case "CHT":
			command = command.substring(4);
		default:
			chatMessage(command);
			return false;
		}
	}

	private void createVehicle() {
		this.vehicle = new Vehicle(game.getStartPoint());
		vehicle.setSocketHandler(this);
		game.addVehicle(vehicle);
	}

	private void chatMessage(String command) {
		game.broadcastCommand("CHT "+getPort()+";"+command);
	}

	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String commandString;
			
			while((commandString = reader.readLine()) != null) {
				System.out.println(getPort()+": "+commandString);
				Command command = Command.getCommandFromString(commandString);
				
				if ( command == null ) continue;
				
				ces.fireCommand(command);
			}
			
			System.out.println("connection closed...");	
		} catch (IOException e) {
			System.out.println("connection closed with exception...");
			e.printStackTrace();
		} finally {
			game.removeBroadcast(this);
			if (vehicle != null)
				game.removeVehicle(vehicle);
		}
	}

	public String getStatusString() {
		if ( vehicle == null ) {
			return "Broadcast only";
		}
		if ( vehicle.isDestroyed() ) {
			return "Destroyed";
		}
		if ( vehicle.isActive() ) {
			return "Active";
		}
		if ( vehicle.isReady() ) {
			return "Ready";
		}
		else {
			return "Not ready";
		}
	}

	public TrackPoint getPosition() {
		if ( vehicle == null ) return null;
		return vehicle.getPosition();
	}

	public String getTile()  {
		if ( vehicle == null ) return null;
		return Track.colorNames.get(vehicle.getTile());
	}

	public String getSpeedString() {
		if (vehicle == null ) return null;
		return vehicle.getSpeed().toString();
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

	@Override
	public void commandReceived(CommandEvent event) {
		
	}

	public void sendCommand(Command command) {
		
	}
}
