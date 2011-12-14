import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class Agent implements Names{

	public Point START_POINT = new Point(4, 180);
	public Point END_POINT = new Point(108,31);
	
	public  final static boolean MOVED = true;
	public  final static boolean NOT_MOVED = false;

	private Point lastLocation = null;
	private Point location = START_POINT;

	private int spacePreferenceRadius;
	private double spacePreference;
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
			double distanceStreetPreference, int spaceRadiusPreference) {
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
		this.spacePreferenceRadius = spaceRadiusPreference;
		this.spacePreference = (((spaceRadiusPreference * 2) + 1) * ((spaceRadiusPreference * 2) + 1)) - 1;
	}
	
	
	public int getSpacePreference(){
		return spacePreferenceRadius;
	}
	
	public Point getLocation() {
		return location;
	}
	
	public boolean reachedEndPoint() {
		return location == END_POINT;
	}


	public boolean doTurn() {
		logger.debug("doTurn()");
		if (location != END_POINT) {
			sample();
			return movement();
		}else {
			return NOT_MOVED;
		}

	}

	private boolean movement() {
		logger.debug("move()");
		Point targetCell = getNextLocation();
		return moveToLocation(targetCell);
	}

	private boolean moveToLocation(Point targetCell) {
		logger.debug("moveToLocation()");
		if (targetCell != null) {
			logger.info(targetCell.toString());
			lastLocation = location;
			location = targetCell;
			return MOVED;
		}else{
			return NOT_MOVED;
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
		
		// Sicherheit = Licht + Raum - Verkehr 
		double sicherheit = lightPreference * world.getLightIntensity(location) 
							+ ((world.calcSpace(location, spacePreferenceRadius)/spacePreference)-1)
							- ( trafficPreference * world.getCurrentTraffic() * (world.getTrafficAtLocation(location)/100));
		
		// Vergnügen = WLAN + Raum - Gestank - Verkehr + Grünfläche
		double vergnuegen = wLanPreference * world.getWlanAtLocation(location) 
							+ ((world.calcSpace(location, spacePreferenceRadius)/spacePreference)-1)
							- smellPreference * world.getSmellIntensity(location)
							- trafficPreference * world.getCurrentTraffic() * (world.getTrafficAtLocation(location)/100)
							+ greenPreference * (world.getGreenIntensity(location)/10);
		
		// Soziologie = WLAN + Licht - Gestank
		double soziologie = wLanPreference * world.getWlanAtLocation(location) 
							+ lightPreference * world.getLightIntensity(location)
							- smellPreference * world.getSmellIntensity(location);
		
		// Produktivität = WLAN - Verkehr - Gestank
		double produktivitaet = wLanPreference * world.getWlanAtLocation(location)
							- trafficPreference * world.getCurrentTraffic() * (world.getTrafficAtLocation(location)/100)
							- smellPreference * world.getSmellIntensity(location);
		
		
		double gesamt = 0.5 * sicherheit + 0.2 * vergnuegen + 0.2 * produktivitaet + 0.1 * soziologie;
		
		HashMap<String, Double> tempMap = new HashMap<String, Double>();
		tempMap.put(EB_SECURITY, sicherheit);
		tempMap.put(EB_FUN, vergnuegen);
		tempMap.put(EB_SOZIOLOGY, soziologie);
		tempMap.put(EB_PRODUCTIVITY, produktivitaet);
		tempMap.put(EB_OVERALL, gesamt);
		
		dataLogger.addEntry(tempMap);
		
		//System.out.println("Smell " + world.getSmellIntensity(location));
		
		logger.debug("Sicherheit: " + sicherheit);
		logger.debug("Vergnügen: " + vergnuegen);
		logger.debug("Soziologie: " + soziologie);
		logger.debug("Produktivität: " + produktivitaet);
		logger.debug("Gesamt: " + gesamt);
		
		logger.debug("WLAN: " + world.getWlanAtLocation(location));

	}


	public DataPointLog getDataLogger() {
		return dataLogger;
	}
	
	

}
