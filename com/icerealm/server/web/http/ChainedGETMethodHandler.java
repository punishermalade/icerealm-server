package com.icerealm.server.web.http;

import java.io.OutputStream;
import java.util.Map;

import com.icerealm.server.request.RequestEntireContent;
import com.icerealm.server.web.io.ContentHandler;

/**
 * This class uses the DefaultHTTPMethodHandler to deliver any GET request
 * content. The Composition pattern has been used for this implementation. 
 * @author neilson
 *
 */
public class ChainedGETMethodHandler extends ChainedHTTPMethodHandler {

	/**
	 * the default http handler that comes with the web server
	 */
	private DefaultHTTPHandler _handler = null;
	
	@Override
	public boolean canHandleRequest(RequestEntireContent request) {
		if (request.getFirstHeaderLine().contains("GET")) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean pushToSuccessor() {
		return true;
	}
	
	@Override
	public void registerContentHandler(ContentHandler h) {
		if (_handler == null) {
			_handler = new DefaultHTTPHandler(h);
		}
	}

	@Override
	public void specializedHandleHTTPOperation(RequestEntireContent c, OutputStream w) {
		_handler.handleHTTPOperation(c, w);
	}

	@Override
	public void onInit(Map<String, String> config) {
		// no config are expected
	}

}
