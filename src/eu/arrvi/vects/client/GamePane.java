package eu.arrvi.vects.client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

class GamePane extends JPanel implements ActionListener {
	/**
	 * Image of track displayed on game panel.
	 */
	private BufferedImage track;

	/**
	 * Resolution of track. Track area will be divided on both edges by this number creating grid of move points.
	 *
	 * TODO #2 non-square image support
	 */
	private int resolution;

	/**
	 * History of positions of all vehicles.
	 */
	private Map<Integer, List<Point>> positions = new HashMap<Integer, List<Point>>();

	/**
	 * List of current targets (buttons).
	 * Stored to be removed at the end of the move.
	 */
	private List<TargetButton> targets = new ArrayList<TargetButton>();

	/**
	 * Client window reference for communication.
	 *
	 * TODO #1 Refactor to event-driven model - remove parent reference
	 */
	private ClientWindow contr;

	/**
	 * Solid stroke for drawing points and this player's trail
	 */
	private final static Stroke solidStroke = new BasicStroke();
	/**
	 * Dashed stroke for drawing other players trails
	 */
	private final static Stroke dashedStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2,2}, 0);

	/**
	 * Scaling of track display
	 *
	 * TODO #8 resolution aware scaling, in-game zoom
	 */
	private double scale = 2.0;




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
		setPreferredSize(new Dimension(500,500));
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
		File file = new File(path);
		track = ImageIO.read(file);
		setPreferredSize(new Dimension((int)(track.getWidth()*scale), (int)(track.getHeight()*scale)));
	}

	/**
	 * Custom painting of component. Paints an image and then all trails.
	 *
	 * TODO #9
	 * @param g graphics instance to be drawn on
	 */
	@Override
	protected void paintComponent(Graphics g) {
		if (track == null) {
			return;
		}

		Graphics2D g2 = (Graphics2D)g;

		// Draw track image
		g2.drawImage(track, 0, 0, (int)(track.getWidth()*scale), (int)(track.getHeight()*scale), 0, 0, track.getWidth(), track.getHeight(), null);

		// Turn on anti-aliasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (Integer i : positions.keySet()) {
            Point last = null;

			// Draw this player's trail
            if ( i.intValue() == contr.getID()) {
                g2.setPaint(Color.GREEN.darker());
                g2.setStroke(solidStroke);

				// Draw trail
                for (Point p : positions.get(i)) {
					// Draw point
                    g2.draw(new Rectangle2D.Double(_(p.getX())-1, _(p.getY())-1, 3.0, 3.0));

					// Draw line
                    if ( last != null ) {
                        g2.draw(new Line2D.Double(_(last.getX()), _(last.getY()), _(p.getX()), _(p.getY())));
                    }
                    last = p;
                }
            }
			// Draw other player's trail
            else {
                g2.setPaint(Color.lightGray);

                for (Point p : positions.get(i)) {
					// Draw point
                    g2.setStroke(solidStroke);
                    g2.draw(new Rectangle2D.Double(_(p.getX())-1, _(p.getY())-1, 3.0, 3.0));

					// Draw line
                    if ( last != null ) {
                        g2.setStroke(dashedStroke);
                        g2.draw(new Line2D.Double(_(last.getX()), _(last.getY()), _(p.getX()), _(p.getY())));
                    }
                    last = p;
                }
            }
        }
	}

	/**
	 * Adds all vehicles' current positions. This does not take position history.
	 * Skips repetitive points.
	 *
	 * @param map current positions of all vehicles (vehicle id is map key)
	 */
	public void updatePositions(Map<Integer, Point> map) {
		for( Integer i : map.keySet() ) {
			// Create history for new id
			if ( !positions.containsKey(i) ) {
				positions.put(i, new ArrayList<Point>());
			}

			// Skip repeats
			if ( !positions.get(i).get(positions.get(i).size() - 1).equals(map.get(i))) {
				positions.get(i).add(map.get(i));
			}
		}
		repaint();
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
		for (Point point : pts) {
			TargetButton target = new TargetButton(point);

			// FIXME move bounds to constructor and simplify conversion
			target.setBounds((int)__(point.getX()), (int)__(point.getY()), (int)(track.getWidth()*scale/resolution), (int)(track.getWidth()*scale/resolution));
			target.addActionListener(this);
			targets.add(target);
			add(target);
		}
		invalidate();
		repaint();
	}

	/**
	 * Removes all target buttons and performs a move.
	 *
	 * TODO #1 Refactor to event-driven model - remove parent reference
	 *
	 * @param evt
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		for (TargetButton targetButton : targets) {
			remove(targetButton);
		}
		targets.clear();
		contr.moveTo(((TargetButton)evt.getSource()).getPoint());
	}

}
