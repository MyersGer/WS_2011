import java.awt.Point;

public class Agent {

	public Point START_POINT = new Point(0, 0);

	private Point lastLocation;
	
	private double lightPreference;
	private double trafficPreference;
	private double smellPreference;
	private double wLanPreference;
	private double cleanPreference;
	private double greenPreference;
	private double distanceStreetPreference;
	
	public Agent(double lightPreference, double trafficPreference, double smellPreference, double wLanPreference, double cleanPreference, double greenPreference, double distanceStreetPreference) {
		super();
		this.lightPreference = lightPreference;
		this.trafficPreference = trafficPreference;
		this.smellPreference = smellPreference;
		this.wLanPreference = wLanPreference;
		this.cleanPreference = cleanPreference;
		this.greenPreference = greenPreference;
		this.distanceStreetPreference = distanceStreetPreference;
	}
	
	

}
