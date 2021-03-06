import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

public class SimpleGame extends BasicGame {

	private TiledMap map;
	private Agent agent;
	private Image agentIMG = null;

	private static Logger logger;

	private static final int DISPLAY_WIDTH = 800;
	private static final int DISPLAY_HEIGHT = 800;

	private static final String TILED_MAP_LOCATION = "maps/map_campus_berliner_tor.tmx";
	private static final String TILED_RESOURCE_LOCATION = "maps";

	private static final int AGENT_SPEED_KMH = 5;
	private static final String NAME_CSV_FILE = "gamelog.csv";

	private int speedMultiplicator = 1;

	private static SimpleDateFormat SIMULATIONTIMEFORMAT = new SimpleDateFormat("HH:mm:ss");

	World world;
	private static AppGameContainer app;
	long lastTime = System.nanoTime();// , beforeTime, afterTime, timeDiff,
										// sleepTime;

	public SimpleGame() {
		super("Slick2DPath2Glory - SimpleGame");

		System.out.println(System.getProperty("user.dir"));

	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		logger = Logger.getLogger("SimpleGame");

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		Date startDate;
		try {
			startDate = dateFormat.parse("7.12.2011 12:05");
		} catch (ParseException e) {
			logger.error("Fehler beim Datumparsen. Aktuelle Zeit wird genommen!");
			startDate = new Date(System.currentTimeMillis());
		}

		map = new TiledMap(TILED_MAP_LOCATION, TILED_RESOURCE_LOCATION);
		world = new World(map, startDate);
		agent = new Agent(world, 1, 1, 1, 1, 1, 1, 1, 3);

		agentIMG = new Image("gfx/stickman.png");
		world.getClock().start(); // Zeitzählung starten
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		boolean moved = false;
		logger.debug("update");
		processInput(gc.getInput());

		moved = agent.doTurn();
		world.getClock().update();

		if (!moved) {
			logger.debug("Stop Game");
			try {
				Convert2X.Convert2CSV(agent.getDataLogger().getDataSetList(), NAME_CSV_FILE);
			} catch (IOException e) {
				logger.debug("Could not write gameLog", e);
			}
			app.destroy();
		}

		double stepTime = Math.ceil(1 / ((AGENT_SPEED_KMH * 1000) / 3600000F)); // ms
																				// für
																				// einen
																				// Meter

		long currentTime = System.nanoTime();
		long timeDiff = currentTime - lastTime;

		long sleepTime = (long) (stepTime - (timeDiff / 1000000L)); // zu
																	// wartende
																	// Zeit in
																	// ms

		try {
			Thread.sleep(sleepTime / this.speedMultiplicator); // Zeitdifferenz
																// aussitzen
		} catch (Exception ex) {
		} // don't panic

		lastTime = System.nanoTime();
	}

	private void processInput(Input in) {
		if (in.isKeyDown(Input.KEY_DOWN)) {
			if (speedMultiplicator > 1)
				speedMultiplicator--;
		} else if (in.isKeyDown(Input.KEY_UP)) {
			if (speedMultiplicator < 10)
				speedMultiplicator++;
		} else if (in.isKeyDown(Input.KEY_LEFT)) {

		} else if (in.isKeyDown(Input.KEY_RIGHT)) {

		}
	}

	public void render(GameContainer gc, Graphics g) throws SlickException {
		// map.render(0, 0, 0, 0, map.getHeight(), map.getWidth());
		int tileWidth = map.getTileWidth();
		int tileHeight = map.getTileHeight();

		int agentx = agent.getLocation().x;
		int agenty = agent.getLocation().y;

		// Mittelpunkt bestimmen
		int centerx = (DISPLAY_WIDTH / tileWidth) / 2;
		int centery = (DISPLAY_HEIGHT / tileHeight) / 2;

		// Offset bestimmen
		int offsetx = agentx - centerx;
		int offsety = agenty - centery;

		map.render(0, 0, offsetx, offsety, map.getHeight(), map.getWidth());

		agentIMG.draw((centerx - 0.5f) * tileWidth, (centery - 1) * tileHeight, 2 * tileWidth, 2 * tileHeight);

		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, 180, 280);
		g.setColor(new Color(255, 255, 255));
		g.drawString(world.getClock().getTimeString(), 10, 30);
		g.drawString("Speed: " + this.speedMultiplicator, 10, 60);
		Date outputTime = world.getClock().getAbsoluteTime();
		g.drawString(SIMULATIONTIMEFORMAT.format(outputTime), 10, 90);
		// Ausgabe der Umgebungsparameter
		g.drawString("Licht: " + world.getLightIntensity(agent.getLocation()), 10, 120);
		g.drawString("Verkehr: " + world.getTrafficAtLocation(agent.getLocation()), 10, 150);
		g.drawString("Geruch: " + world.getSmellIntensity(agent.getLocation()), 10, 180);
		g.drawString("Platz: "+ world.calcSpace(agent.getLocation(), agent.getSpacePreference()), 10, 210);
		g.drawString("Gruenflaeche: "+ world.getGreenIntensity(agent.getLocation()), 10, 240);

	}

	public static void main(String[] args) throws SlickException {
		app = new AppGameContainer(new SimpleGame());

		app.setDisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT, false);
		
		app.start();
	}

	@Override
	public boolean closeRequested() {
		// TODO Auto-generated method stub
		return false;
	}

}