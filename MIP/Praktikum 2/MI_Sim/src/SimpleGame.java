import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

public class SimpleGame extends BasicGame {
	private TiledMap map;

	public SimpleGame() {
		super("Slick2DPath2Glory - SimpleGame");
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		map = new TiledMap("maps/tile_map_campus_example.tmx", "maps");

	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {

	}

	public void render(GameContainer gc, Graphics g) throws SlickException {
		map.render(0, 0, 0,0,map.getHeight(), map.getWidth());

	}

	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new SimpleGame());

		app.setDisplayMode(1200, 800, false);
	
		app.start();

	}
}