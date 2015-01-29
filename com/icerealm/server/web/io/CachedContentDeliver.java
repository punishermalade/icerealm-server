package com.icerealm.server.web.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * this class keeps the files that are requested in memory until the files
 * changes. the change is detected with the Last Modified field of the File
 * class
 * @author neilson
 *
 */
public class CachedContentDeliver {

	/**
	 * the logger
	 */
	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * the map that determine the last time a file was modified
	 */
	private Map<File, Long> _lastModified = null;
	
	/**
	 * the map that contains the byte for a file
	 */
	private Map<File, byte[]> _cachedContent = null;
	
	/**
	 * default constructor
	 */
	public CachedContentDeliver() {
		_lastModified = new HashMap<File, Long>();
		_cachedContent = new HashMap<File, byte[]>();
	}
	
	/**
	 * return the array of byte representing a requested file
	 * @param f the file to be sent
	 * @return the array of byte
	 */
	public byte[] getFileContent(File f) {
		
		if (!_lastModified.containsKey(f) || _lastModified.get(f) != f.lastModified()) {
			
			try {
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				FileInputStream stream = new FileInputStream(f);
				int byteRead = 0;
				while ((byteRead = stream.read()) != -1) {
					buffer.write(byteRead);
				}
				
				stream.close();
				buffer.flush();
				buffer.close();
				
				_lastModified.put(f, f.lastModified());
				_cachedContent.put(f, buffer.toByteArray());
				
			}
			catch (Exception ex) {
				LOGGER.log(Level.WARNING, "Error while writing file content to buffer: " + f, ex);
				return new byte[0];
			}				
		}
		
		return _cachedContent.get(f);
	}	
}
