import org.apache.log4j.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
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

	public SimpleGame() {
		super("Slick2DPath2Glory - SimpleGame");
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
		try {
			Thread.currentThread().sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("update");
		agent.doTurn();
		
	}

	public void render(GameContainer gc, Graphics g) throws SlickException {
		//map.render(0, 0, 0, 0, map.getHeight(), map.getWidth());
		int tileWidth = map.getTileWidth();
		int tileHeight = map.getTileHeight();
		
		int agentx = agent.getLocation().x;
		int agenty = agent.getLocation().y;
		
		//Mittelpunkt bestimmen
		int centerx = (DISPLAY_WIDTH/tileWidth)/2;
		int centery = (DISPLAY_HEIGHT/tileHeight)/2;
		
		//Offset bestimmen
		int offsetx = agentx-centerx;
		int offsety = agenty-centery;
			
		map.render(0, 0, offsetx, offsety, map.getHeight(), map.getWidth());
			
		agentIMG.draw(centerx*tileWidth, centery*tileHeight, tileWidth, tileHeight);


	}

	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new SimpleGame());

		app.setDisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT, false);
		app.start();

	}
}