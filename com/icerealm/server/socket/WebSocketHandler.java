package com.icerealm.server.socket;

/**
 * This interface encapsulate the use of a WebSocket. It provides a convenient way to get
 * notified of a new accepted WebSocket connection and the subsequent received messages during
 * the execution of the application.
 * @author punisher
 *
 */
public interface WebSocketHandler {

	/**
	 * Get notified of a new successful connection by a client
	 * @param ws The WebSocket that is created. The handshake is complete at this point
	 */
	public void newConnection(WebSocket ws);
	
	/**
	 * Get notified when a WebSocket client send a message to the server
	 * @param message The content of the message
	 * @param ws The WebSocket, to be able to identify the client
	 */
	public void messageReceived(String message, WebSocket ws);
	
	/**
	 * Get notified when a connection is ended
	 * @param ws The closing WebSocket
	 */
	public void connectionEnded(WebSocket ws);
	
}
