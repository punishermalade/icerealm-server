package com.icerealm.server.interaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.icerealm.server.socket.WebSocket;

/**
 * This class handles multiple user that connect to the server. It provides simple method
 * to add, remove and send message to all the users on the list.
 * @author punisher
 *
 */
public class MultiUserHandler<T> {

	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	private Map<WebSocket, T> _webUsers = null;

	/**
	 * the list of user and their websocket
	 */
	private Map<T, WebSocket> _users = null;

	/**
	 * Manage the list of users through their websocket. 
	 */
	public MultiUserHandler() {
		_webUsers = new HashMap<WebSocket, T>();
		_users = new HashMap<T, WebSocket>();
	}
	
	/**
	 * Add a T instance with a Websocket key
	 * @param s the WebSocket corresponding this T instance
	 * @param name the T instance associated to the WebSocket
	 * @return true if the websocket was added; 
	 * return false if the websocket is already in the list
	 */
	public boolean addUser(WebSocket s, T name) {
		if (!_users.containsKey(s)) {
			// add T user
			_users.put(name, s);
			
			// add WebSocket user
			_webUsers.put(s, name);
			return true;
		}
		return false;
	}
	
	/**
	 * remove a websocket from the list
	 * @param webSocket the websocket to remove
	 * @return true if the websocket was in the list; otherwise false
	 */
	public boolean removeUser(WebSocket webSocket) {
		if (_webUsers.containsKey(webSocket)) {
			// remove the T users
			_users.remove(_webUsers.get(webSocket));
			
			// remove the WebSocket users
			_webUsers.remove(webSocket);
			return true;
		}
		return false;
	}
	
	public T getUserFromWebSocket(WebSocket socket) {
		return _webUsers.get(socket);
	}
	
	/**
	 * 
	 * @param message
	 */
	public void sendToAll(String message) {
		for (WebSocket ws : _webUsers.keySet()) {
			try {
				ws.send(message);
			}
			catch (Exception ex) {
				LOGGER.log(Level.WARNING, "Could not send message to " + ws.getKey() + ": " + ex.getMessage(), ex);
			}
		}
	}
	
	public boolean sendToClient(T user, String message) {
		
		WebSocket socket = _users.get(user);
		if (socket != null) {
			this.sendToWebSocket(socket, message);
			return true;
		}
		return false;
	}
	
	public void sendToWebSocket(WebSocket websocket, String message) {
		try {
			websocket.send(message);
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Could not send message to " + websocket.getKey() + ": " + ex.getMessage(), ex);
		}
	}
	
	public List<T> getAllUsers() {
		List<T> list = new ArrayList<T>();
		for (T u : _users.keySet()) {
			list.add(u);
		}
		
		return list;
	}
}