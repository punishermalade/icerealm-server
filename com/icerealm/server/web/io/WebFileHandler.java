package com.icerealm.server.web.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This decorator adds a HTTP header when a request is made to the server
 * @author neilson
 *
 */
public class WebFileHandler extends ContentDecorator {

	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	private Map<String, String> _extensionContentType = null;
	
	
	/**
	 * This implementation adds a HTTP header to enable the diffusion
	 * of the content to a web browser
	 * @param h a ContentHandler
	 */
	public WebFileHandler(ContentHandler h) {
		super(h);
		
		// defining the entensionContentType
		_extensionContentType = new HashMap<String, String>();
		_extensionContentType.put("html", "text/html");
		_extensionContentType.put("htm", "text/html");
		_extensionContentType.put("ico", "image/x-icon");
		_extensionContentType.put("jpeg", "image/jpeg");
		_extensionContentType.put("jpg", "image/jpeg");
		_extensionContentType.put("png", "image/png");
		_extensionContentType.put("gif", "image/gif");
		_extensionContentType.put("css", "text/css");
		_extensionContentType.put("js", "text/javascript");
		_extensionContentType.put("dart", "application/dart");
		_extensionContentType.put("ogg", "application/ogg");
		_extensionContentType.put("wav", "audio/x-wav");
	}
	
	/**
	 * Constructor that accept a list of extension content type 
	 * @param h The content handler to be used
	 * @param ext The extension table to be used (pair file type/MIME type);
	 */
	public WebFileHandler(ContentHandler h, Map<String, String> ext) {
		super(h);
		_extensionContentType = ext;
	}
	
	@Override
	public byte[] writeContent(String s) {
		
		LOGGER.fine("WebFileHandler writeContent: " + s);

		// get the file, to know what to write on the header
		File file = new File(getRootFolder() + s);
				
		// contains the file data
		byte[] data = super.writeContent(getRootFolder() + s);
		
		// need a stream for output
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		try {
			
			if (file.exists() && !file.isDirectory()) {
				buffer.write("HTTP/1.1 200 OK\n".getBytes());
				buffer.write((getCurrentTime() + "\n").getBytes());
				buffer.write((getContentLentgh(file) + "\n").getBytes());
				buffer.write((getContentType(file) + "\n").getBytes());			
				buffer.write((getLastModified(file) + "\n").getBytes());				
				buffer.write("\n".getBytes());
				
				// writing the rest
				buffer.write(data);
			}
			else if (!file.isDirectory()) {
				buffer.write("HTTP/1.1 404 File not found\n".getBytes());
				buffer.write("Content-Type: text/plain\n".getBytes());
				buffer.write("\n".getBytes());
				buffer.write("Error 404 File Not Found\n".getBytes());
				buffer.write(("Ressource not available: " + s).getBytes());				
			}
			else {

				buffer.write("HTTP/1.1 403 Forbidden\n".getBytes());
				buffer.write("Content-Type: text/plain\n".getBytes());
				buffer.write("\n".getBytes());
				buffer.write("Error 403 Forbidden\n".getBytes());
				buffer.write("Resource is a directory, access denied".getBytes());
			}
			
			buffer.flush();
		}
		catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Server error occured while writing content to a stream: " + ex.getMessage(), ex);
			try {
				buffer.write("HTTP/1.1 500 Server error\n".getBytes());
				buffer.write("Content-Type: text/plain\n".getBytes());
				buffer.write("\n".getBytes());
				buffer.write(("Ressource is available: " + s + " but server encoutered an internal error").getBytes());
				buffer.flush();
			}
			catch (Exception e) {
				LOGGER.log(Level.SEVERE, "IO problem with the buffer: " + e.getMessage(), e);
			}
		}

		return buffer.toByteArray();
	}
	
	private String getContentLentgh(File f) {
		return ("Content-Length: " + f.length());
	}
	
	@SuppressWarnings("deprecation")
	private String getCurrentTime() {
		return ("Date: " + new Timestamp(System.currentTimeMillis()).toGMTString());
	}
	
	private String getContentType(File f) {
		String extension = f.getName().substring(f.getName().lastIndexOf(".") + 1);
		if (_extensionContentType.containsKey(extension)) {
			return "Content-Type: " + _extensionContentType.get(extension);
		}
		return "Content-Type: text/plain";
	}
	
	@SuppressWarnings("deprecation")
	private String getLastModified(File f) {
		Timestamp tm = new Timestamp(f.lastModified());
		return "Last-Modified: " + tm.toGMTString();
	}

}
