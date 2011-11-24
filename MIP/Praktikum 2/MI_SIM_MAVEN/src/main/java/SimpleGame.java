import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

public class SimpleGame extends BasicGame {
	
	private TiledMap map;
	private List<Agent> agents = new ArrayList<Agent>();
	private static Logger logger;
	
	private static final String TILED_MAP_LOCATION = "map_campus_berliner_tor.tmx";
	private static final String TILED_RESOURCE_LOCATION = "maps";
	
	private static final int AGENT_SPEED_KMH = 5;
	
	
	long lastTime = System.nanoTime();//, beforeTime, afterTime, timeDiff, sleepTime; 

	public SimpleGame() {
		super("Slick2DPath2Glory - SimpleGame");
		
		System.out.println(System.getProperty("user.dir"));
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		logger = Logger.getLogger("SimpleGame");
		map = new TiledMap(TILED_MAP_LOCATION, TILED_RESOURCE_LOCATION);
		World world = new World(map);
		agents.add(new Agent(world, 1, 1, 1, 1, 1, 1, 1));

	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		logger.debug("update");
		processInput(gc.getInput());		
		
		for (Agent agent : agents) {
			//agent.doTurn();
		}
		
		double stepTime = Math.ceil(1/((AGENT_SPEED_KMH * 1000) / 3600000F)); // ms für einen Meter
		
		long currentTime = System.nanoTime();
		long timeDiff = currentTime-lastTime;
		
		long sleepTime = (long) (stepTime - (timeDiff/1000000L)); //zu wartende Zeit in ms
	
		try{
			Thread.sleep(sleepTime); //Zeitdifferenz aussitzen
		}
		catch(Exception ex){} //don't panic 
		
		lastTime = System.nanoTime();
	}

	private void processInput(Input in) {
				if(in.isKeyDown(Input.KEY_DOWN)){
					
				}
				else if (in.isKeyDown(Input.KEY_UP)){
					
				}
				else if (in.isKeyDown(Input.KEY_LEFT)){
					
				}
				else if (in.isKeyDown(Input.KEY_RIGHT)){
					
				}
	}

	public void render(GameContainer gc, Graphics g) throws SlickException {
	
		map.render(0, 0, 0, 0, map.getHeight(), map.getWidth());
	}

	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new SimpleGame());

		app.setDisplayMode(1200, 800, false);

		app.start();

	}
}