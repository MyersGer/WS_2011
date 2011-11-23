import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

public class SimpleGame extends BasicGame {
	
	private TiledMap map;
	private List<Agent> agents = new ArrayList<Agent>();
	private static Logger logger;
	
	private static final String TILED_MAP_LOCATION = "maps/tile_map_campus_example.tmx";
	private static final String TILED_RESOURCE_LOCATION = "maps";

	public SimpleGame() {
		super("Slick2DPath2Glory - SimpleGame");
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
		for (Agent agent : agents) {
			agent.doTurn();
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