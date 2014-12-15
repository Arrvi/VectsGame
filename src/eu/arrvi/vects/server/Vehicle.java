package eu.arrvi.vects.server;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

class Vehicle {
	private Vector speed;
	private Point position;
	private ServerSocketHandler socketHandler;
	private List<Point> positionHistory = new ArrayList<Point>();
	private boolean ready =false;
	private boolean active=false;
	private boolean destroyed = false;
	
	public Vehicle(Point pos) {
		position = pos;
		speed = new Vector(0,0);
		positionHistory.add(pos);
	}
	
	public List<Point> getPossiblePoints() {
		List<Vector> vec = speed.getMoves();
		List<Point> pts = new ArrayList<Point>();
		for (Vector v : vec) {
			Point pt = (Point) position.clone();
			pt.translate(v.getDX(), v.getDY());
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
		if ( getPossiblePoints().contains(point) ) {
			speed = new Vector(position, point);
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

	public List<Point> getHistory() {
		return positionHistory;
	}

	public Point getPosition() {
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
		// sendCommand("POS "+(int)this.getPosition().getX()+","+(int)this.getPosition().getY()+","+game.getTile(vehicle));
		
		List<Point> points = this.getPossiblePoints();
		StringBuilder builder = new StringBuilder();
		builder.append("TAR ");
		for (Point point : points) {
			builder
				.append((int)point.getX())
				.append(',')
				.append((int)point.getY())
				.append('|');
		}
		builder.deleteCharAt(builder.length()-1);
		socketHandler.sendCommand(builder.toString());
	}

	public boolean isActive() {
		return active;
	}

	public Vector getSpeed() {
		return speed;
	}
}
