package eu.arrvi.vects.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpeedVector {
	int dx,dy;
	
	public SpeedVector(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	public SpeedVector(TrackPoint a, TrackPoint b) {
		this((int)(b.getX()-a.getX()), (int)(b.getY()-a.getY()));
	}

	public int getDX() {
		return dx;
	}
	
	public int getDY() {
		return dy;
	}
	
	public Set<SpeedVector> getMoveVectors(int maxSpeed, int acc) {
		Set<SpeedVector> set = new HashSet<>();
		
		for (int x=dx-acc; x<=dx+acc; ++x) {
			if (x > maxSpeed || x < -maxSpeed ) continue;
			for (int y=dy-acc; y<=dy+acc; ++y) {
				if (y > maxSpeed || y < -maxSpeed) continue;
				set.add(new SpeedVector(x, y));
			}
		}
		
		return set;
	}
	
	@Override
	public String toString() {
		return "["+dx+", "+dy+"]";
	}
}
