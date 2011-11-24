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
	private Agent agent;
	private Image agentIMG = null;
	
	private static Logger logger;
	
	private static final int DISPLAY_WIDTH = 600;
	private static final int DISPLAY_HEIGHT = 600;
	
	private static final String TILED_MAP_LOCATION = "maps/tile_map_campus_example.tmx";
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
		agent = new Agent(world, 1, 1, 1, 1, 1, 1, 1);
		
		agentIMG = new Image("gfx/stickman.png");
		
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


	}

	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new SimpleGame());

		app.setDisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT, false);
		app.start();

	}
}