package eu.arrvi.vects.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.swing.*;

/**
 * Button that represents possible move in game. Action should be performed in ActionListener.
 * Button should be removed after move (all of buttons).
 */
class TargetButton extends JButton {
	/**
	 * Point on board (game logic coordinate system)
	 */
	private Point point;

	/**
	 * Buffered button faces.
	 */
	private BufferedImage icon, hoverIcon, clickIcon;

	/**
	 * Creates button that represents given point on board that player can move to.
	 *
	 * @param point point of possible move
	 */
	public TargetButton(Point point) {
		super();
		setContentAreaFilled(false);
		this.point = point;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		// As we know new size - redraw icons
		generateIcons(width, height);
	}

	/**
	 * Generates icons for each normal, hover and active state and applies them to the button.\
	 *
	 * @param width width of icons
	 * @param height height of icons
	 */
	private void generateIcons(int width, int height) {
		icon = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		hoverIcon = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		clickIcon = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g;
		g = (Graphics2D)icon.getGraphics();
		g.setPaint(Color.RED.darker());
		g.draw(new Ellipse2D.Double(0, 0, width-1, height-1));

		g = (Graphics2D)hoverIcon.getGraphics();
		g.setPaint(Color.RED.darker());
		g.fill(new Ellipse2D.Double(0, 0, width - 1, height - 1));
		g.draw(new Ellipse2D.Double(0, 0, width - 1, height - 1));

		g = (Graphics2D)clickIcon.getGraphics();
		g.setPaint(Color.RED);
		g.fill(new Ellipse2D.Double(0, 0, width - 1, height - 1));
		g.draw(new Ellipse2D.Double(0, 0, width - 1, height - 1));

		setIcon(new ImageIcon(icon));
		setRolloverIcon(new ImageIcon(hoverIcon));
		setPressedIcon(new ImageIcon(clickIcon));
	}

	/**
	 * Returns point that is being represented by this button.
	 *
	 * @return point on board (game logic coordinates)
	 */
	public Point getPoint() {
		return point;
	}
}
