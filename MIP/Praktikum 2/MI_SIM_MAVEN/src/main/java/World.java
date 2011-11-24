import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;


public class World {
	private static Logger logger = Logger.getLogger("SimpleGame");
	
	private static final String WAYPOINT_PROPERTY = "Typ";
	private static final String WAYPOINT_PROPERTY_NOT_MOVABLE = "0";
	private static final String WAYPOINT_PROPERTY_MOVABLE = "waypoint";
	
	
	private static Point LEFT_UP = new Point(-1,-1);
	private static Point LEFT = new Point(-1,0);
	private static Point LEFT_DOWN = new Point(-1,1);
	private static Point DOWN = new Point(0,1);
	private static Point RIGHT_DOWN = new Point(1,1);
	private static Point RIGHT = new Point(1,0);
	private static Point RIGHT_UP = new Point(1,-1);
	private static Point UP = new Point(0,-1);
	
	private static String NAME_MOVEMENT_LAYER = "movement";
	
	private static Set<Point> neighborModifications = new HashSet<Point>();
	
	static{
		neighborModifications.add(LEFT_UP);
		neighborModifications.add(LEFT);
		neighborModifications.add(LEFT_DOWN);
		neighborModifications.add(DOWN);
		neighborModifications.add(RIGHT_DOWN);
		neighborModifications.add(RIGHT);
		neighborModifications.add(RIGHT_UP);
		neighborModifications.add(UP);		
	}
	
	private TiledMap map;

	public World(TiledMap map) {
		this.map = map;
	}
	
	public Set<Point> getNeighbors(Point middle){
		Set<Point> neighbors = new HashSet<Point>();
		for (Point neighborMod : neighborModifications) {
			int x = middle.x + neighborMod.x;
			int y = middle.y + neighborMod.y;
			neighbors.add(new Point(x,y));
		}
		return neighbors;
	}

	public boolean isWaypoint(Point point) {
		int movementLayerIdx = map.getLayerIndex(NAME_MOVEMENT_LAYER);
		int tileId = map.getTileId(point.x, point.y, movementLayerIdx);
		String waypoint = map.getTileProperty(tileId, WAYPOINT_PROPERTY, WAYPOINT_PROPERTY_NOT_MOVABLE);
		
		if(waypoint.equals(WAYPOINT_PROPERTY_MOVABLE))
			logger.debug(point.toString() + " is Movable: " + waypoint);
		return waypoint.equals(WAYPOINT_PROPERTY_MOVABLE);
	}

}
