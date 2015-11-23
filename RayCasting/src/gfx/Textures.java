package gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Textures {

	public static final Bitmap textures = loadTexture("/textures/textures.png");

	public static Bitmap loadTexture(String path) {
		try {
			BufferedImage img = ImageIO.read(Textures.class.getResourceAsStream(path));
			Bitmap res = new Bitmap(img.getWidth(), img.getHeight());
			img.getRGB(0, 0, res.width, res.height, res.pixels, 0, res.width);

			// note: 'greyness' must be a full hex number ie 0xcccccc, 0xffffff
			// etc - in the image file that is
			for (int i = 0; i < res.pixels.length; i++) {
				int ci = res.pixels[i];
				int col = (ci & 0xf) >> 2;
				if (ci == 0xffff00ff)
					col = -1;
				res.pixels[i] = col;

			}

			return res;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("whoops loading textures went wrong!");
			return null;
		}
	}

}
