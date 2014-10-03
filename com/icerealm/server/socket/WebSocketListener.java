package com.icerealm.server.socket;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This class listen continuously to a WebSocket in a separate thread. The read operation
 * of the WebSocket is a blocking operation. This class provides a efficient way to listen
 * and be notified of a new message by a WebSocket
 * @author punisher
 *
 */
public class WebSocketListener implements Runnable {

	/**
	 * The client WebSocket
	 */
	private WebSocket _webSocket = null;
	
	/**
	 * The server callback that get notified when a message is sent by the WebSocket
	 */
	private WebSocketHandler _wsCallback = null;
	
	private Executor _executor = null;
	
	/**
	 * Returns an instance of a WebSocketListener. It starts it immediately.
	 * @param cb WebSocketHandler instance
	 * @param ws The WebSocket
	 * @return A running WebSocketListener
	 */
	public static WebSocketListener getWebSocketListener(WebSocketHandler cb, WebSocket ws) {
		WebSocketListener listen = new WebSocketListener(cb, ws, true);	
		return listen;
	}
	
	/**
	 * Assign the callback and the WebSocket to the member of the class. When this 
	 * Runnable is executed, it will be in an infinite loop, waiting for a message to be
	 * received by a client. When a message is receive, the callback is notified of this.
	 * @param cb The callback that is used to communicate
	 * @param ws The WebSocket that send a message
	 */
	public WebSocketListener(WebSocketHandler cb, WebSocket ws) {
		this(cb, ws, false);
	}
	
	/**
	 * Build the WebSocketListener
	 * @param cb WebSocket Handler instance
	 * @param ws WebSocket instance
	 * @param now if true, this runnable will be start now, if false, it will not be started
	 */
	public WebSocketListener(WebSocketHandler cb, WebSocket ws, boolean now) {
		_executor = Executors.newSingleThreadExecutor();
		_wsCallback = cb;
		_webSocket = ws;
		if (now) {
			_executor.execute(this);
		}
	}
	
	@Override
	public void run() {
		try {
			String message = null;
			while ((message = _webSocket.read()) != null) {
				_wsCallback.messageReceived(message, _webSocket);
			}
		}
		catch (Exception ex) {
			_wsCallback.connectionEnded(_webSocket);
		}
	}
}
