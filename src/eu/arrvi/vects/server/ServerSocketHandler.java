package eu.arrvi.vects.server;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

class ServerSocketHandler implements Runnable {
	private Socket socket;
	private BufferedWriter writer;
	private Game game;
	private Vehicle vehicle;

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
			
			String command;
			
			while((command = reader.readLine()) != null) {
				System.out.println(getPort()+": "+command);
				if ( runCommand(command) ) {
					game.nextPlayer();
				}
				else {
					System.out.println("continue");
				}
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

	public Point getPosition() {
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
}
