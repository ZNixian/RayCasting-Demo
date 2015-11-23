package level;

import java.util.ArrayList;
import java.util.List;

public class Block {
	public  boolean SOLID_RENDER = false;
	public  boolean SOLID_MOTION = false;

	public List<Sprite> sprites = new ArrayList<Sprite>();
	
	public Block() {
		
	}

	public void addSprite(Sprite sprite) {
		sprites.add(sprite);
		
	}
	
	
}
