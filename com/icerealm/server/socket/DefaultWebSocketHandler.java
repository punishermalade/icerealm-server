package com.icerealm.server.socket;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultWebSocketHandler implements WebSocketHandler {

	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * The executor to be used when a new connection is received.
	 */
	private Executor _excecutor = null;
	
	/**
	 * Default constructor that uses a Single Thread Executor
	 */
	public DefaultWebSocketHandler() {
		_excecutor = Executors.newSingleThreadExecutor();
	}
	
	@Override
	public void newConnection(WebSocket ws) {
		try {
			ws.handshake();
			ws.send("connection established via websocket, now listening to your message");
			_excecutor.execute(new WebSocketListener(this, ws));
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, ex.getMessage(), ex);
		}
	}

	@Override
	public void messageReceived(String message, WebSocket ws) {
		LOGGER.log(Level.INFO, "Message from " + ws.getKey() + ": " + message);
	}

	@Override
	public void connectionEnded(WebSocket ws) {
		LOGGER.log(Level.INFO, "Close connection from " + ws.getKey());
	}
}
