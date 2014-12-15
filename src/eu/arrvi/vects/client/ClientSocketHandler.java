package eu.arrvi.vects.client;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Technically Vects protocol socket. Parses all incoming commands and allows sending other to server. It only
 * communicates with server and has no knowledge of other clients.
 */
class ClientSocketHandler implements Runnable {
	/**
	 * Game reference for command calls.
	 *
	 * TODO #1 Refactor to event-driven model - remove game reference
	 */
	private ClientWindow game;

	/**
	 * Socket object for communication via TCP
	 */
	private Socket socket;

	/**
	 * Socket stream writer
	 */
	private BufferedWriter writer;

	/**
	 * Socket stream reader
	 */
	private BufferedReader reader;

	/**
	 * Host address given to socket to connect.
	 */
	private String host;

	/**
	 * Host port given to socket to connect
	 */
	private int port;

	/**
	 * Creates socket support object for Vects protocol. This does not connect nor create socket until run in separate
	 * thread. Due to blocking nature of stream readers it has to be done in separate thread.
	 *
	 * TODO #1 Refactor to event-driven model - remove game reference
	 *
	 * @param game game reference for passing commands from server
	 * @param host host address to connect to
	 * @param port host port to connect to
	 */
	public ClientSocketHandler(ClientWindow game, String host, int port) {
		this.game = game; // TODO #1
		this.host = host;
		this.port = port;
	}

	/**
	 * Creates and listens to socket. Exits on EOF or exception and sets connected states.
	 *
	 * TODO #1 Refactor to event-driven model - remove game reference (1) and direct chat invocation (2)
	 */
	@Override
	public void run() {
		game.chatMessage("<html><font color=#999999>Connecting...");
		try {
			// Prepare socket
			socket = new Socket(host, port);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			

			// Listen in loop
			String command;
			while ((command = reader.readLine()) != null) {
				doCommand(command);
			}
		}
		catch (ConnectException e) {
			e.printStackTrace();
			chatMessage("<html><font color='red'><b>Cannot establish connection:</b> "+e.getMessage()+"</font>"); // TODO #1.2
		}
		catch (SocketException e) {
			e.printStackTrace();
			chatMessage("<html><font color='red'><b>Connection lost:</b> "+e.getMessage()+"</font>"); // TODO #1.2
		}
		catch (IOException e) {
			e.printStackTrace();
			chatMessage("<html><font color='red'><b>Connection error:</b> "+e.getMessage()+"</font>"); // TODO #1.2
		}
		finally {
			game.setConnected(false); // TODO #1.1
		}
	}

	/**
	 * Parse and execute incoming command. Available commands are:
	 *
	 * `NOP` - do nothing (not used; potentially could maintain connection)
	 * `ACK [info]` - assume no error (used as confirmation of client's command)
	 * `TRK path;res` - track information
	 *     `path` - path to track file (image)
	 *     `res` - track resolution / density of points on each side of an image
	 *     TODO #2 non square image support
	 * `POS id;x,y|id2;x,y|...` - current positions of all vehicles
	 *     TODO #3 position history support - add 'turn' parameter
	 * `TAR x,y|x,y|...` - possible move targets / your turn
	 * `DEN [reason]` - denial (used as client's command error information)
	 * `WIN info` and `LOS info` - information about win or loss (win means end of a game)
	 *     TODO #4 disconnecting after end of a game
	 *     TODO #5 game after winning support (2nd place etc.)
	 * `ECH` - echo (echo protocol; testing only)
	 * `CHT id;msg` - chat message from specific author (system or other player)
	 *
	 *
	 * TODO #1 Refactor to event-driven model - remove game reference (1) and direct chat invocation (2)
	 * TODO #6 Reflection handling!
	 *
	 * @param command command to be executed
	 */
	private void doCommand(String command) {
		// Debug
		System.out.println("[SERVER] "+command);

		// Command has to be at least 3 characters long
		if ( command.length() < 3 ) {
			chatMessage(command); // TODO #1.2
			return;
		}
		switch (command.substring(0, 3).toUpperCase()) {
		/*
		 * NOP - Noop
		 * Do nothing
		 */
		case "NOP":
			return;
		/*
		 * ACK - Acknowledge
		 * Confirm command
		 */
		case "ACK":
			game.serverResponse(true, command.substring(4));
			return;
			
		/*
		 * TRK - Track path
		 */
		case "TRK":
			String[] trk = command.substring(4).split(";");
			if ( trk.length != 2 ) { 
				sendCommand("DEN Syntax error");
				return;
			}
			try {
				game.setTrack(trk[0], Integer.parseInt(trk[1])); // TODO #2
			} catch (IOException e) {
				sendCommand("DEN Cannot open file");
			} catch (NumberFormatException e) {
				sendCommand("DEN Wrong resolution format");
			}
			return;
		
		/*
		 * POS - Position(s)
		 * Positions of all vehicles
		 */
		case "POS": // TODO #3
			String[] pos = command.substring(4).split("\\|");
			HashMap<Integer, Point> map = new HashMap<Integer, Point>();
			for (String p : pos) {
				String[] rec = p.split("[;,]");
				map.put(new Integer(rec[0]), new Point(Integer.parseInt(rec[1]), Integer.parseInt(rec[2])));
			}
			game.updatePositions(map); // TODO #1.1
			return;
			
		/*
		 * TAR - Target(s)
		 * Possible moves, "your turn" command
		 */
		case "TAR":
			String[] tar = command.substring(4).split("\\|");
			ArrayList<Point> pts = new ArrayList<Point>();
			for (String p : tar) {
				String[] pt = p.split(",");
				pts.add(new Point(Integer.parseInt(pt[0]), Integer.parseInt(pt[1])));
			}
			game.setTargets(pts); // TODO #1.1
			return;
			
		/*
		 * DEN - Deny
		 * Command not accepted
		 */
		case "DEN":
			return;
			
		/*
		 * WIN - Win / Victory
		 * Successful end of race
		 */
		case "WIN":
			// TODO #4
			// TODO #5
			command = command.substring(4);
			chatMessage(String.format("<html><font color=#009900 size=6>You win (%s)", command)); // TODO #1.2
			return;

			
		/*
		 * LOS - Loss
		 * Unsuccessful end of the race
		 */
		case "LOS":
			command = command.substring(4);
			chatMessage(String.format("<html><font color=#990000 size=6>You lose (%s)", command)); // TODO #1.2
			return;
			
		/*
		 * ECH - Echo
		 * Response to server
		 */
		case "ECH":
			
		/*
		 * CHT - Chat
		 * Message
		 */
		case "CHT":
			String[] msg = command.substring(4).split(";");
			if ( msg.length != 2 ) { 
				return;
			}
			chatMessage(msg[0], msg[1]); // TODO #1.2
			return;
		default:
			chatMessage(command); // TODO #1.2
		}
	}

	private void chatMessage(String message) {
		game.chatMessage(message); // TODO #1
	}
	private void chatMessage(String author, String message) {
		game.chatMessage(author, message); // TODO #1
	}

	public void sendCommand(String command) {
		// Debug
		System.out.println(command);

		try {
			writer.write(command+"\r\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getPort() {
		return socket.getLocalPort();
	}

}
