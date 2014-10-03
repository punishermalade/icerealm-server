package com.icerealm.server.web.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Read a file at a specified location and returns an array of byte. The only byte are the
 * file content.
 * @author neilson
 * 
 */
public class FileHandler extends ContentHandler {
	
	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * Represents the current file that will be written into an
	 * a array of byte
	 */
	protected File _currentFile = null;
	
	@Override
	public byte[] writeContent(String s) {
		try {

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			File file = new File(getRootFolder() + s.toString());
			LOGGER.fine("FileHandler: " + s);
			
			if (!file.isDirectory()) {
				FileInputStream stream = new FileInputStream(file);

				int byteRead = 0;
				while ((byteRead = stream.read()) != -1) {
					buffer.write(byteRead);
				}
				
				stream.close();
				buffer.flush();
				buffer.close();
			}
					
			return buffer.toByteArray();
		}
		catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Problem while writing content to byte array: " + ex.getMessage(), ex);
		}
		
		return new byte[0];
	}
}
