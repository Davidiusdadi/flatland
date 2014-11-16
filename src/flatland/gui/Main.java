package flatland.gui;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import flatland.simulation.Map;

class Main extends JFrame {
	public Main() throws IOException {
		super( "flatland first person viewer  use w, a, s,d and esc" );
		
		Map map = new Map( ImageIO.read( new File( "map.bmp" ) ) );
		this.add(  new CanvasExplorer( map ) );		

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		this.setMaximizedBounds( env.getMaximumWindowBounds() );
		this.setExtendedState( this.getExtendedState() | this.MAXIMIZED_BOTH );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	}

	public static void main( String[] args ) throws IOException {
		new Main().setVisible( true );
	}
}


