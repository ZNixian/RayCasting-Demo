package gfx;

import game.Game;
import level.Block;
import level.Level;
import level.Sprite;

public class Bitmap3D extends Bitmap {

	private double[] depthBuffer;
	private double[] depthBufferWall;
	private double xCam, yCam, zCam, rot, rSin, rCos, fov, xCenter, yCenter;

	public Bitmap3D(int width, int height) {
		super(width, height);

		depthBuffer = new double[width * height];
		depthBufferWall = new double[width];
	}

	public void render(Game game) {

		for (int x = 0; x < width; x++) {
			depthBufferWall[x] = 0;
		}

		fov = height;
		xCenter = width / 2.0;
		yCenter = height / 3.0;

		xCam = game.player.x;
		yCam = game.player.y;
		zCam = 1.7;
		rot = Math.sin(game.time / 40.0) * 0.5;
		rot = game.player.rot;

		rSin = Math.sin(rot);
		rCos = Math.cos(rot);

		for (int y = 0; y < height; y++) {
			double yd = ((y + 0.5) - (yCenter)) / fov;
			double zd = (4 + zCam) / yd;
			if (yd < 0)
				zd = (4 - zCam) / -yd;

			for (int x = 0; x < width; x++) {
				double xd = (x - xCenter) / fov;
				xd *= zd;

				double xx = xd * rCos - zd * rSin + (xCam + 0.5) * 8;
				double yy = xd * rSin + zd * rCos + (yCam + 0.5) * 8;

				int xPix = (int) xx * 2;
				int yPix = (int) yy * 2;

				if (xx < 0)
					xPix--;
				if (yy < 0)
					yPix--;

				depthBuffer[x + y * width] = zd;
				pixels[x + y * width] = Textures.floors.pixels[(xPix & 15) + 16 | (yPix & 15) * Textures.floors.width];
			}
		}

		// renderWall(0, 2, 1, 2);
		// renderWall(0, 1, 0, 2);
		// renderWall(0, 0, 0, 1);
		// renderWall(1, 2, 1, 1);
		// renderWall(1, 1, 1, 0);

		Level level = game.level;
		for (int y = -1; y <= level.height; y++) {
			for (int x = -1; x <= level.width; x++) {
				Block c = level.getBlock(x, y);
				// east west south north
				Block e = level.getBlock(x + 1, y);
				Block w = level.getBlock(x - 1, y);
				Block s = level.getBlock(x, y - 1);
				Block n = level.getBlock(x, y + 1);

				if (!c.SOLID_RENDER)
					continue;

				if (!e.SOLID_RENDER)
					renderWall(x + 1, y, x + 1, y + 1);
				if (!w.SOLID_RENDER)
					renderWall(x, y + 1, x, y);
				if (!s.SOLID_RENDER)
					renderWall(x, y, x + 1, y);
				if (!n.SOLID_RENDER)
					renderWall(x + 1, y + 1, x, y + 1);
			}
		}

		// sprite jazz
		for (int y = -1; y <= level.height; y++) {
			for (int x = -1; x <= level.width; x++) {
				Block c = level.getBlock(x, y);

				for (int i = 0; i < c.sprites.size(); i++) {
					Sprite sprite = c.sprites.get(i);

					renderSprite(x + sprite.x, sprite.y, y + sprite.z);
				}
			}
		}
	}

	public void renderSprite(double x, double y, double z) {
		double xo = x - xCam;
		double yo = y + zCam / 8;
		double zo = z - yCam;

		double xx = xo * rCos + zo * rSin;
		double yy = yo;
		double zz = -xo * rSin + zo * rCos;

		if (zz < 0.1)
			return;

		double xPixel0 = xx / zz * fov + xCenter - (fov / 2) / zz;
		double xPixel1 = xx / zz * fov + xCenter + (fov / 2) / zz;
		double yPixel0 = yy / zz * fov + yCenter - (fov / 2) / zz;
		double yPixel1 = yy / zz * fov + yCenter + (fov / 2) / zz;

		int xp0 = (int) xPixel0;
		int xp1 = (int) xPixel1;
		int yp0 = (int) yPixel0;
		int yp1 = (int) yPixel1;

		if (xp0 < 0)
			xp0 = 0;
		if (xp1 > width)
			xp1 = width;
		if (yp0 < 0)
			yp0 = 0;
		if (yp1 > height)
			yp1 = height;

		zz *= 8;
		for (int yp = yp0; yp < yp1; yp++) {
			double pry = (yp - yPixel0) / (yPixel1 - yPixel0);
			int yt = (int) (pry * 16.0);

			for (int xp = xp0; xp < xp1; xp++) {
				double prx = (xp - xPixel0) / (xPixel1 - xPixel0);
				int xt = (int) (prx * 16.0);

				if (depthBuffer[xp + yp * width] > zz) {
					int col = Textures.floors.pixels[(xt & 15) + 64 + (yt & 15) * Textures.floors.width];
					if (col != 0xff00ff) {
						pixels[xp + yp * width] = col;
						depthBuffer[xp + yp * width] = zz;
					}
				}
			}
		}
	}

