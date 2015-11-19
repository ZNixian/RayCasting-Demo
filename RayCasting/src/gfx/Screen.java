package gfx;

import game.Game;

import java.util.Random;

public class Screen extends Bitmap{

	public Random r = new Random();
	
	public Bitmap test;
	public Bitmap3D perspectiveVison;
	
	public Screen(int width, int height) {
		super(width, height);
		
		test = new Bitmap(50,50);
		for(int i = 0;i < test.pixels.length;i++) {
			test.pixels[i] = r.nextInt();
		}
		
		perspectiveVison = new Bitmap3D(width, height);
	}

	public void render(Game game) {
		clear();
		perspectiveVison.render(game);
		perspectiveVison.renderFog();
		render(perspectiveVison,0,0);
		//render(test, (width - 50) / 2 + ox,(height - 50) / 2 + oy);
	
	}
	
	public void update() {
		
	}
	
}
