package com.icerealm.server.web;

import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.icerealm.server.WebContainer;
import com.icerealm.server.WebContainer;
import com.icerealm.server.request.RequestBlockedException;
import com.icerealm.server.request.RequestBlocker;
import com.icerealm.server.request.RequestEntireContent;
import com.icerealm.server.request.RequestHandler;
import com.icerealm.server.socket.DefaultWebSocketHandler;
import com.icerealm.server.socket.WebSocket;
import com.icerealm.server.socket.WebSocketHandler;
import com.icerealm.server.stats.Publisher;
import com.icerealm.server.web.http.ChainedHTTPMethodHandler;
import com.icerealm.server.web.http.DefaultHTTPHandler;
import com.icerealm.server.web.http.HTTPMethodHandler;
import com.icerealm.server.web.io.CachedContentHandler;
import com.icerealm.server.web.io.ContentHandler;
import com.icerealm.server.web.io.WebFileHandler;

/**
 * This implementation acts as a simple Web Server that delivers any files 
 * that is stored in the root folder.
 * @author punisher
 *
 */
public class WebServerHandler implements RequestHandler {

	/**
	 * default logger
	 */
	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * the default file name for the ip blocked list
	 */
	private static final String DEFAULT_IP_BLOCKED = "blocked.global";
	
	/**
	 * the default file name for the url blocked list
	 */
	private static final String DEFAULT_URL_BLOCKED = "blocked.pattern.global";
	
	/**
	 * A content handler
	 */
	protected ContentHandler _contentHandler = null;
	
	/**
	 * The default root folder, will be used if the configuration map 
	 * does not contains a value
	 */
	protected String _rootFolder = "html";
	
	/**
	 * the default GET handler. handle normal GET request
	 */
	protected String _defaultGETHandlerName = "DefaultHandler";
	
	/**
	 * the default class for the chained get method handler
	 */
	protected String _defaultGETHandler = "com.icerealm.server.web.http.ChainedGETMethodHandler";
	
	/**
	 * The WebSocket callback to handle the webSocket request that are 
	 * valid according to the protocol
	 */
	protected WebSocketHandler _websocketHandler = null;
	
	/**
	 * Handles the HTTP method that is sent
	 */
	protected HTTPMethodHandler _httpOperationHandler = null;
	
	/**
	 * this request blocker will filter on already doubtful known IP address
	 */
	protected RequestBlocker _ipBlocker = null;
	
	/**
	 * this request blocker will filter based on URL pattern doubtful
	 */
	protected RequestBlocker _urlBlocker = null;
	
	/**
	 * Default constructor. Provide a simple implementation that let a website designer 
	 * a very lean webserver to provide static files, image, sound, etc...
	 */
	public WebServerHandler() {
		LOGGER.info("WebServerHandler initializing...");
		_contentHandler = new WebFileHandler(new CachedContentHandler());
		_httpOperationHandler = new DefaultHTTPHandler(_contentHandler);
		_websocketHandler = new DefaultWebSocketHandler();
		
		// init the ip addresse blocker
		LOGGER.info("Setting IP Address Blocker");
		_ipBlocker = new IPAddressBlocker();
		_ipBlocker.parseTemplate(new File(DEFAULT_IP_BLOCKED));
		
		// init the url pattern blocker
		LOGGER.info("Setting URL Pattern Blocker");
		_urlBlocker = new URLPatternBlocker();
		_urlBlocker.parseTemplate(new File(DEFAULT_URL_BLOCKED));
	}
	
	@Override
	public void handleClientRequest(Socket s) {

		InputStreamReader br = null;
		OutputStream pw = null;
		
		try {
			
			// blocking ip address is fast and easy
			if (_ipBlocker.isBlocked(s)) {
				throw new RequestBlockedException("Request from " + s.getInetAddress().getHostAddress() + " blocked");
			}
			
			// set the stream to read and write them easily
			br = new InputStreamReader(s.getInputStream());
			pw = s.getOutputStream();
			
			// reading the request header to know that to do
			RequestEntireContent requestContent = new RequestEntireContent(br);
			LOGGER.log(Level.INFO, "Client from " + s.getInetAddress().toString() + " requested: " + requestContent.getFirstHeaderLine());

			// blocking the url pattern
			if (_urlBlocker.isBlocked(requestContent)) {
				throw new RequestBlockedException("Request with pattern " + requestContent.getFirstHeaderLine() + 
												  " from " + s.getInetAddress().getHostAddress() + " blocked");	
			}
			
			// read the first line to determine what to do next
			_httpOperationHandler.handleHTTPOperation(requestContent, pw);
	
			
		}
		catch (RequestBlockedException rbex) {
			LOGGER.log(Level.INFO, rbex.getMessage());
		}
		catch (Exception e) {
			LOGGER.log(Level.WARNING, e.getMessage(), e);
		} 
	}

