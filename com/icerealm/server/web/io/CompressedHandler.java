package com.icerealm.server.web.io;

import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;


/**
 * Compress the byte array using GZIPOutputStream from JDK
 * @author neilson
 *
 */
public class CompressedHandler extends ContentDecorator {

	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * Default constructor
	 * @param h A ContentHandler
	 */
	public CompressedHandler(ContentHandler h) {
		super(h);
	}
	
	@Override
	public byte[] writeContent(String s) {
		
		byte[] data = super.writeContent(getRootFolder() + s);
		ByteArrayOutputStream raw = new ByteArrayOutputStream();
	
		try {
			GZIPOutputStream compressed = new GZIPOutputStream(raw);
			compressed.write(data);
			compressed.finish();
			
			return raw.toByteArray();
			
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Problem while compressing content: " + ex.getMessage(), ex);
		}
		
		return new byte[0];
	}
}
