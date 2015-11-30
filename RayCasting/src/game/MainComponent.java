package game;

import gfx.Screen;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.net.Socket;

import javax.swing.JFrame;

public class MainComponent extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 160;
	public static final int HEIGHT = WIDTH * 3 / 4;
	public static final int SCALE = 4;
	public static final String TITLE = "RayCasting demo";

	public static final double FPS_CAP = 60;

	private boolean isRunning = false;

	public final BufferedImage img;
	public final int[] pixels;

	private AbstractGame game;
	private Screen screen;
	private InputHandler inputHandler;

	public MainComponent() {
		Dimension d = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
		setMinimumSize(d);
		setMaximumSize(d);
		setPreferredSize(d);

		img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();

		inputHandler = new InputHandler();

		addKeyListener(inputHandler);
		addMouseMotionListener(inputHandler);
		addFocusListener(inputHandler);
		addMouseMotionListener(inputHandler);
		addMouseWheelListener(inputHandler);

	}

	public void start() {
		if (isRunning) {
			return;
		}
		isRunning = true;
		init();
		new Thread(this).start();
	}

	public void init() {
		Socket connect = ClientGame.connect();
		if (connect == null) {
			game = new HostGame();
		} else {
			game = new ClientGame(connect);
		}

		game.load();
		screen = new Screen(WIDTH, HEIGHT);

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

			if (unposcessedTime >= nsPerTick) {
				unposcessedTime = 0;
				update();
				updates++;
			}

			render();
			frames++;

			if (System.currentTimeMillis() - frameCounter >= 1000) {
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
		screen.render(game);

		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = screen.pixels[i];
		}

		g.drawImage(img, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);

		g.dispose();
		bs.show();
	}

	public void update() {
		game.update(inputHandler.keys);
		screen.update();
	}

	public void stop() {
		if (!isRunning) {
			return;
		}
		isRunning = false;
	}

	public void dispose() {
		System.exit(0);
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setTitle(TITLE);
		f.setResizable(false);
		MainComponent game = new MainComponent();
		f.add(game);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setAlwaysOnTop(true);
		f.setVisible(true);

		game.start();

	}

}
