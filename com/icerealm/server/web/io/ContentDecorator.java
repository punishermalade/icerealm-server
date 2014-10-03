package com.icerealm.server.web.io;


/**
 * A simple decorator that act as a base to add responsability to the 
 * ContentHandler
 * @author neilson
 *
 */
public class ContentDecorator extends ContentHandler {

	/**
	 * The reference to the ContentHandler
	 */
	private ContentHandler _contentReference = null;
	
	/**
	 * Default constructor that needs to have an instance of a ContentHandler
	 * @param h
	 */
	public ContentDecorator(ContentHandler h) {
		_contentReference = h;
	}
	
	@Override
	public byte[] writeContent(String s) {
		return _contentReference.writeContent(s);
	}

}
