package game;

import game.net.Client;
import gfx.Bitmap3D;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;
import level.Block;
import level.Level;
import level.SolidBlock;
import level.Sprite;

public class HostGame extends AbstractGame {

	public ServerSocket server;
	public ArrayList<Client> clients = new ArrayList<Client>();

	public HostGame() {
	}

	@Override
	public void load() {
		super.load();

		try {
			server = new ServerSocket(4891);
			new Thread(() -> {
				acceptConnections();
			}).start();
		} catch (IOException ex) {
			Logger.getLogger(HostGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
	}

	private void acceptConnections() {
		while (true) {
			try {
				Socket accept = server.accept();
				new Thread(() -> {
					handleClient(accept);
				}).start();
			} catch (IOException ex) {
				Logger.getLogger(HostGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
			}
		}
	}

	@Override
	public void render(Bitmap3D render) {
		super.render(render);

		synchronized (clients) {
			for (Client client : clients) {
				render.renderSprite(client.x, 0, client.y, 0, 0xFFFFC9);
			}
		}
	}

	private void handleClient(Socket socket) {
		Client client = new Client();
		try {
			BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
			BufferedInputStream in = new BufferedInputStream(socket.getInputStream());

			synchronized (clients) {
				clients.add(client);
			}

			while (true) {
				int opcode = in.read();
				if (opcode == NetLevel.COMMAND_SIZE) {
					out.write(level.width);
					out.write(level.height);
					out.flush();
				} else if (opcode == NetLevel.COMMAND_GET) {
					for (int y = 0; y < level.height; y++) {
						for (int x = 0; x < level.width; x++) {
							Block block = level.getBlock(x, y);
							if (block instanceof SolidBlock) {
								out.write(Block.NET_ID_SOLID);
							} else if (!block.sprites.isEmpty()) {
								out.write(Block.NET_ID_GRASS);
							} else if (x == Level.xSpawn && y == Level.ySpawn) {
								out.write(Block.NET_ID_SPAWN);
							} else {
								out.write(Block.NET_ID_EMPTY);
							}
						}
					}
					out.flush();
				} else if (opcode == NetLevel.COMMAND_UPDATE_POS) {
					// decode the player position
					byte[] bytes = new byte[8];

					// x
					in.read(bytes);
					double x = NetLevel.toDouble(bytes);

					// y
					in.read(bytes);
					double y = NetLevel.toDouble(bytes);

					client.x = x;
					client.y = y;
				} else if (opcode == NetLevel.COMMAND_GET_POSES) {
					// tell the client how many other clients there are
					// +1 because of the host, -1 because dont send a client itself
					out.write(clients.size());

					// send the others
					for (Client otherclient : clients) {
						if (otherclient == client) {
							continue;
						}
						sendClientUpdate(otherclient.x, otherclient.y, out);
					}

					// send ourself
					sendClientUpdate(player.x, player.y, out);

					// flush it
					out.flush();
				} else if (opcode == -1) {
//					String msg = "Bye\n";
//					out.write(msg.getBytes(), 0, msg.length());
//					out.flush();
					break;
				}
			}

			System.out.println("Connection to " + socket.getInetAddress().getHostAddress() + " closed.");
			out.close();
			in.close();
			socket.close();
		} catch (IOException ex) {
			Logger.getLogger(HostGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} finally {
			clients.remove(client);
		}
	}

	private void sendClientUpdate(double x, double y, BufferedOutputStream out) throws IOException {
		out.write(NetLevel.toByteArray(x));
		out.write(NetLevel.toByteArray(y));
	}

	@Override
	public Level loadLevel() {
		return Level.loadLevel("level0.png");
	}

}
