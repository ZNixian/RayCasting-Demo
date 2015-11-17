package gfx;

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

	int t = 0;
	
	public void render() {
		t++;
		int ox = (int) (Math.sin(t / 1000.0) * width / 2);
		int oy = (int) (Math.cos(t / 1000.0) * height / 2);
		
		clear();
		perspectiveVison.render();
		render(perspectiveVison,0,0);
		//render(test, (width - 50) / 2 + ox,(height - 50) / 2 + oy);
	
	}
	
	public void update() {
		
	}
	
}
