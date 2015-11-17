package gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Textures {

	public static final Bitmap floors = loadTexture("/textures/floors.png");

	public static Bitmap loadTexture(String path) {
		try {
			BufferedImage img = ImageIO.read(Textures.class.getResourceAsStream(path));
			Bitmap res = new Bitmap(img.getWidth(), img.getHeight());
			img.getRGB(0, 0, res.width, res.height, res.pixels, 0, res.width);

			for (int i = 0; i < res.pixels.length; i++) {
				res.pixels[i] = res.pixels[i] & 0xffffff; // To delete Alpha :
															// 0x'ff'ffffff
			}

			return res;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("whoops loading textures went wrong!");
			return null;
		}
	}

}
