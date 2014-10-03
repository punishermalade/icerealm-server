package com.icerealm.server.config;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class acts like a DTO when the WebContainer reads the jar files and the configuration
 * files inside them.
 * @author punisher
 *
 */
@Deprecated
public class AppConfig {

	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * The application name
	 */
	private String _appName = null;
	
	/**
	 * The port that will be used to accept the request
	 */
	@Deprecated
	private Integer _port = 0;
	
	/**
	 * The class name to be used by the ClassLoader
	 */
	private String _className = null;
	
	/**
	 * An indicator to know if this configuration is valid
	 */
	private boolean _isValid = true;
	
	/**
	 * An instance of the ConfigManager
	 */
	private ConfigManager _configManager = null;
	
	/**
	 * Default constructor, leaves the attributes to null.
	 */
	public AppConfig() { }
	
	/**
	 * Uses the ConfigManager to load all the property and search for the
	 * mandatory field that should be included in the configuration file
	 * for a plugin
	 * @param stream An inputstream that points to the file
	 */
	public AppConfig(InputStream stream) {
		try {
			// loading the configuration manager from a stream
			_configManager = new ConfigManager();
			_configManager.loadConfiguration(stream);
			
			// getting the propoerty we need for the plugin
			_appName = _configManager.getPropertyValue("appname").toString();
			_className = _configManager.getPropertyValue("implementation").toString();
			
			// throwing a better exception
			if (_appName == null || _className == null) {
				throw new Exception("appname or implementation keys missing in config file");
			}
		}
		catch (Exception e) {
			LOGGER.log(Level.WARNING, e.getMessage(), e);
			_isValid = false;
		}
	}
	
	/**
	 * Represents if the configuration is valid
	 * @return True if valid, otherwise false
	 */
	public boolean isValidConfig() {
		return _isValid;
	}
	
	/**
	 * The application name
	 * @return The application name
	 */
	public String getAppName() {
		return _appName;
	}
	
	public void setAppName(String a) {
		_appName = a;
	}
	
	/**
	 * The port to be used by this application
	 * @return The port to be used by this application
	 */
	@Deprecated
	public int getPort() {
		return _port;
	}
	
	public void setPort(int i) {
		_port = i;
	}
	
	/**
	 * The class name to be used by the ClassLoader
	 * @return The class name to be used by the ClassLoader
	 */
	public String getClassName() {
		return _className;
	}
	
	public void setClassName(String c) {
		_className = c;
	}
}
