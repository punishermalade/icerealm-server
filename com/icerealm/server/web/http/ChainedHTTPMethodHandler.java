package com.icerealm.server.web.http;

import java.io.OutputStream;
import java.util.Map;

import com.icerealm.server.request.RequestEntireContent;
import com.icerealm.server.web.io.ContentHandler;

/**
 * This class represent a chain of handler that can be used in a web server. It contains
 * the logic to pass the request to the successor if it cannot handle it. Common use is
 * to implements very specific handling scenario and inject the handler into the WebServer
 * class.
 * @author neilson
 *
 */
public abstract class ChainedHTTPMethodHandler implements HTTPMethodHandler {

	/**
	 * The default contentHandler for this specific handler
	 */
	private ContentHandler _contentHandler = null;
	
	/**
	 * the successor for this handler
	 */
	private ChainedHTTPMethodHandler _successor = null;
	
	/**
	 * determine if this handler allow the successor to be called even
	 * if the request has been handled with no error 
	 * @return true if the sucessor is called no matter what, otherwise false
	 */
	public abstract boolean pushToSuccessor();
	
	/**
	 * determine if this handler can perform the required action
	 * @param request The content of the request, header and values
	 * @return true if the request can be handled, otherwise false
	 */
	public abstract boolean canHandleRequest(RequestEntireContent request);
	
	/**
	 * this function contains all the specialized logic to handle the request
	 * @param c the entire request content
	 * @param w the output stream to write the content
	 */
	public abstract void specializedHandleHTTPOperation(RequestEntireContent c, OutputStream w);
	
	/**
	 * let the handler receive a configuration object.
	 * @param config a Map containing the configuration for the handler
	 */
	public abstract void onInit(Map<String, String> config);
	
	/**
	 * Set the successor for this handler
	 * @param h the successor in the chain, can be null. If it is null the request will stop to be passed around in this handler
	 */
	public void setSuccessor(ChainedHTTPMethodHandler h) {
		_successor = h;
	}
	
	@Override
	public void registerContentHandler(ContentHandler contentHandler) {
		_contentHandler = contentHandler;
	}

	@Override
	public void handleHTTPOperation(RequestEntireContent c, OutputStream w) {
		// validating if this handler can handle the request
		if (canHandleRequest(c)) {
			specializedHandleHTTPOperation(c, w);
		}
		
		if (_successor != null && pushToSuccessor()) {
			_successor.handleHTTPOperation(c, w);
		}
	}
	
	/**
	 * return the current instance of the content handler
	 * @return the content handler
	 */
	public ContentHandler getContentHandler() {
		return _contentHandler;
	}
	
}