	public void renderWall(double x0, double y0, double x1, double y1) {
		double xo0 = x0 - 0.5 - xCam;
		double u0 = -0.5 + zCam / 8;
		double d0 = 0.5 + zCam / 8;
		double zo0 = y0 - 0.5 - yCam;

		double xx0 = xo0 * rCos + zo0 * rSin;
		double zz0 = -xo0 * rSin + zo0 * rCos;

		double xo1 = x1 - 0.5 - xCam;
		double u1 = -0.5 + zCam / 8;
		double d1 = 0.5 + zCam / 8;
		double zo1 = y1 - 0.5 - yCam;

		double xx1 = xo1 * rCos + zo1 * rSin;
		double zz1 = -xo1 * rSin + zo1 * rCos;

		double t0 = 0;
		double t1 = 16;

		double clip = 0.1;

		if (zz0 < clip && zz1 < clip) {
			return;
		}

		if (zz0 < clip) {
			double p = (clip - zz0) / (zz1 - zz0);
			zz0 = zz0 + (zz1 - zz0) * p;
			xx0 = xx0 + (xx1 - xx0) * p;
			t0 = t0 + (t1 - t0) * p;
		}
		if (zz1 < clip) {
			double p = (clip - zz1) / (zz1 - zz0);
			zz1 = zz1 + (zz1 - zz0) * p;
			xx1 = xx1 + (xx1 - xx0) * p;
			t1 = t1 + (t1 - t0) * p;
		}

		double xPixel0 = xx0 / zz0 * fov + xCenter;
		double xPixel1 = xx1 / zz1 * fov + xCenter;

		if (xPixel0 > xPixel1)
			return;
		int xp0 = (int) Math.ceil(xPixel0);
		int xp1 = (int) Math.ceil(xPixel1);
		if (xp0 < 0)
			xp0 = 0;
		if (xp1 > width)
			xp1 = width;

		double yPixel00 = (u0 / zz0 * fov + yCenter) + 0.5;
		double yPixel10 = (u1 / zz1 * fov + yCenter) + 0.5;
		double yPixel01 = (d0 / zz0 * fov + yCenter) + 0.5;
		double yPixel11 = (d1 / zz1 * fov + yCenter) + 0.5;

		double iz0 = 1 / zz0;
		double iz1 = 1 / zz1;

		double xt0 = t0 * iz0;
		double xt1 = t1 * iz1;

		for (int x = xp0; x < xp1; x++) {
			double p = (x - xPixel0) / (xPixel1 - xPixel0);
			double yPixel0 = yPixel00 + (yPixel10 - yPixel00) * p;
			double yPixel1 = yPixel01 + (yPixel11 - yPixel01) * p;
			double iz = iz0 + (iz1 - iz0) * p;

			if (depthBufferWall[x] > iz)
				continue;
			depthBufferWall[x] = iz;

			int xTex = (int) ((xt0 + (xt1 - xt0) * p) / iz);

			if (yPixel0 > yPixel1)
				return;
			int yp0 = (int) yPixel0;
			int yp1 = (int) yPixel1;
			if (yp0 < 0)
				yp0 = 0;
			if (yp1 > height)
				yp1 = height;

			for (int y = yp0; y < yp1; y++) {
				double py = (y - yPixel0) / (yPixel1 - yPixel0);
				int yTex = (int) (py * 16);

				depthBuffer[x + y * width] = 8 / iz;
				// depthBuffer[x + y * width] = 0;
				pixels[x + y * width] = Textures.floors.pixels[(xTex & 15) + 32 + ((yTex & 15) * Textures.floors.width)];
			}
		}
	}

	public void renderFloor(double xx, double yy, double yd, int x, int y, int xPix, int yPix, int pX, int pY) {
		if (yd >= 0 && xx >= pX * 16 && xx < pX * 16 + 16 && yy >= pY * 16 && yy < pY * 16 + 16) {
			pixels[x + y * width] = Textures.floors.pixels[(xPix & 15) + 16 | (yPix & 15) * Textures.floors.width];
		}
	}

	public void renderCeiling(double xx, double yy, double yd, int x, int y, int xPix, int yPix, int pX, int pY) {
		if (yd <= 0 && xx >= pX * 16 && xx < pX * 16 + 16 && yy >= pY * 16 && yy < pY * 16 + 16) {
			pixels[x + y * width] = Textures.floors.pixels[(xPix & 15) + 16 | (yPix & 15) * Textures.floors.width];
		}
	}

	// col = colour
	public void renderFog() {
		for (int i = 0; i < depthBuffer.length; i++) {
			// abs = absolute value of... ie removes the negative (if any)
			double t = Math.abs(i % width - width / 2.0) / (width / 1.5);

			int col = pixels[i];
			int r = (col >> 16) & 0xff;
			int g = (col >> 8) & 0xff;
			int b = (col) & 0xff;

			double brightness = 255 - (depthBuffer[i] * 3 * (t * t * 3 + 1));

			if (brightness < 0)
				brightness = 0;
			if (brightness > 255)
				brightness = 255;

			r = (int) (r / 255.0 * brightness);
			g = (int) (g / 255.0 * brightness);
			b = (int) (b / 255.0 * brightness);

			pixels[i] = r << 16 | g << 8 | b;
		}
	}
}
