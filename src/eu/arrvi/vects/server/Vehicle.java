package eu.arrvi.vects.server;

import eu.arrvi.vects.common.*;

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
	
	public Vehicle(TrackPoint pos) {
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
//		pts.removeAll(socketHandler.getGame().getPositions());
		return pts;
	}
	
	public void moveTo(TrackPoint point) throws GameException {
		if ( !active ) {
			throw new VehicleNotActiveException(this.getID());
		}
//		if ( !getPossibleMoves().contains(point) ) {
//			throw new IllegalMoveException(this.getID());
//		}
		speed = new SpeedVector(position, point);
		position = point;
		positionHistory.add(point);
		System.out.println("Moved "+getID()+" to: "+point);
	}

	private void destroyVehicle() {
		destroyed = true;
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public void setSocketHandler(ServerSocketHandler socketHandler) {
		this.socketHandler = socketHandler;
	}
	
	public void doCommand(String command) {
		socketHandler.write(command);
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
		return new VehiclePosition(getID(), getPosition());
	}
}
