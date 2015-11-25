package level;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class Level {
	private static Map<String, Level> levels = new HashMap<>();
	public String path;
	public int width;
	public int height;
	public int[] pixels;
	public Block[] tile;

	public static int xSpawn;
	public static int ySpawn;

	public Level(String path, int width, int height) {
		this.path = path;
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
		tile = new Block[width * height];
		this.pixels = new int[width * height];
	}

	public void load() {

		for (int i = 0; i < width * height; i++) {
			pixels[i] = pixels[i] & 0xffffff;
		}

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Block block = new Block();

				int type = pixels[x + y * width];

				if (type == 0xFFFFFF) {
					block = new SolidBlock();
					block.col = 0x667CDB & 0x555555;
				} else if (type == 0xFFFF00) {
					xSpawn = x;
					ySpawn = y;
				} else if (type == 0x00FF00) {
					block.addSprite(new Sprite(0, 0, 0, 1, 0x003300));
				}
				// set roof and floor colours
				if (type != 0xFFFFFF) {
					block.ceilCol = 0x550055;
					block.floorCol = 0x550000;
				}
				tile[x + y * width] = block;
			}
		}
	}

	public Block getBlock(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height)
			return new SolidBlock();

		return tile[x + y * width];
	}

	public static Level loadLevel(String path) {
		if (levels.containsKey(path))
			return levels.get(path);

		try {
			BufferedImage img = ImageIO.read(Level.class.getResourceAsStream("/levels/" + path));
			int w = img.getWidth();
			int h = img.getHeight();

			Level res = new Level(path, w, h);
			img.getRGB(0, 0, res.width, res.height, res.pixels, 0, res.width);
			res.load();

			return res;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("whoops loading level went wrong!");
			return null;
		}
	}

}
