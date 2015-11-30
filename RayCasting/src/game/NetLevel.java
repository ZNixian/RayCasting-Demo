package game;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import level.Block;
import level.Level;
import level.SolidBlock;
import level.Sprite;

/**
 * Wraps around a level, providing a easy way to load the server's level into
 * this game.
 *
 * @author znix
 */
public class NetLevel extends Level {

	public static final int COMMAND_SIZE = 'a' + 1; // get the size of the map
	public static final int COMMAND_GET = 'a' + 2; // get the map
	public static final int COMMAND_UPDATE_POS = 'a' + 3; // client -> server position update
	public static final int COMMAND_GET_POSES = 'a' + 4; // ask the server for player positions

	/**
	 * Encodes a double value into a byte array
	 *
	 * @param value The value to encode
	 * @return the encoded form of {@code value}
	 */
	public static byte[] toByteArray(double value) {
		byte[] bytes = new byte[8];
		ByteBuffer.wrap(bytes).putDouble(value);
		return bytes;
	}

	/**
	 * Decodes a byte array into a double
	 *
	 * @param bytes The array of bytes encoded via {@link #toByteArray(double)}
	 * @return
	 */
	public static double toDouble(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getDouble();
	}

	/**
	 * Loads a level over the network
	 * @param in
	 * @param out
	 * @return
	 * @throws IOException 
	 */
	public static NetLevel loadNetLevel(BufferedInputStream in, BufferedOutputStream out) throws IOException {

		out.write(NetLevel.COMMAND_SIZE);
		out.flush();

		int width = in.read();
		int height = in.read();

		out.write(NetLevel.COMMAND_GET);
		out.flush();

		Block tile[] = new Block[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Block block = new Block();

				int type = in.read();

				if (type == Block.NET_ID_SOLID) {
					block = new SolidBlock();
					block.col = 0x667CDB & 0x555555;
				} else if (type == Block.NET_ID_SPAWN) {
					Level.xSpawn = x;
					Level.ySpawn = y;
				} else if (type == Block.NET_ID_GRASS) {
					block.addSprite(new Sprite(0, 0, 0, 1, 0x003300));
				}
				// set roof and floor colours
				if (type != Block.NET_ID_SOLID) {
					block.ceilCol = 0x550055;
					block.floorCol = 0x550000;
				}
				tile[x + y * width] = block;
			}
		}

		return new NetLevel(tile, width, height);
	}

	public NetLevel(Block[] tile, int width, int height) {
		super(null, width, height);

		this.tile = tile;
	}

}
