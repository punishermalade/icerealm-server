package com.icerealm.server.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A singleton that encapsulates the read and write operation related
 * to a property files
 * @author punisher
 *
 */
@Deprecated
public class ConfigManager {

	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * The default comment line beggning
	 */
	private static final String COMMENT_LINE = "#";
	
	/**
	 * The default property separator
	 */
	private static final String PROPERTY_SEPARATOR = "=";
	
	/**
	 * The private static instance of this singleton
	 */
	private static ConfigManager _instance = null;
	
	/**
	 * The configuration values are store in a Map implementation.
	 */
	private Map<String, String> _configValues = null;
	
	/**
	 * A static method to retreive the unique instance of this singleton. 
	 * @return
	 */
	@Deprecated
	public static ConfigManager getInstance() {
		
		if (_instance == null) {
			_instance = new ConfigManager();
		}
		
		return _instance;
	}
	
	/**
	 * public constructor that initialise the Map attribute. It can be easily reuse without
	 * being a singleton.
	 */
	public ConfigManager() {
		_configValues = new HashMap<String, String>();
	}
	
	/**
	 * Read the filename provided and extract the property name
	 * and value from the file.
	 * @param filename The name of the file
	 */
	public void loadConfiguration(File filename) {
		try {
			loadConfiguration(new BufferedReader(new FileReader(filename)));
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, ex.getMessage(), ex);
		}
	}
	
	/**
	 * Populate the properties with the given BufferedReader
	 * @param reader An open BufferedReader instance
	 */
	public void loadConfiguration(BufferedReader reader) {
		try {
			
			// clear the values before reloading them in memory
			_configValues.clear();
			
			// read the property file
			String line = null;
			
			while ((line = reader.readLine()) != null) {
				
				if (!line.contains(COMMENT_LINE) && !line.isEmpty()) {
					String[] tokenizer = line.split(PROPERTY_SEPARATOR);
					if (tokenizer.length > 1) {
						_configValues.put(tokenizer[0], tokenizer[1]);
					}
				}
			}
			// done reading
			reader.close();
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, ex.getMessage(), ex);
		}
	}
	
	/**
	 * Populate the properties with the given InputStream. It creates the BufferedReader
	 * and call the loadConfiguration(BufferedReader reader) method.
	 * @param stream an open InputStream
	 */
	public void loadConfiguration(InputStream stream) {
		loadConfiguration(new BufferedReader(new InputStreamReader(stream)));
	}
	
	/**
	 * Load a configuration from a filename
	 * @param name A filename to load the configuration from
	 */
	public void loadConfiguration(String name) {
		loadConfiguration(new File(name));
	}
	
	/**
	 * Save the current state of the configuration property and values
	 * in a given filename
	 * @param filename The file to write the properties in memory.
	 */
	public void saveConfiguration(String filename) {
		try {
			
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File(filename)));
			for (String key : _configValues.keySet()) {
				pw.format("{0}{1}{2}", key, PROPERTY_SEPARATOR, _configValues.get(key));
			}
			pw.flush();
			pw.close();
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, ex.getMessage(), ex);
		}
	}
	
	/**
	 * Get the value for a given property
	 * @param propertyName the name of the property
	 * @return return the value, otherwise null
	 */
	public String getPropertyValue(String propertyName) {	
		
		try {
			return _configValues.get(propertyName);
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, ex.getMessage(), ex);
		}
		
		return null;
	}
		
	/**
	 * Set a property with the key and the value. This is only
	 * kept in memory. To persist the change, use saveConfiguration(String filename)
	 * method.
	 * @param name Property name
	 * @param property Property value
	 */
	public void setPropertyValue(String name, String property) {
		_configValues.put(name, property);
	}
	
	/**
	 * Provides a way to navigate through all the property
	 * @return A concrete instance of a Map
	 */
	public Set<String> getAllProperties() {
		return _configValues.keySet();
	}
}
