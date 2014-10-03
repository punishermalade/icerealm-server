package com.icerealm.server.socket;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.icerealm.server.web.WebServerHandler;


/**
 * This class provides handler to inject your own code on different handler from the WebSocketHandler
 * interface. When a new connection is made, it handle the handshake mechanism and register the WebSocketHandler
 * with the underlying WebServer layer. It also handle the thread creation to receive message from the client
 * connected through WebSocket.
 * @author punisher
 *
 */
public abstract class ManagedWebSocketHandler extends WebServerHandler implements WebSocketHandler {
	
	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * This is called after the handshake mechanism
	 * @param ws The WebSocket connection from the client
	 * @throws Exception Thrown if there is a problem when using the WebSocket
	 */
	public abstract void onNewConnection(WebSocket ws) throws Exception;
	
	/**
	 * Once a connection is close by the client
	 * @param ws The WebSocket that is closed.
	 * @throws Exception Thrown if there is a problem when using the WebSocket
	 */
	public abstract void onConnectionEnded(WebSocket ws) throws Exception;
	
	/**
	 * This is called when a message is received from a client
	 * @param m The message content. Depending on the implementation, could be JSON, plain text, XML, etc...
	 * @param ws The WebSocket that is sending the message
	 * @throws Exception Thrown if there is a problem when using the WebSocket
	 */
	public abstract void onMessageReceived(String m, WebSocket ws) throws Exception;
	
	/**
	 * This constructor registers the WebSocketHandler so that the class can handle the requests
	 * from clients.
	 */
	public ManagedWebSocketHandler() {
		registerWebsocketHandler(this);
	}
	
	@Override
	public void newConnection(WebSocket ws) {
		try {
			ws.handshake();
			WebSocketListener.getWebSocketListener(this, ws);
			
			// calling the extending class implementation
			onNewConnection(ws);
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Problem while handling a new WebSocket connection: " + ex.getMessage(), ex);
		}
	}

	@Override
	public void messageReceived(String message, WebSocket ws) {
		try {
			// calling the extending class implementation
			onMessageReceived(message, ws);
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error while receiving websocket message: " + ex.getMessage(), ex);
		}
	}

	@Override
	public void connectionEnded(WebSocket ws) {
		try {
			// calling the extending class implementation
			onConnectionEnded(ws);
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error while handling websocket end connection: " + ex.getMessage(), ex);
		}
	}
}