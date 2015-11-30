package game;

import gfx.Bitmap3D;
import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_E;
import static java.awt.event.KeyEvent.VK_Q;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_W;
import level.Level;
import level.Player;

/**
 *
 * @author znix
 */
public abstract class AbstractGame {

	public Level level;
	public Player player;
	public int time;

	public AbstractGame() {
	}

	/**
	 * Load everything
	 */
	public void load() {
		level = loadLevel();
		player = new Player(this);
	}
	
	/**
	 * Render this game stuff (eg, other players, HUD) to the screen.
	 * @param render The bitmap to render to
	 */
	public void render(Bitmap3D render) {
	}
	
	/**
	 * Get the level this game is to be played on
	 * @return the level
	 */
	public abstract Level loadLevel();

	public void update(boolean[] keys) {
		time++;

		boolean up = keys[VK_W];
		boolean down = keys[VK_S];
		boolean left = keys[VK_A];
		boolean right = keys[VK_D];
		boolean turnLeft = keys[VK_Q];
		boolean turnRight = keys[VK_E];

		player.update(up, down, left, right, turnLeft, turnRight);
	}

}
