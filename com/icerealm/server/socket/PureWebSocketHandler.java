package com.icerealm.server.socket;

import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.icerealm.server.request.RequestEntireContent;
import com.icerealm.server.request.RequestHandler;

/**
 * this class is an simple implementation of the web socket handler. it does take the 
 * Socket direclty and make the necessary handshake when a new connection is detected.
 * @author neilson
 *
 */
public abstract class PureWebSocketHandler implements RequestHandler, WebSocketHandler {

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
	
	@Override
	public void handleClientRequest(Socket s) {
		
		try {
			
			// reading the socket request
			InputStreamReader isr = new InputStreamReader(s.getInputStream());
			RequestEntireContent content = new RequestEntireContent(isr);
			String key = null;
			
			// checking if the client wants to initiate a websocket communication
			if ((key = content.getHeaderLine("sec-websocket-key")) != null) {
				this.newConnection(new WebSocket(s, key));
			}
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Could not handle request from socket: " + ex.getMessage(), ex);
		}
	}

	@Override
	public void onInit(Map<String, String> config) {
		// nothing to do
	}

	@Override
	public void messageReceived(String message, WebSocket ws) {
		try {
			// calling the extending class implementation
			this.onMessageReceived(message, ws);
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error while receiving websocket message: " + ex.getMessage(), ex);
		}
	}

	@Override
	public void connectionEnded(WebSocket ws) {
		try {
			// calling the extending class implementation
			this.onConnectionEnded(ws);
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error while handling websocket end connection: " + ex.getMessage(), ex);
		}
	}

	@Override
	public void newConnection(WebSocket ws) {
		try {
			ws.handshake();
			WebSocketListener.getWebSocketListener(this, ws);
			
			// calling the extending class implementation
			this.onNewConnection(ws);
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Problem while handling a new WebSocket connection: " + ex.getMessage(), ex);
		}
	}
}
