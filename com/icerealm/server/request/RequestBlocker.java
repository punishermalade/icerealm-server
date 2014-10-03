package com.icerealm.server.request;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class abstract a RequestBlocker and let the developper creating it's own condition to block
 * any request that comes on the server.
 * @author neilson
 *
 */
public abstract class RequestBlocker {

	/**
	 * defaut logger
	 */
	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * Determine if the incoming socket must be blocked or not
	 * @param s the client socket
 	 * @return true if it's blocked, otherwise false
	 */
	public abstract boolean isBlocked(Object s);
	
	/**
	 * Provides a way to store any template in memory. It can be used without parsing file.
	 * @param s the template to be checked when determining if a request must be blocked
	 */
	public abstract void storeTemplateInMemory(String s);
	
	/**
	 * fonction that let the implementation validate the template that is found in a file. In case
	 * there is no validation needed, simply return true in the implementation
	 * @param s the tempate to be validated
	 * @return true if the template is valid, otherwise false
	 * @throws Exception in case there is an exception not verified by the code logic
	 */
	public abstract boolean isValidTemplate(String s) throws Exception;
	
	/**
	 * parse a file and extract the template from it. each template should be terminate by a newline
	 * character (enter key).
	 * @param f the file that needs to be read.
	 */
	public void parseTemplate(File f) {
		
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line = null;
			
			// parsing the file
			while ((line = reader.readLine()) != null) {
				
				try {
					
					if (isValidTemplate(line)) {
						storeTemplateInMemory(line);
					}
				}
				catch (Exception netex) {
					LOGGER.log(Level.WARNING, "Problem while parsing template", netex);
				}
				
			}
		}
		catch (FileNotFoundException ex) {
			LOGGER.log(Level.WARNING, "Could not find the file", ex);
		}
		catch (IOException ioex) {
			LOGGER.log(Level.WARNING, "Could not read the file", ioex);
		}
	}
	
}
