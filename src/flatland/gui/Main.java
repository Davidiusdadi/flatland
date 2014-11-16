package flatland.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import flatland.simulation.Map;




class Main extends JFrame {
	public Main() throws IOException {
		super( "2D Eye" );
		BufferedImage map_image = ImageIO.read( new File( "map.bmp" ) );
		
		Map map = new Map( map_image );
		Component c = new CanvasExplorer( map );
		// JButton button = new JButton( "Trolled" );
		this.add( c );
		// this.add( button );
		this.setSize( map_image.getWidth(), map_image.getHeight() + 30 );

		BufferedImage cursorImg = new BufferedImage( 16, 16, BufferedImage.TYPE_INT_ARGB );
		// Create a new blank cursor.
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor( cursorImg, new Point( 0, 0 ), "blank cursor" );
		// Set the blank cursor to the JFrame.
		getContentPane().setCursor( blankCursor );

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		this.setMaximizedBounds( env.getMaximumWindowBounds() );
		this.setExtendedState( this.getExtendedState() | this.MAXIMIZED_BOTH );

	}

	public static void main( String[] args ) throws IOException {
		System.out.println( "Current working dir: " + new File( "." ).getCanonicalPath() );
		JFrame f = new Main();
		f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		f.setVisible( true );
	}
}


