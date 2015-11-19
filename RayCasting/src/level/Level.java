package level;

import java.util.Random;

public class Level {
	public int width;
	public int height;
	public Block[] tile;

	public Level(int width, int height, int seed) {
		this.width = width;
		this.height = height;
		tile = new Block[width * height];

		// so thats how you do it!
		Random r = new Random(seed);

		for (int i = 0; i < tile.length; i++) {
			if (r.nextInt(10) < 3) {
				tile[i] = new Block(true);
			} else {
				tile[i] = new Block(false);
			}
		}
	}

	public Block getBlock(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height)
			return new Block(true);

		return tile[x + y * width];
	}

}
