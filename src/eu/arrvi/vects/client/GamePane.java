package eu.arrvi.vects.client;

import eu.arrvi.vects.common.TrackPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class GamePane extends JLayeredPane implements ActionListener {
	/**
	 * Resolution of track. Track area will be divided on both edges by this number creating grid of move points.
	 *
	 * TODO #2 non-square image support
	 */
	private int resolution;

	/**
	 * List of current targets (buttons).
	 * Stored to be removed at the end of the move.
	 */
	private List<TargetButton> targets = new ArrayList<>();

	/**
	 * Scaling of track display
	 *
	 * TODO #8 resolution aware scaling, in-game zoom
	 */
	private double scale = 2.0;

	/**
	 * Component displaying track and trails
	 */
	private final TrackPane track;

	/**
	 * Panel for displaying target buttons
	 */
	private final JPanel targetPane;

	/**
	 * Client window reference for communication.
	 *
	 * TODO #1 Refactor to event-driven model - remove parent reference
	 */
	private ClientWindow contr;


	/**
	 * Creates game panel that displays track and allows to do moves. Before displaying track has to be set by calling
	 * setTrack method.
	 *
	 * TODO #1 Refactor to event-driven model - remove parent reference
	 *
	 * @param contr Parent reference for communication
	 */
	public GamePane(ClientWindow contr) {
		super();
		this.contr = contr;
		track = new TrackPane();
		track.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.RED));
		targetPane = new JPanel();
		targetPane.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLUE));
		targetPane.setLayout(null);
		
		this.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.GREEN));
		
		add(track, new Integer(1));
		add(targetPane, new Integer(0));
		
		setPreferredSize(new Dimension(500, 500));
		setLayout(null);
	}




	/**
	 * Sets track for display.
	 * @param path Image path of track displayed on game panel.
	 * @param resolution Resolution of track. Track area will be divided on both edges by this number creating grid of move points.
	 * @throws IOException if cannot read the image given in path param
	 */
	public void setTrack(String path, int resolution) throws IOException {
		this.resolution = resolution;
		track.setTrack(path, resolution);
		setPreferredSize(new Dimension((int) (track.getWidth() * scale), (int) (track.getHeight() * scale)));
		track.setSize(new Dimension((int) (track.getWidth() * scale), (int) (track.getHeight() * scale)));
		targetPane.setSize(new Dimension((int)(track.getWidth()*scale), (int)(track.getHeight()*scale)));
		invalidate();
		repaint();
	}
	
	/**
	 * Adds all vehicles' current positions. This does not take position history.
	 * Skips repetitive points.
	 *
	 * @param map current positions of all vehicles (vehicle id is map key)
	 */
	public void updatePositions(Map<Integer, Point> map) {
		track.updatePositions(map);
	}

	/**
	 * Transforms game coordinate for drawing. Returns center of the tile.
	 *
	 * FIXME casting chaos
	 * TODO #2 non-squre image support
	 *
	 * @param x game coordinate
	 * @return center of the tile in pixels
	 */
	public double _(double x) {
		return (x+0.5)*((double)track.getWidth()/resolution)*scale;
	}

	/**
	 * Transforms game coordinate for drawing. Returns beginning (left top corner) of the tile.
	 *
	 * FIXME casting chaos
	 * TODO #2 non-squre image support
	 *
	 * @param x game coordinate
	 * @return top left corner of the tile in pixels
	 */
	public double __(double x) {
		return (x)*((double)track.getWidth()/resolution)*scale;
	}

	/**
	 * Creates set of buttons at possible move target points.
	 *
	 * FIXME refator to actual set (not list)
	 *
	 * @param pts set of points to make target buttons from
	 */
	public void setTargets(List<Point> pts) {
		if ( track.getHighlightPlayerId() == -1 ) track.setHighlightPlayerId(contr.getID());
		for (Point point : pts) {
			TargetButton target = new TargetButton(point);

			// FIXME move bounds to constructor and simplify conversion
			target.setBounds((int)__(point.getX()), (int)__(point.getY()), (int)(track.getWidth()*scale/resolution), (int)(track.getWidth()*scale/resolution));
			target.addActionListener(this);
			targets.add(target);
			targetPane.add(target);
		}
		invalidate();
		repaint();
	}

	/**
	 * Removes all target buttons and performs a move.
	 *
	 * TODO #1 Refactor to event-driven model - remove parent reference
	 *
	 * @param evt action event (to specify the sources)
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		for (TargetButton targetButton : targets) {
			targetPane.remove(targetButton);
		}
		targets.clear();
		contr.moveTo(((TargetButton)evt.getSource()).getPoint());
	}

}
