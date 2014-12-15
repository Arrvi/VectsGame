package eu.arrvi.vects.server;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;

class Track {
	public final static int IN = 0xffffffff;
	public final static int OUT = 0xff000000;
	public final static int START = 0xff00ff00;
	public final static int FINISH = 0xffff0000;
	static int[] availTypes = {IN, OUT, START, FINISH};
	{
		Arrays.sort(availTypes);
	}
	public final static Map<Integer, String> colorNames = new HashMap<>();
	{
		colorNames.put(IN, "track");
		colorNames.put(OUT, "outside");
		colorNames.put(START, "start");
		colorNames.put(FINISH, "finish");
	}
	
	private File file;
	private int resolution;
	
	private int[][] track;
	
	private boolean canRace = false;
	
	private ArrayList<Point> start = new ArrayList<Point>();

	public Track(File image, int resolution) throws IOException {
		this.file = image;
		this.resolution = resolution;
		track = new int[resolution][resolution];
		generateTrack();
	}
	
	public String getTrackPath() {
		return file.getPath();
	}
	public int getResolution() {
		return resolution;
	}
	
	public int getTile(int x, int y) {
		if ( x < 0 || x >= resolution || y < 0 || y >= resolution) {
			if ( x < 0 ) x = 0;
			else x = resolution-1;
			if ( y < 0 ) y = 0;
			else y = resolution-1;
		}
		
		return track[x][y];
	}
	
	private static Random rand = new Random();
	private BufferedImage image;
	public Point getStartPoint() {
		return start.remove(rand.nextInt(start.size()));
	}
	
	public boolean canRace() {
		return canRace;
	}
	
	public BufferedImage getTrackImage(int pixelSize) {
		BufferedImage img = new BufferedImage(resolution*pixelSize, resolution*pixelSize, BufferedImage.TYPE_INT_ARGB);
		double ratio = (double)resolution*pixelSize/image.getWidth();
		for (int x=0; x<img.getWidth(); ++x) {
			for (int y=0; y<img.getHeight(); ++y) {
				int color = (int)(
						((double)track[x/pixelSize][y/pixelSize]*0.8)
						+
						((double)image.getRGB((int)(x/ratio), (int)(y/ratio))*0.2)
					);
				img.setRGB(x, y, color);
			}
		}
		return img;
	}
	
	private void generateTrack () throws IOException {
		image = ImageIO.read(file);

		double dx = (double)image.getWidth()/resolution;
		double dy = (double)image.getHeight()/resolution;

		int ends = 0;

		int halfPix = image.getWidth()/resolution/2;
		for (int x=0; x<resolution; ++x) {
            for(int y=0; y<resolution; ++y) {
                track[x][y] = approxType(image.getRGB((int)(x*dx+halfPix), (int)(y*dy+halfPix)));
                if (track[x][y] == START) start.add(new Point(x, y));
                else if (track[x][y] == FINISH) ends++;
            }
        }

		canRace = true;
		if ( start.size() < 2 ) {
            System.out.println("There are less than 2 start points. Race cannot be started");
            canRace = false;
        }
		if (ends < 1 ) {
            System.out.println("There is no finish line. Race cannot be started.");
            canRace = false;
        }
	}
	
	private static int approxType(int color) {
		int found;
		if((found = Arrays.binarySearch(availTypes, color)) < 0) {
			if ( found == -availTypes.length || found < -1 && color-availTypes[-2-found] < availTypes[-1-found]-color)
				return availTypes[-2-found];
			return availTypes[-1-found];
		}
		return color;
	}

	public static String[] getAvailableTracks() {
		File folder = new File("tracks");
		File[] files = folder.listFiles(imageFilter);
		ArrayList<String> tracks = new ArrayList<>();
		for(File file : files) {
			tracks.add(file.getPath());
		}
		String[] tracksArray = new String[tracks.size()];
		return tracks.toArray(tracksArray);
	}

	private static final FileFilter imageFilter = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return pathname.isFile() && pathname.canRead();
		}
	};
}
