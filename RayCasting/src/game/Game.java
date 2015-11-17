package game;

import gfx.Screen;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 640;
	public static final int HEIGHT = WIDTH * 3 / 4;
	public static final int SCALE = 1;
	public static final String TITLE = "RayCasting demo";

	public static final double FPS_CAP = 60;
	
	private boolean isRunning = false;

	public static final BufferedImage img = new BufferedImage(WIDTH, HEIGHT,BufferedImage.TYPE_INT_RGB);
	public static final int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();

	private Screen screen;
	
	public Game() {
		screen = new Screen(WIDTH, HEIGHT);
	}

	public void start() {
		if (isRunning)
			return;
		isRunning = true;
		new Thread(this).start();

	}

	@Override
	public void run() {
		// nano seconds per tick
		final double nsPerTick = 1000000000.0 / FPS_CAP;
		
		long lastTime = System.nanoTime();
		double unposcessedTime = 0;
		
		int frames = 0;
		int updates = 0;
		
		long frameCounter = System.currentTimeMillis();
		
		while (isRunning) {
			long currentTime = System.nanoTime();
			long passedTime = currentTime - lastTime;
			lastTime = currentTime;
			unposcessedTime += passedTime;
			
			if(unposcessedTime >= nsPerTick) {
				unposcessedTime = 0;
				update();
				updates++;
			}
			
			render();
			frames++;
			
			if(System.currentTimeMillis() - frameCounter >= 1000) {
				System.out.println("Frames :" + frames + ", updates : " + updates);
				frames = 0;
				updates = 0;
				frameCounter += 1000;
			}
			
		}
		dispose();

	}

	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();

		// background colour
		for (int i = 0; i < pixels.length; i++) { 
			pixels[i] = 0;
		}
		
		//update the screens pixels
		screen.render();

		for (int i = 0; i < pixels.length; i++) { 
			pixels[i] = screen.pixels[i];
		}
		
		g.drawImage(img, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);

		g.dispose();
		bs.show();
	}

	public void update() {
		screen.update();
	}

	public void stop() {
		if (!isRunning)
			return;
		isRunning = false;
	}

	public void dispose() {
		System.exit(0);
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setTitle(TITLE);
		f.setResizable(false);
		Game game = new Game();
		Dimension d = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
		game.setMinimumSize(d);
		game.setMaximumSize(d);
		game.setPreferredSize(d);
		f.add(game);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setAlwaysOnTop(true);
		f.setVisible(true);

		game.start();

	}

}
