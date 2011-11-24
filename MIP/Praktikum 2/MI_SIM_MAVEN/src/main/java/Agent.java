import java.awt.Point;
import java.util.Set;

import org.apache.log4j.Logger;

public class Agent {

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

	}

}
