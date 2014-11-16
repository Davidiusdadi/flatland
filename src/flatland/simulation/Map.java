package flatland.simulation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Map {

	private BufferedImage img;
	private List<Actor> actors = new ArrayList<Actor>();

	public Map( BufferedImage img ) {
		this.img = img;
	}

	public Color getColor( double x, double y ) {
		return new Color( rgb( x, y ) );
	}

	public int rgb( double x, double y ) {
		return img.getRGB( (int) ( x ), (int) ( y ) );
	}

	public int getOriginX() {
		return 1;
	}

	public int getOriginY() {
		return 1;
	}

	public int getWidth() {
		return img.getWidth() - 1;
	}

	public int getHeight() {
		return img.getHeight() - 1;
	}

	public BufferedImage getImage() {
		return img;
	}

	public List<Actor> getActors() {
		return actors;
	}
}
