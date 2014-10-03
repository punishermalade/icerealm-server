package com.icerealm.server.request;

import java.net.Socket;

/**
 * Simple utility Runnable to use a request handler in a separate thread.
 * @author punisher
 *
 */
public class RequestWorker implements Runnable {

	/**
	 * The RequestHandler to be used
	 */
	private RequestHandler _handler = null;
	
	/**
	 * The client socket
	 */
	private Socket _socket = null;
	
	/**
	 * Default constructor
	 * @param handler The request handler
	 * @param socket The client socket
	 */
	public RequestWorker(RequestHandler handler, Socket socket) {
		_handler = handler;
		_socket = socket;
	}
	
	@Override
	public void run() {
		_handler.handleClientRequest(_socket);
	}
}
