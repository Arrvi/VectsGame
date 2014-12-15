package eu.arrvi.vects.server;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;

@Deprecated
public class OldVectsServer {
	
	static int previewResolution = 7;

	@SuppressWarnings("serial")
	public static void main(String[] args) throws IOException {
		Track track = new Track(new File("tracks/2.png"), 100);
		final Game game = new Game(track);
		game.setNumberOfPlayers(1);
		
		final BufferedImage tr = track.getTrackImage(previewResolution);
		JFrame preview = new JFrame("Track preview");
		preview.setSize(tr.getWidth(), tr.getHeight()); 
		final JPanel imagePane = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				g.drawImage(tr, 0, 0, null);
				int halfPix = previewResolution/2;
				for(Vehicle v:game.getVehicles()) {
					Point last=null;
					for(Point p : v.getHistory()) {
						g.setColor(Color.DARK_GRAY);
						
						g.fillRect(
							(int)p.getX()*previewResolution-1+halfPix, 
							(int)p.getY()*previewResolution-1+halfPix, 
						3, 3);
						
						if ( last == null ) {
							last = p;
							continue;
						}
						g.drawLine(
							(int)last.getX()*previewResolution+halfPix, 
							(int)last.getY()*previewResolution+halfPix, 
							(int)p.getX()*previewResolution+halfPix, 
							(int)p.getY()*previewResolution+halfPix
						);
						last = p;
					}
					
					if ( v.isActive() ) {
						g.setColor(Color.MAGENTA);
					}
					else {
						g.setColor(Color.CYAN);
					}
					for(Point p : v.getPossiblePoints()) {
						g.fillRect(
							(int)p.getX()*previewResolution-1+halfPix, 
							(int)p.getY()*previewResolution-1+halfPix, 
						3, 3);
					}
				}
			}
		};
		preview.add(imagePane);
		preview.setLocationRelativeTo(null);
		preview.setVisible(true);
		preview.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		(new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
				imagePane.repaint();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
				}
			}
		})).start();
	}

}
