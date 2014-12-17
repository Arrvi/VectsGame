package eu.arrvi.vects.server;

import eu.arrvi.vects.common.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Vehicle {
	private SpeedVector speed;
	private TrackPoint position;
	private ServerSocketHandler socketHandler;
	private List<TrackPoint> positionHistory = new ArrayList<>();
	private boolean ready =false;
	private boolean active=false;
	private boolean destroyed = false;
	
	private int maxSpeed = 10;
	private int acc = 1;
	
	public Vehicle(Point pos) {
		position = pos;
		speed = new SpeedVector(0,0);
		positionHistory.add(pos);
	}
	
	public Set<CommandParameter> getPossibleMoves() {
		Set<SpeedVector> vec = speed.getMoveVectors(maxSpeed, acc);
		Set<CommandParameter> pts = new HashSet<>();
		for (SpeedVector v : vec) {
			TrackPoint pt = new TrackPoint(position);
			pt.translate(v);
			pts.add(pt);
		}
		pts.removeAll(socketHandler.getGame().getPositions());
		return pts;
	}
	
	public boolean moveTo(Point point) {
		if ( !active ) {
			doCommand("DEN Not your turn");
			return false;
		}
		if ( getPossibleMoves().contains(point) ) {
			speed = new SpeedVector(position, point);
			position = point;
			positionHistory.add(point);
			System.out.println("Moved "+getID()+" to: "+point);
			
			if ( getTile() == Track.OUT ) {
				destroyVehicle();
				doCommand("LOS You have crashed");
			}
			else if ( getTile() == Track.FINISH ) {
				win();
				doCommand("WIN You have finished first");
			}
			else {
				doCommand("ACK "+(int)point.getX()+","+(int)point.getY());
			}
			
			return true;
		}
		doCommand("DEN Illegal move");
		return false;
	}

	private void win() {
		socketHandler.getGame().end(this);
	}

	private void destroyVehicle() {
		destroyed = true;
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public int getTile() {
		return socketHandler.getGame().getTile(position);
	}

	public void setSocketHandler(ServerSocketHandler socketHandler) {
		this.socketHandler = socketHandler;
	}
	
	public void doCommand(String command) {
		socketHandler.sendCommand(command);
	}

	public List<TrackPoint> getHistory() {
		return positionHistory;
	}

	public TrackPoint getPosition() {
		return position;
	}

	public boolean isReady() {
		return ready;
	}
	public void setReady(boolean ready) {
		this.ready =ready;
	}
	
	public int getID() {
		return socketHandler.getPort();
	}

	public void startMove() {
		active = true;
		sendPoints();
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	

	private void sendPoints() {
		Command command = new Command(socketHandler.getPort(), "POS", this.getPossibleMoves());
		socketHandler.sendCommand(command);
	}

	public boolean isActive() {
		return active;
	}

	public SpeedVector getSpeed() {
		return speed;
	}

	public VehiclePosition getVehiclePosition() {
		
	}
}
