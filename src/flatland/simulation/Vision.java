package flatland.simulation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;


public class Vision {
	public double distinct_distances = 255;
	public double max_rad = 150 * 4;

	private RayResult sendRay( Map map, double pos_x, double pos_y, double ang, double maxdist ) {
		double px = Math.cos( ang );
		double py = Math.sin( ang );
		double x = pos_x, y = pos_y;
		double dis = maxdist;

		Color c = map.getColor( pos_x, pos_y );
		Color pos_color = c;
		while ( true ) {
			x += px;
			y += py;

			if( x < map.getOriginX() || y < map.getOriginY() || x > map.getWidth() || y > map.getHeight() ) {
				x -= px;
				y -= py;
				break;
			}

			dis = Util.getDistance( pos_x, pos_y, x, y );
			if( dis > maxdist ) {
				break;
			}

			Color cur_color = map.getColor( x, y );
			if( !pos_color.equals( cur_color ) ) {
				c = cur_color;
				break;
			}
		}

		return new RayResult( pos_x, pos_y, x, y );
	}

	public List<RayResult> setRays( Map map, double pos_x, double pos_y, double angle, double view_ang ) {
		List<RayResult> rays = new ArrayList<RayResult>();
		for( double i = -view_ang / 2 ; i < view_ang / 2 ; i += 0.5 ) {
			double ang = angle + 1 / (double) view_ang * i;
			rays.add( sendRay( map, pos_x, pos_y, ang, max_rad ) );
		}
		return rays;
	}

	public class RayResult {
		public double from_x, from_y, to_x, to_y;

		public RayResult( double from_x , double from_y , double to_x , double to_y ) {
			super();
			this.from_x = from_x;
			this.from_y = from_y;
			this.to_x = to_x;
			this.to_y = to_y;
		}

		public double distance() {
			double dist = Util.getDistance( from_x, from_y, to_x, to_y );
			return dist > max_rad ? max_rad : dist;
		}

		public int lofi_distance() {
			double dist = distance();
			if( dist < 1 ) {
				return 0;
			}
			return (int) ( Math.log( dist ) / Math.log( max_rad ) * distinct_distances );
		}

		public Color color_distance() {
			double lowfi_dist = lofi_distance();
			int color = 255; // nearest
			if( lowfi_dist != 0 ) {
				color = (int) ( 255d - lowfi_dist / distinct_distances * 255d );
			}
			return new Color( color, color, color );
		}

		@Override
		public String toString() {
			return String.format( "RayResult{distance: %s},  lofi_distance: %s, ", distance(), lofi_distance() );
		}

	}
}
