package level;

import game.AbstractGame;

public class Player {

	public AbstractGame game;
	public Level level;
	public double x, y, xa, ya, ra, rot;

	public Player(AbstractGame game) {
		this.game = game;
		this.level = game.level;
		this.x = level.xSpawn;
		this.y = level.ySpawn;
		rot = -Math.PI / 2;
	}

	public void update(boolean up, boolean down, boolean left, boolean right, boolean turnLeft, boolean turnRight) {
		// walk speed
		double wSpeed = 0.03;
		// rotation speed
		double rSpeed = 0.03;

		double xd = 0;
		double yd = 0;

		if (up)
			yd++;
		if (down)
			yd--;
		if (left)
			xd--;
		if (right)
			xd++;
		if (turnLeft)
			ra++;
		if (turnRight)
			ra--;

		double rCos = Math.cos(rot);
		double rSin = Math.sin(rot);

		xa += (xd * rCos + yd * -rSin) * wSpeed;
		ya += (xd * rSin + yd * rCos) * wSpeed;
		rot += ra * rSpeed;

		if (isFree(x + xa, y))
			x += xa;
		if (isFree(x, y + ya))
			y += ya;
			

		// adds a slow down effect
		xa *= 0.6;
		ya *= 0.6;
		ra *= 0.6;
	}

	// collision stuffs!
	private boolean isFree(double xx, double yy) {
		double d = 0.15;
		
		int x0 = (int) (Math.round(xx - d));
		int x1 = (int) (Math.round(xx + d));
		int y0 = (int) (Math.round(yy - d));
		int y1 = (int) (Math.round(yy + d));
		
		if(level.getBlock(x0, y0).SOLID_MOTION) return false;
		if(level.getBlock(x1, y0).SOLID_MOTION) return false;
		if(level.getBlock(x0, y1).SOLID_MOTION) return false;
		if(level.getBlock(x1, y1).SOLID_MOTION) return false;

		return true;
	}

	public void render() {

	}

}