	@Override
	public void onInit(Map<String, String> config) {
		
		// this is some additional config for this request handler
		if (config.containsKey("publicfolder")) {
			_contentHandler.setRootFolder(config.get("publicfolder"));
		}
		else {
			_contentHandler.setRootFolder(_rootFolder);
		}
		
		LOGGER.log(Level.INFO, "Root folder from config: " + _contentHandler.getRootFolder());
		
		// checking if any handler are configured to be used here
		Map<String, Map<String, String>> allHandlerConfig = WebContainer.getHandlersConfig();
		
		// there is at least one configured handler
		if (allHandlerConfig.size() > 0) {
			
			LOGGER.info("Registering the different handlers");
			
			// need some reference to establish the links between successors
			ChainedHTTPMethodHandler first = null;
			ChainedHTTPMethodHandler previous = null;
			ChainedHTTPMethodHandler current = null;
			int success = 0, failure = 0;

			// setting the handlers list to be instanciated
			List<String> handlers = new ArrayList<String>();
			for (String key : allHandlerConfig.keySet()) {
				handlers.add(key);
			}
			
			// we need to have the default handler last, we create an 
			// entry in the general config and add the name last in the list
			Map<String, String> defaultHandlerConfig = new HashMap<String, String>();
			defaultHandlerConfig.put(WebContainer.getDefaultAppName(), _defaultGETHandlerName);
			defaultHandlerConfig.put("class", _defaultGETHandler);
			WebContainer.getHandlersConfig().put(_defaultGETHandlerName, defaultHandlerConfig);
			handlers.add(_defaultGETHandlerName);
						
			// going though the handler and instanciate it
			for (String s : handlers) {
				
				/*
				 * the default class loader is used to retreive the class instance from
				 * a external jar. If this is the first handler, this handler will be 
				 * the first to be called all the time. Only this first instance will
				 * be registered as the HTTP handler. If it's not the first handler, 
				 * the successor link is made after it is instanciated. the "previous" 
				 * handler will have the next instance as the successor. The default
				 * GET handler is added last all the time.
				 */
				try {
					
					Map<String, String> handlerConfig = allHandlerConfig.get(s);
					String className = handlerConfig.get("class");
					Class<?> clazz = WebContainer.getDefaultClassLoader().loadClass(className);

					// This is the first handle to be instanciate
					if (first == null) {
						first = (ChainedHTTPMethodHandler)clazz.newInstance();
						first.onInit(handlerConfig);
						first.registerContentHandler(_contentHandler);
						registerHTTPHandler(first);
						previous = first;
					}
					else {
						// this is not the first handler
						current = (ChainedHTTPMethodHandler)clazz.newInstance();
						current.onInit(handlerConfig);
						current.registerContentHandler(_contentHandler);
						
						// setting the successor of the preivous
						previous.setSuccessor(current);
						
						// the previous become the current
						previous = current;
					}
					
					LOGGER.info(s + " handler has been instanciated");
					success++;
					
				} 
				catch (ClassNotFoundException e) {
					LOGGER.log(Level.SEVERE, "ChainedHTTPMethodeHandler not found for classname " + s, e);
					failure++;
				} 
				catch (InstantiationException e) {
					LOGGER.log(Level.SEVERE, "Could not instanciate a ChainedHTTPMethodHandler " + s, e);
					failure++;
				} 
				catch (IllegalAccessException e) {
					LOGGER.log(Level.SEVERE, "IllegalAccessException for class " + s, e);
					failure++;
				}
			}

			LOGGER.log(Level.INFO, "Handler instanciated: " + success + " failed: " + failure);
		}
	}
	
	/**
	 * Register an instance of a WebSocketCallback. When a WebSocket upgrade request comes
	 * from a client, this callback will be called after the initial handshake.
	 * @param wsc
	 */
	public void registerWebsocketHandler(WebSocketHandler wsc) {
		_websocketHandler = wsc;
	}
	
	/**
	 * Register an instance of HTTPMethodHandler
	 * @param h
	 */
	public void registerHTTPHandler(HTTPMethodHandler h) {
		_httpOperationHandler = h;
	}
}