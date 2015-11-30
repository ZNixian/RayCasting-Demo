package game;

import level.Level;

public class SingleplayerGame extends AbstractGame {

	public SingleplayerGame() {
	}

	@Override
	public Level loadLevel() {
		return Level.loadLevel("level0.png");
	}

}
