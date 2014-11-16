package flatland.simulation;

public class UserActor extends Actor{
	
	public double rotate_step = Math.PI * 2 / 360;
	
	public UserActor() {
	}
	
	public void step( double px, double py, Map map ) {
		double new_pos_x = pos_x + px;
		double new_pos_y = pos_y + py;
		if( new_pos_x >= map.getOriginX() && new_pos_x < map.getWidth() && new_pos_y >= map.getOriginY() && new_pos_y < map.getHeight() ) {
			if( map.rgb( pos_x, pos_y ) == map.rgb( new_pos_x, new_pos_y ) ) {
				pos_x = new_pos_x;
				pos_y = new_pos_y;
			}
		}
	}
}
