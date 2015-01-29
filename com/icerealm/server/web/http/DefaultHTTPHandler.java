package com.icerealm.server.web.http;

import java.io.OutputStream;
import com.icerealm.server.request.RequestEntireContent;
import com.icerealm.server.web.io.ContentHandler;

public class DefaultHTTPHandler implements HTTPMethodHandler {

	/**
	 * the default ContentHandler to be used in in this implementation
	 */
	private ContentHandler _contentHandler = null;
	
	/**
	 * the default index file
	 */
	private String _indexFile = "index.html";
	
	/**
	 * Default constructor
	 * @param content The ContentHandler to be used by this implementation 
	 */
	public DefaultHTTPHandler(ContentHandler content) {
		_contentHandler = content;
	}

	@Override
	public void registerContentHandler(ContentHandler contentHandler) {
		_contentHandler = contentHandler;
	}

	@Override
	public void handleHTTPOperation(RequestEntireContent content, OutputStream pw) {
		
		if (content.getFirstHeaderLine().contains("GET")) {
			String[] tokenized = content.getFirstHeaderLine().split(" ");
			String ressource = tokenized[1].substring(1);
			
			// construct the byte array for the wanted resource
			byte[] binContent = _contentHandler.writeContent(ressource);
			
			// write the content of the byte array and close the stream
			_contentHandler.writeIntoStream(pw, binContent, true);
		}
	}

	/**
	 * Append default index file if the request is empty
	 * @param r the request received
	 * @return appended index file if empty, otherwise the original request
	 */
	private String appendDefaultFile(String r) {
		String result = r;
		
		if (r.isEmpty()) {
			result = _indexFile;
		}
		/*
		else {
			
			// splitting this string for advanced processing
			String[] tokenized = r.split("/");
			
			// if the last token doesn't contain a ".", it's a directory
			if (!tokenized[tokenized.length - 1].contains(".")) {
				result += "/" + _indexFile;
			}
			
		}
		*/
		return result;
		
	}
}
