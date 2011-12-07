import java.awt.Point;
import java.util.Calendar;
import java.util.Date;
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
	private static final String PROPERTY_RADIUS_DEFAULT = "0";
	private static final String PROPERTY_RADIUS = "Radius";

	private static Point LEFT_UP = new Point(-1, -1);
	private static Point LEFT = new Point(-1, 0);
	private static Point LEFT_DOWN = new Point(-1, 1);
	private static Point DOWN = new Point(0, 1);
	private static Point RIGHT_DOWN = new Point(1, 1);
	private static Point RIGHT = new Point(1, 0);
	private static Point RIGHT_UP = new Point(1, -1);
	private static Point UP = new Point(0, -1);

	private static String NAME_MOVEMENT_LAYER = "movement";
	private static String NAME_LIGHT_LAYER = "leuchtmittelebene";
	private static String NAME_SMELL_LAYER = "stinkebene";
	private static String NAME_WLAN_LAYER = "wlanebene";
	private static String NAME_TRAFFIC_LAYER = "topologieebene";
	private static String NAME_TOPOLOGY_LAYER = "topologieebene";

	private static String NAME_TRAFFIC_TYPE = "strasse";
	private static String NAME_WALL_TYPE = "mauer";

	private static Set<Point> neighborModifications = new HashSet<Point>();

	static {
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
	private int[][] smell_map;
	private int[][] wlan_map;
	private int[][] traffic_map;

	private Clock clk;

	public World(TiledMap map, Date startDate) {
		this.map = map;

		this.light_map = new int[map.getWidth()][map.getHeight()];
		initLayerMap(NAME_LIGHT_LAYER, light_map, PROPERTY_RADIUS,
				PROPERTY_RADIUS_DEFAULT);

		this.smell_map = new int[map.getWidth()][map.getHeight()];
		initLayerMap(NAME_SMELL_LAYER, smell_map, PROPERTY_RADIUS,
				PROPERTY_RADIUS_DEFAULT);

		this.wlan_map = new int[map.getWidth()][map.getHeight()];
		initLayerMap(NAME_WLAN_LAYER, wlan_map, PROPERTY_RADIUS,
				PROPERTY_RADIUS_DEFAULT);

		this.traffic_map = new int[map.getWidth()][map.getHeight()];
		initLayerMap(NAME_TRAFFIC_LAYER, traffic_map, PROPERTY_RADIUS,
				PROPERTY_RADIUS_DEFAULT, NAME_TRAFFIC_TYPE);
		for (int y = 0; y < map.getHeight(); y++) {
			for (int x = 0; x < map.getWidth(); x++) {
				System.out.print(traffic_map[x][y] + " ");
			}
			System.out.print("\n\r");
		}

		clk = new Clock(startDate);
	}

	private void initLayerMap(String layer, int[][] layer_map,
			String attribute, String default_attribute, String... optArgs) {

		boolean processTile = true;
		int layerIdx = map.getLayerIndex(layer);

		if (layerIdx > -1) {

			for (int x = 0; x < map.getWidth(); x++) {
				for (int y = 0; y < map.getHeight(); y++) {
					processTile = true;

					int tileId = map.getTileId(x, y, layerIdx);
					Integer radius = new Integer(map.getTileProperty(tileId,
							attribute, default_attribute));

					if (optArgs.length > 0) {
						String type = map.getTileProperty(tileId, "Typ", "");
						if (!type.equals(optArgs[0]))
							processTile = false;
					}

					if (processTile && radius > 0) {
						int fillstart_x;
						int fillstart_y;
						int fillend_x;
						int fillend_y;

						fillstart_x = x - radius;
						fillstart_y = y - radius;
						fillend_x = x + radius;
						fillend_y = y + radius;

						if (fillstart_x < 0)
							fillstart_x = 0;
						if (fillstart_y < 0)
							fillstart_y = 0;

						if (fillend_x > map.getWidth() - 1)
							fillend_x = map.getWidth() - 1;
						if (fillend_y > map.getHeight() - 1)
							fillend_y = map.getHeight() - 1;

						for (int fill_x = fillstart_x; fill_x <= fillend_x; fill_x++) {
							for (int fill_y = fillstart_y; fill_y <= fillend_y; fill_y++) {
								layer_map[fill_x][fill_y]++;
							}
						}
					}
				}
			}
		} else {
			logger.error("Layer konnte nicht gefunden werden");
		}
	}

	public Set<Point> getNeighbors(Point middle) {
		Set<Point> neighbors = new HashSet<Point>();
		for (Point neighborMod : neighborModifications) {
			int x = middle.x + neighborMod.x;
			int y = middle.y + neighborMod.y;
			neighbors.add(new Point(x, y));
		}
		return neighbors;
	}

	public boolean isWaypoint(Point point) {
		int movementLayerIdx = map.getLayerIndex(NAME_MOVEMENT_LAYER);
		int tileId = map.getTileId(point.x, point.y, movementLayerIdx);
		String waypoint = map.getTileProperty(tileId, WAYPOINT_PROPERTY,
				WAYPOINT_PROPERTY_NOT_MOVABLE);

		if (waypoint.equals(WAYPOINT_PROPERTY_MOVABLE))
			logger.debug(point.toString() + " is Movable: " + waypoint);
		return waypoint.equals(WAYPOINT_PROPERTY_MOVABLE);
	}

	public int getLightIntensity(Point p) {
		return this.light_map[p.x][p.y];
	}

	public int getSmellIntensity(Point p) {
		return this.smell_map[p.x][p.y];
	}

	public int getCurrentTraffic() {
		Calendar cal = Calendar.getInstance();
		int currentHour = cal.get(Calendar.HOUR);

		if (currentHour >= 7 && currentHour < 8) {
			return 150;
		} else if (currentHour >= 8 && currentHour < 9) {
			return 230;
		} else if (currentHour >= 9 && currentHour < 16) {
			return 100;
		} else if (currentHour >= 16 && currentHour < 18) {
			return 500;
		} else if (currentHour >= 18 && currentHour < 24) {
			return 25;
		} else if (currentHour >= 0 && currentHour < 8) {
			return 25;
		} else
			return 0;
	}

	public int getTrafficAtLocation(Point location) {
		return this.traffic_map[location.x][location.y];
	}

	public Clock getClock() {
		return clk;
	}

	public int calcSpace(Point pos, int radius) {

		int space = (((radius * 2) + 1) * ((radius * 2) + 1)) - 1;

		int layerIdx = map.getLayerIndex(NAME_TOPOLOGY_LAYER);

		if (layerIdx > -1) {
			int fillstart_x;
			int fillstart_y;
			int fillend_x;
			int fillend_y;

			fillstart_x = pos.x - radius;
			fillstart_y = pos.y - radius;
			fillend_x = pos.x + radius;
			fillend_y = pos.y + radius;

			if (fillstart_x < 0)
				fillstart_x = 0;
			if (fillstart_y < 0)
				fillstart_y = 0;

			if (fillend_x > map.getWidth() - 1)
				fillend_x = map.getWidth() - 1;
			if (fillend_y > map.getHeight() - 1)
				fillend_y = map.getHeight() - 1;

			for (int fill_x = fillstart_x; fill_x <= fillend_x; fill_x++) {
				for (int fill_y = fillstart_y; fill_y <= fillend_y; fill_y++) {

					int tileId = map.getTileId(fill_x, fill_y, layerIdx);

					String type = map.getTileProperty(tileId, "Typ", "");
					if (type.equals(NAME_WALL_TYPE)) {
						space--;
					}

				}
			}
		}

		else {
			logger.error("Layer konnte nicht gefunden werden");
		}
		
		return space;
	}

}