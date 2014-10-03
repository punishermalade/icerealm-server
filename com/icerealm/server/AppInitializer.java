package com.icerealm.server;

/**
 * A simple runnable class that uses a server listen and call the listen function. It has to be
 * executed on another thread in order to have the same machine listening on different ports. 
 * The ServerListener.listen(int port) is a blocking operation.
 * @author punisher
 *
 */
public class AppInitializer implements Runnable {

	/**
	 * The ServerListener to be used.
	 */
	private ServerListener _server = null;
	
	/**
	 * The port on which to listen
	 */
	private int _port = 0;
	
	/**
	 * Use the ServerListener and the port to listen for client request
	 * @param s
	 * @param port
	 */
	public AppInitializer(ServerListener s, int port) {
		_server = s;
		_port = port;
	}
	
	@Override
	public void run() {
		_server.listen(_port);
	}
}
