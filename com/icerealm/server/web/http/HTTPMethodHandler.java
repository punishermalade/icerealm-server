package com.icerealm.server.web.http;

import java.io.OutputStream;
import com.icerealm.server.request.RequestEntireContent;
import com.icerealm.server.web.io.ContentHandler;

/**
 * Provide a way to handle requests from client. It allows any ContentHandler 
 * to be used.
 * @author neilson
 *
 */
public interface HTTPMethodHandler {
	
	/**
	 * Register a ContentHandler to be the default one. Depending on the implementation
	 * it may be added to a list of replaced.
	 * @param contentHandler
	 */
	public void registerContentHandler(ContentHandler contentHandler);

	/**
	 * Handle the HTTP request that are received. The RequestEntireContent object
	 * contains all the information to process a request. The OutputStream can 
	 * be used to send data back to the client
	 * @param c Request content, as received from the client
	 * @param w Client OutputStream, let the implementation writes anything to it
	 */
	public void handleHTTPOperation(RequestEntireContent c, OutputStream w);

}
