package game;

import game.net.Client;
import gfx.Bitmap3D;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;
import level.Level;

/**
 *
 * @author znix
 */
public class ClientGame extends AbstractGame {

	private BufferedInputStream in;
	private BufferedOutputStream out;

	private NetLevel netLevel;

	// double-buffer with the arrays: add
	// to one (not being drawn), then swap them around.
	// to prevent wierd rendering problems
	private ArrayList<Client> clients = new ArrayList<Client>();

	public static Socket connect() {
		try {
			Socket soc = new Socket("localhost", 4891);
			return soc;
		} catch (IOException ex) {
			// it's fine, no server is running
		}
		return null;
	}

	@Override
	public void update(boolean[] keys) {
		super.update(keys);

		try {
			out.write(NetLevel.COMMAND_UPDATE_POS); // what we are doing
			out.write(NetLevel.toByteArray(player.x)); // send the data
			out.write(NetLevel.toByteArray(player.y)); // send the data

			out.write(NetLevel.COMMAND_GET_POSES); // what we want (the other player positions)
			out.flush(); // make sure the data was sent - it is buffered until we call this

			int numclients = in.read(); // read the num of clients
			clients.clear();
			for (int i = 0; i < numclients; i++) {
				// decode the player position
				byte[] bytes = new byte[8];

				// x
				in.read(bytes);
				double x = NetLevel.toDouble(bytes);

				// y
				in.read(bytes);
				double y = NetLevel.toDouble(bytes);

				// add that client
				Client client = new Client();
				client.x = x;
				client.y = y;
				clients.add(client);
			}
		} catch (IOException ex) {
			Logger.getLogger(ClientGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
			System.exit(0); // server probably stopped
		}
	}

	@Override
	public void render(Bitmap3D render) {
		super.render(render);

		// render other players
		for (Client client : clients) {
			render.renderSprite(client.x, 0, client.y, 0, 0xFFFFC9);
		}
	}

	public ClientGame(Socket conn) {
		try {
			in = new BufferedInputStream(conn.getInputStream());
			out = new BufferedOutputStream(conn.getOutputStream());

			netLevel = NetLevel.loadNetLevel(in, out);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void load() {
		super.load();
	}

	@Override
	public Level loadLevel() {
		return netLevel;
	}

}
