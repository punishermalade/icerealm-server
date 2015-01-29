package com.icerealm.server.web.io;

import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.icerealm.server.request.RequestEntireContent;

/**
 * Retreive a resource and produce an array of
 * byte to be written into a OutputStream.
 * @author punisher
 *
 */
public abstract class ContentHandler {

	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * the root folder to find the associated resource
	 */
	private String _rootFolder = "";
	/**
	 * Define how to write the content found at the resource location
	 * @param s a unique identifier to find a specific resource
	 * @return an array of byte that represents the resource
	 */
	public abstract byte[] writeContent(String s);
	
	public abstract byte[] writeContent(String s, RequestEntireContent req);
	
	/**
	 * Provide a way to set the root folder for a specified resource
	 * @param r the root folder. It can be empty, depending on the implementation of the ContentHandler
	 */
	public void setRootFolder(String r) {
		_rootFolder = r;
	}
	
	/**
	 * Returns the current root folder
	 * @return the current root folder
	 */
	public String getRootFolder() {
		if (!_rootFolder.equalsIgnoreCase("")) {
			return _rootFolder + System.getProperty("file.separator");
		}
		return _rootFolder;
	}
	
	/**
	 * Provide a convenient way to write an array of byte into a stream. It can be used to append
	 * more byte to an existing stream. It flushes the stream but do not close it
	 * @param out The OutputStream
	 * @param bin the binary content
	 */
	public void writeIntoStream(OutputStream out, byte[] bin) {
		writeIntoStream(out, bin, false);
	}
	
	/**
	*  Provide a convenient way to write an array of byte into a stream. It can be used to append
	 * more byte to an existing stream. It flushes the stream and it can be close after.
	 * @param out The OutputStream
	 * @param bin the binary content
	 * @param close Close the stream if true. If false, do not close it.
	 */
	public void writeIntoStream(OutputStream out, byte[] bin, boolean close) {
		try {
			if (bin != null && bin.length > 0) {
				out.write(bin);
				out.flush();
				
				if (close) {
					out.close();
				}
			}
		}
		catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Problem writing content to a stream: " + ex.getMessage(), ex);
		}
	}
}
