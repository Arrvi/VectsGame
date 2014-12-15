package eu.arrvi.vects.server;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

class Vector {
	int dx,dy;
	int maxSpeed = 10;
	int acc=2;
	
	public Vector(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	public Vector(Point a, Point b) {
		this((int)(b.getX()-a.getX()), (int)(b.getY()-a.getY()));
	}

	public int getDX() {
		return dx;
	}
	
	public int getDY() {
		return dy;
	}
	
	public List<Vector> getMoves() {
		ArrayList<Vector> list = new ArrayList<Vector>();
		
		for (int x=dx-acc; x<=dx+acc; ++x) {
			if (x > getMaxSpeed() || x < -getMaxSpeed() ) continue;
			for (int y=dy-acc; y<=dy+acc; ++y) {
				if (y > getMaxSpeed() || y < -getMaxSpeed()) continue;
				list.add(new Vector(x,y));
			}
		}
		
		return list;
	}
	
	@Override
	public String toString() {
		return "["+dx+", "+dy+"]";
	}


	private int getMaxSpeed() {
		return maxSpeed;
	}
}
