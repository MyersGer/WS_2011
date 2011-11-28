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
	private static final String LIGHT_PROPERTY_RADIUS_DEFAULT = "0";
	private static final String LIGHT_PROPERTY_RADIUS = "Radius";
	
	
	private static Point LEFT_UP = new Point(-1,-1);
	private static Point LEFT = new Point(-1,0);
	private static Point LEFT_DOWN = new Point(-1,1);
	private static Point DOWN = new Point(0,1);
	private static Point RIGHT_DOWN = new Point(1,1);
	private static Point RIGHT = new Point(1,0);
	private static Point RIGHT_UP = new Point(1,-1);
	private static Point UP = new Point(0,-1);
	
	private static String NAME_MOVEMENT_LAYER = "movement";
	private static String NAME_LIGHT_LAYER = "leuchtmittelebene";
	
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
	private int[][] light_map;

	public World(TiledMap map) {
		this.map = map;
		initLightMap();
	}
	
	private void initLightMap(){
		this.light_map = new int[map.getWidth()][map.getHeight()];
		int lightLayerIdx = map.getLayerIndex(NAME_LIGHT_LAYER);
		
		
		if(lightLayerIdx > -1){
		
			for(int y = 0; y < map.getHeight(); y++){
				for(int x = 0; x < map.getWidth(); x++){
					int tileId = map.getTileId(x, y, lightLayerIdx);
					Integer radius = new Integer(map.getTileProperty(tileId, LIGHT_PROPERTY_RADIUS, LIGHT_PROPERTY_RADIUS_DEFAULT));
					
					if(radius > 0){
						int fillstart_x;
						int fillstart_y;
						int fillend_x;
						int fillend_y;
						
						fillstart_x = x - radius;
						fillstart_y = y - radius;
						fillend_x   = x + radius;
						fillend_y   = y + radius;
						
						if(fillstart_x < 0)
							fillstart_x = 0;
						if(fillstart_y < 0)
							fillstart_y = 0;
						
						if(fillend_x > map.getWidth())
							fillend_x = map.getWidth();
						if(fillend_y > map.getHeight())
							fillend_y = map.getHeight();
						
						for(int fill_y = fillstart_y; fill_y <= fillend_y; fill_y++){
							for(int fill_x = fillstart_x; fill_x <= fillend_y; fill_x++){
								light_map[fill_x][fill_y] ++;
							}
						}
					}
				}
			}
		}
		else{
			logger.error("Licht Layer konnte nicht gefunden werden");
		}			
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
	
	public int getLightIntensity(Point p){
		return this.light_map[p.x][p.y];
	}

}