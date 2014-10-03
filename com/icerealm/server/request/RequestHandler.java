package com.icerealm.server.request;

import java.net.Socket;
import java.util.Map;

/**
 * Define a way to handle a Socket when it is connecting to the server. Typically,
 * it could be used for different kind of request (from a browser, a native Java 
 * client, etc...)
 * @author punisher
 *
 */
public interface RequestHandler {
	
	/**
	 * Handle a request from a client.
	 * @param s The client socket
	 */
	public void handleClientRequest(Socket s);
	
	/**
	 * Gets called at creation, to do initialization work, if necessary
	 */
	public void onInit(Map<String, String> config);
	
}
