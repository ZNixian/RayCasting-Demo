package level;

import java.util.ArrayList;
import java.util.List;

public class Block {
	public static final int NET_ID_SOLID = 1;
	public static final int NET_ID_EMPTY = 2;
	public static final int NET_ID_SPAWN = 3;
	public static final int NET_ID_GRASS = 4;
	
	public boolean SOLID_RENDER = false;
	public boolean SOLID_MOTION = false;

	public List<Sprite> sprites = new ArrayList<Sprite>();

	public int tex = 0;
	public int col = 0x555555;
	
	public int ceilTex = 0;
	public int floorTex = 0;
	public int ceilCol = 0x555555;
	public int floorCol = 0x555555;

	public Block() {

	}

	public void addSprite(Sprite sprite) {
		sprites.add(sprite);

	}

}
