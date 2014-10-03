package com.icerealm.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.icerealm.server.request.RequestHandler;
import com.icerealm.server.request.RequestWorker;


/**
 * A simple implementation that acts as a server that continuously listen
 * for request made by client. It uses a Dependency-Injection pattern to
 * handle the client requests.
 * @author punisher
 *
 */
public class ServerListener {

	/**
	 * default logger
	 */
	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * represent the number of default thread per pool
	 */
	private static final int DEFAULT_NUM_THREAD = 3;
	
	/**
	 * The socket used by the server to listen to client request
	 */
	private ServerSocket _serverSocket = null;
	
	/**
	 * An implementation on how to handle client request
	 */
	private RequestHandler _requestHandler = null;
	
	/**
	 * The executor to be used for every request
	 */
	private Executor _executor = null;
	
	/**
	 * Default constructor that uses a concrete RequestHandler to handler
	 * the client request
	 * @param handler A concrete RequestHandler
	 */
	public ServerListener(RequestHandler handler) {
		_requestHandler = handler;
	}
	
	/**
	 * Creates a ServerSocket that listen to a specific port and wait for
	 * client to send a request. This method blocks when the server waits 
	 * for a connection.
	 * @param port The port on which the server is listening.
	 */
	public void listen(int port) {
		
		try {
			_serverSocket = new ServerSocket(port);
			Socket socket = null;

			while (((socket = _serverSocket.accept()) != null)) {
				RequestWorker worker = new RequestWorker(_requestHandler, socket);
				_executor.execute(worker);
			}
			
			stop();

		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Problem with incoming socket connection", e);
		}
	}
	
	/**
	 * a handle to configure the ServerListener based on the application name
	 * @param config the map containing a list of key/value that represents a configuration
	 */
	public void initConfig(Map<String, String> config) {
		
		// setting the thread pool according to the configuration received
		int numThread = DEFAULT_NUM_THREAD;
		if (config.containsKey("thread")) {
			try {
				numThread = Integer.parseInt(config.get("thread"));
			}
			catch (NumberFormatException ex) {
				LOGGER.log(Level.WARNING, "Cannot parse the number of thread for server listener", ex);
			}
		}
		
		// setting the thread pool
		_executor = Executors.newFixedThreadPool(numThread);
	}
	
	/**
	 * Stop the server by calling the close() method on the ServerSocket
	 */
	public void stop() {
		try {
			_serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
