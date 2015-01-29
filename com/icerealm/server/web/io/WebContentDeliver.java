package com.icerealm.server.web.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class delivers content that is requested by a remote client. It is optimized for 
 * delievering web content. It uses a cache mechanism to avoid costly IO operation and it
 * compresses the content to reduce the bandwidth.
 * @author neilson
 *
 */
public class WebContentDeliver extends ContentHandler {

	/**
	 * the logger
	 */
	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * represents the default index filename for each directory
	 */
	private String _defaultIndexFilename = "index.html";
	
	/**
	 * represent the MIME type that are associated with specific files
	 */
	private Map<String, String> _extensionContentType = null;
	
	/**
	 * represents the cache handler
	 */
	private CachedContentDeliver _cachedHandler = null;
	
	/**
	 * default constructor, it initialize the MIME type table
	 */
	public WebContentDeliver() {
		
		_cachedHandler = new CachedContentDeliver();
		
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

	@Override
	public byte[] writeContent(String s) {
		
		byte[] fileContent = new byte[0];
		
		try {
			String ressourcePath = getRootFolder() + s;
			File file = new File(ressourcePath);
			
			if (file.isDirectory()) {
				
				File[] listOfFiles = getFileList(file);
				
				if (listOfFiles.length > 0) {
					fileContent = getFileContent(new File(ressourcePath + System.getProperty("file.separator") + _defaultIndexFilename));
				}
				else {
					fileContent = getFordiddenRessourceContent(s);
				}
			}
			else if (!file.exists()) {
				fileContent = getFileNotFoundContent(s);
			}
			else {
				fileContent = getFileContent(file);
			}
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error while delivering content for: " + s, ex);
			try {
				fileContent = getErrorServerContent(s);
			}
			catch (Exception e) {
				LOGGER.log(Level.SEVERE, "IO problem with the buffer: " + e.getMessage(), e);
			}
		}

		return fileContent;
	}
	
	/**
	 * Return the bytes that represent the file that is requested
	 * @param f the file
	 * @return a array of byte representing the file
	 * @throws Exception in case there is a problem with the buffer
	 */
	private byte[] getFileContent(File f) throws Exception {
		
		// writing the http header to the client
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		buffer.write("HTTP/1.1 200 OK\n".getBytes());
		buffer.write((getCurrentTime() + "\n").getBytes());
		buffer.write((getContentLentgh(f) + "\n").getBytes());
		buffer.write((getContentType(f) + "\n").getBytes());			
		buffer.write((getLastModified(f) + "\n").getBytes());				
		buffer.write("\n".getBytes());

		// using the cached content handler
		byte[] fileContent = _cachedHandler.getFileContent(f);
		buffer.write(fileContent);
		buffer.flush();
		buffer.close();
		return buffer.toByteArray();
	}
	
	/**
	 * return an array of byte representing a file not found http error
	 * @param ressource the requested ressource that could not be found
	 * @return an array of byte representing the content to be sent to the client
	 * @throws Exception in case there is IO error with the buffer
	 */
	private byte[] getFileNotFoundContent(String ressource) throws Exception {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		buffer.write("HTTP/1.1 404 File not found\n".getBytes());
		buffer.write("Content-Type: text/plain\n".getBytes());
		buffer.write("\n".getBytes());
		buffer.write("Error 404 File Not Found\n".getBytes());
		buffer.write(("Ressource not available: " + ressource).getBytes());		
		buffer.flush();
		return buffer.toByteArray();
	}
	
	/**
	 * return an array of byte representing a fordidden access error
	 * @param ressource the fordidden requested ressource
	 * @return an array of byte representing the message return to the client
	 * @throws Exception in case of IO error with the buffer
	 */
	private byte[] getFordiddenRessourceContent(String ressource) throws Exception {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		buffer.write("HTTP/1.1 403 Forbidden\n".getBytes());
		buffer.write("Content-Type: text/plain\n".getBytes());
		buffer.write("\n".getBytes());
		buffer.write("Error 403 Forbidden\n".getBytes());
		buffer.write("Resource is a directory, access denied".getBytes());
		buffer.flush();
		return buffer.toByteArray();
	}
	
	/**
	 * return an array of byte representing an internal server error 
	 * @param s the ressource that was requested
	 * @return an array of byte
	 * @throws Exception in case there is a problem with the buffer IO
	 */
	private byte[] getErrorServerContent(String s) throws Exception {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		buffer.write("HTTP/1.1 500 Server error\n".getBytes());
		buffer.write("Content-Type: text/plain\n".getBytes());
		buffer.write("\n".getBytes());
		buffer.write(("Ressource is available: " + s + " but server encoutered an internal error").getBytes());
		buffer.flush();
		return buffer.toByteArray();
	}
	
	/**
	 * return the list of file from a directory that meet the filter criteria
	 * @param f the directory to be searched
	 * @return a list of valid file
	 */
	private File[] getFileList(File f) {
		return f.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.equalsIgnoreCase(_defaultIndexFilename);
			}
		});
	}
	
	/**
	 * return the content length of a file
	 * @param f the file
	 * @return the lenght, in bytes
	 */
	private String getContentLentgh(File f) {
		return ("Content-Length: " + f.length());
	}
	
	/**
	 * return the current time
	 * @return the current timestamp
	 */
	@SuppressWarnings("deprecation")
	private String getCurrentTime() {
		return ("Date: " + new Timestamp(System.currentTimeMillis()).toGMTString());
	}
	
	/**
	 * return a valid MIME type for the file
	 * @param f the file
	 * @return a string representing a MIME type
	 */
	private String getContentType(File f) {
		String extension = f.getName().substring(f.getName().lastIndexOf(".") + 1);
		if (_extensionContentType.containsKey(extension)) {
			return "Content-Type: " + _extensionContentType.get(extension);
		}
		return "Content-Type: text/plain";
	}
	
	/**
	 * get the timestamp of the last time the file was modified
	 * @param f the file
	 * @return the string representing a timestamp
	 */
	@SuppressWarnings("deprecation")
	private String getLastModified(File f) {
		Timestamp tm = new Timestamp(f.lastModified());
		return "Last-Modified: " + tm.toGMTString();
	}
}
