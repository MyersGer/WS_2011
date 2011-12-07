import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class Agent implements Names{

	public Point START_POINT = new Point(4, 180);
	public Point END_POINT = new Point(108,31);

	private Point lastLocation = null;
	private Point location = START_POINT;

	private double lightPreference;
	private double trafficPreference;
	private double smellPreference;
	private double wLanPreference;
	private double cleanPreference;
	private double greenPreference;
	private double distanceStreetPreference;
	private World world;
	
	private DataPointLog dataLogger;

	private static Logger logger = Logger.getLogger("SimpleGame");

	public Agent(World world, double lightPreference, double trafficPreference, double smellPreference, double wLanPreference, double cleanPreference, double greenPreference,
			double distanceStreetPreference) {
		super();
		this.lightPreference = lightPreference;
		this.trafficPreference = trafficPreference;
		this.smellPreference = smellPreference;
		this.wLanPreference = wLanPreference;
		this.cleanPreference = cleanPreference;
		this.greenPreference = greenPreference;
		this.distanceStreetPreference = distanceStreetPreference;
		this.world = world;
		this.dataLogger = new DataPointLog();
	}
	
	
	public Point getLocation() {
		return location;
	}
	
	public boolean reachedEndPoint() {
		return location == END_POINT;
	}


	public void doTurn() {
		logger.debug("doTurn()");
		if (location != END_POINT) {
			sample();
			movement();
		}

	}

	private void movement() {
		logger.debug("move()");
		Point targetCell = getNextLocation();
		moveToLocation(targetCell);
	}

	private void moveToLocation(Point targetCell) {
		logger.debug("moveToLocation()");
		if (targetCell != null) {
			logger.info(targetCell.toString());
			lastLocation = location;
			location = targetCell;
		}

	}

	private Point getNextLocation() {
		logger.debug("getNextLocation()");
		Set<Point> neighbors = world.getNeighbors(location);
		Point target = null;
		for (Point candidateTarget : neighbors) {
			if (world.isWaypoint(candidateTarget) && !candidateTarget.equals(lastLocation)) {
				target = candidateTarget;
			}
		}

		return target;
	}

	private void sample() {
		logger.debug("sample()");
		
		double sicherheit = lightPreference * world.getLightIntensity(location) - ( trafficPreference * world.getCurrentTraffic() * (world.getTrafficAtLocation(location)/100));
		
		double vergnuegen = 0;
		double soziologie = 0;
		double produktivitaet = 0;
		
		double gesamt = 0.5 * sicherheit + 0.2 * vergnuegen + 0.2 * produktivitaet + 0.1 * soziologie;
		
		HashMap<String, Double> tempMap = new HashMap<String, Double>();
		tempMap.put(EB_SECURITY, sicherheit);
		tempMap.put(EB_FUN, vergnuegen);
		tempMap.put(EB_SOZIOLOGY, soziologie);
		tempMap.put(EB_PRODUCTIVITY, produktivitaet);
		tempMap.put(EB_OVERALL, gesamt);
		
		Set<Map<String, Double>> entry = new HashSet<Map<String, Double>>();
		
		entry.add(tempMap);
		
		dataLogger.addEntry(entry);
		
		//System.out.println("Smell " + world.getSmellIntensity(location));
		System.out.println("Sicherheit " + sicherheit);

	}

}
