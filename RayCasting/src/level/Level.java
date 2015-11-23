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
			Block block = null;

			if (r.nextInt(7) == 0) {
				block = new BlockSolid();
			} else {
				block = new Block();
				if (r.nextInt(5) == 0) 
					block.addSprite(new Sprite(0, 0, 0));
			}

			tile[i] = block;
		}
	}

	public Block getBlock(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height)
			return new BlockSolid();

		return tile[x + y * width];
	}

}
