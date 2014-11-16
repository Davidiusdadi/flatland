package flatland.gui;
import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Robot;
import java.awt.event.FocusAdapter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferStrategy;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import flatland.simulation.Actor;
import flatland.simulation.Map;
import flatland.simulation.UserActor;
import flatland.simulation.Vision;
import flatland.simulation.Vision.RayResult;

public class CanvasExplorer extends Canvas implements Runnable {

	private UserActor actor;

	public class MouseControl extends MouseAdapter {

		private Robot robo;

		private int mouse_x;
		private int mouse_y;

		private boolean entered = false;
		private boolean rest = false;

		public MouseControl() {
			try {
				robo = new Robot();
			} catch ( AWTException e ) {
				throw new RuntimeException( e );
			}
		}

		public void mouseEntered( java.awt.event.MouseEvent e ) {
			mouse_x = e.getX();
			mouse_y = e.getY();
			entered = true;
		}

		public void mouseExited( java.awt.event.MouseEvent e ) {
			entered = false;
		}

		public void mouseMoved( java.awt.event.MouseEvent e ) {
			mouse_y = e.getY();

			int center_x = getLocationOnScreen().x + getWidth() / 2;
			// int center_y = getLocationOnScreen().y + getHeight() / 2;

			int new_x = e.getX();

			if( new_x == center_x || rest ) {
				mouse_x = new_x;
				rest = false;
				return;
			}

			int dis = new_x - mouse_x;
			actor.angle += dis * Math.PI / 360;
			mouse_x = new_x;

			if( !showmap ) {
				rest = true;
				robo.mouseMove( center_x, e.getYOnScreen() );
			}

		}
	}

	public class UserInput extends KeyAdapter {
		private Set<Character> pressed = new HashSet<Character>();
		boolean shift_down = false;
		boolean control_down = false;

		public UserInput() {
		}

		public void keyPressed( java.awt.event.KeyEvent e ) {
			pressed.add( e.getKeyChar() );

			switch ( e.getKeyCode() ) {
				case KeyEvent.VK_ESCAPE:
					System.exit( 1 );
					break;
				case KeyEvent.VK_SHIFT:
					shift_down = true;
					break;
				case KeyEvent.VK_CONTROL:
					control_down = true;
					break;
				default :
					break;
			}

			switch ( e.getKeyChar() ) {
				case 'm':
					showmap = true;
					break;
				case '+':
					actor.distinct_distances++;
					System.out.println( "distinct_distances:" + actor.distinct_distances );
					break;
				case '-':
					if( actor.distinct_distances > 1 )
						actor.distinct_distances--;
					break;
				default :
					break;
			}

		}

		public void keyReleased( java.awt.event.KeyEvent e ) {
			pressed.remove( e.getKeyChar() );

			switch ( e.getKeyCode() ) {
				case KeyEvent.VK_SHIFT:
					shift_down = false;
					break;
				case KeyEvent.VK_CONTROL:
					control_down = false;
					break;
				default :
					break;
			}

			switch ( e.getKeyChar() ) {
				case 'm':
					showmap = false;
					break;
				default :
					break;
			}
		}

		public void procees() {
			// System.out.println(pressed);
			double ang;
			double speed = shift_down ? pos_speed * 4 : pos_speed;
			speed = control_down ? pos_speed / 4 : speed;

			// System.out.println( pos_speed );W
			for( char c : pressed ) {
				switch ( c ) {
					case 'q':
						actor.angle -= actor.rotate_step;
						break;
					case 'e':
						actor.angle += actor.rotate_step;
						break;
					case 'w':
						actor.step( Math.cos( actor.angle ) * speed, Math.sin( actor.angle ) * speed, map );
						break;
					case 's':
						ang = actor.angle - Math.PI;
						actor.step( Math.cos( ang ) * speed, Math.sin( ang ) * speed, map );
						break;
					case 'a':
						ang = actor.angle - Math.PI / 2;
						actor.step( Math.cos( ang ) * speed, Math.sin( ang ) * speed, map );
						break;
					case 'd':
						ang = actor.angle + Math.PI / 2;
						actor.step( Math.cos( ang ) * speed, Math.sin( ang ) * speed, map );
						break;
					default :
						break;
				}
			}
		}
	}

	private double pos_speed;

	private boolean showmap = false;

	private UserInput userinput;
	private Thread ownThread;
	private MouseControl mouseinput;

	private Map map;
	private Vision vision;

	CanvasExplorer( Map map ) {
		this.actor = new UserActor();
		this.map = map;
		vision = new Vision();

		pos_speed = 1;

		actor.pos_x = map.getWidth() / 2;
		actor.pos_y = map.getHeight() / 2;

		userinput = new UserInput();
		mouseinput = new MouseControl();

		addKeyListener( userinput );
		addMouseMotionListener( mouseinput );

		addFocusListener( new FocusAdapter() {
			public void focusGained( java.awt.event.FocusEvent e ) {
				System.out.println( e );
			};

			public void focusLost( java.awt.event.FocusEvent e ) {
				System.out.println( e );
			};
		} );
		setFocusable( true );
		requestFocus();
		requestFocusInWindow();

	}

	public void paint( Graphics g ) {
		if( ownThread == null ) {
			ownThread = new Thread( this );
			ownThread.start();
		}
	}

	public void render( Graphics g ) {
		List<Vision.RayResult> rays = vision.setRays( map, actor.pos_x, actor.pos_y, actor.angle, actor.view_ang );

		if( showmap ) {
			g.drawImage( map.getImage(), 0, 31, null );
			for( RayResult r : rays ) {
				g.setColor( r.color_distance() );
				g.drawLine( (int) r.from_x, (int) r.from_y + 30, (int) r.to_x, (int) r.to_y + 30 );
			}

		}

		this.paintView( g, rays );
	}

	@Override
	public void run() {
		createBufferStrategy( 2 );
		BufferStrategy strategy = getBufferStrategy();

		do {
			long time = System.currentTimeMillis();
			do {
				Graphics g = strategy.getDrawGraphics();

				try {
					userinput.procees();
					for( Actor actor : map.getActors() ) {
						actor.step();
					}
					render( g );
				} catch ( RuntimeException e ) {
					e.printStackTrace();
				}

				g.dispose();
			} while ( strategy.contentsRestored() );
			if( !strategy.contentsLost() ) {
				strategy.show();
				long time2 = System.currentTimeMillis();
				long dif = time2 - time;
				if( dif < 1000 / 60 ) {
					try {
						Thread.sleep( 1000 / 60 - dif );
					} catch ( InterruptedException e ) {
						e.printStackTrace();
					}
				}
			} else {
				Thread.yield();
			}
		} while ( true );

	}

	public void paintView( Graphics g, List<Vision.RayResult> rays ) {

		int step = getWidth() / rays.size();
		int height = getHeight() / 2;

		for( int i = 0 ; i < rays.size() ; i++ ) {
			RayResult r = rays.get( i );
			if( showmap ) {
				height = 0;
			}

			g.setColor( map.getColor( r.to_x, r.to_y ) );
			g.fillRect( i * step, 0, step, 15 + height );
			g.setColor( r.color_distance() );
			g.fillRect( i * step, 15 + height, step, height );
		}
	}

}
