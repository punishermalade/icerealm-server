package com.icerealm.server.web.io;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Use a cache system to deliver files from memory if the last modified date
 * changed since last loading from disk.
 * @author neilson
 *
 */
public class CachedContentHandler extends FileHandler {

	private Map<String, Long> _lastModified = null;
	private Map<String, byte[]> _cachedContent = null;
	
	/**
	 * Default constructor
	 */
	public CachedContentHandler() {
		_lastModified = new HashMap<String, Long>();
		_cachedContent = new HashMap<String, byte[]>();
	}
	
	@Override
	public byte[] writeContent(String s) {

		File file = new File(s);
		
		if (!_lastModified.containsKey(s) || _lastModified.get(s) != file.lastModified()) {
			byte[] data = super.writeContent(s);
			_lastModified.put(s, file.lastModified());
			_cachedContent.put(s, data);
		}
		return _cachedContent.get(s);
	}

}
