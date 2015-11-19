package level;

public class Player {

	public double x, y,xd,yd,rd, rot;

	public Player() {
		this(0.5, 0.5);
	}
	
	public Player(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void update(boolean up, boolean down, boolean left, boolean right, boolean turnLeft, boolean turnRight) {
		// walk speed
		double wSpeed = 0.03;
		// rotation speed
		double rSpeed = 0.03;

		if (up)
			xd++;
		if (down)
			xd--;
		if (left)
			yd--;
		if (right)
			yd++;
		if (turnLeft)
			rd++;
		if (turnRight)
			rd--;
		
		double rCos = Math.cos(rot);
		double rSin = Math.sin(rot);
		
		x += (xd * -rSin + yd * rCos) * wSpeed;
		y += (xd * rCos + yd * rSin) * wSpeed;
		rot += rd * rSpeed;
		
		//adds a slow down effect
		xd *= 0.6;
		yd *= 0.6;
		rd *= 0.6;
	}

	public void render() {

	}

}
