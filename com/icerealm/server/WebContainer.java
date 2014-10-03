package com.icerealm.server;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.icerealm.server.request.RequestHandler;

/**
 * this is the server bootstrap. the configuration is store in a XML file
 * @author neilson
 *
 */
public class WebContainer {

	/**
	 * Default logger that is customized
	 */
	private static Logger LOGGER = Logger.getLogger("Icerealm");
	
	
	/**
	 * will use the same Icerealm console log format for the WebServer Stat Engine
	 */
	public static String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS";
		
	/**
	 * The properties of the server
	 */
	private static String _pluginExtension 					= ".jar";
	private static String _defaultIndexFilename 			= "index.html";
	private static String _defaultServerLogFilename 		= "server.log";
	private static String _defaultPluginFolder 				= "plugins";
	private static String _defaultPluginNodeName 			= "plugin";
	private static String _defaultHandlerNodeName 			= "handler";
	private static String _defaultLibFolder 				= "lib";
	private static String _defaultLogFolder 				= "logs";
	private static String _defaultWWWFolder 				= "html";
	private static String _defaultGeneralConfig 			= "icerealm.config.xml";
	private static String _appNameIdentifier 				= "name";
	private static String _defaultRequestIPBlockedFile 		= "blocked.pattern.global";
	private static String _defaultRequestPatternBlockFile 	= "blocked.global";
	
	// config and path to resolve dependancy, ClassLoader available here
	private static Map<String, Map<String, String>> _generalPluginConfig 	= null;
	private static Map<String, Map<String, String>> _generalHandlerConfig 	= null;
	private static ClassLoader 						_classLoader 			= null;
	private static Handler 							_consoleHandler 		= null;

	/**
	 * this main method create the logger (console and file), load the files into the 
	 * class loader, read the XML config file and instanciate the plugins.
	 * @param args none
	 */
	public static void main(String[] args) { 
		
		try {
		
			//
			// setting the console logger and start logging, logging level from the config file
			//
			createConsoleLogger();
						
			//
			//	creating directories and default files if not existing
			//
			createDirectories();
			createDefaultFile();
			
			//
			// directories are created, we can now enable the file logger and 
			// display the default values
			//
			createFileLogger();
			displayInternalConfig();
			
			//
			//	reading all the jar files that needs to be visible to the class loader!
			//
			URL[] urls = loadJarFiles(new String[] { _defaultLibFolder, _defaultPluginFolder });
			
			//
			//	the class loader is loaded with all the files that we found
			//
			_classLoader = URLClassLoader.newInstance(urls, WebContainer.class.getClassLoader());	
			
			//
			// reading the XML config file and store the values in Map<String, Map<String, String>>
			//
			_generalPluginConfig = getGeneralConfig(_defaultPluginNodeName);
			_generalHandlerConfig = getGeneralConfig(_defaultHandlerNodeName);
			
			//
			//	instanciation of the plugins only
			//
			instanciatePlugins();
			
		}
		catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Could not start the server: " + ex.getMessage(), ex);
		}
	}
	
	/**
	 * return the default application name that should be in the XML config file.
	 * @return the default application name that should be in the XML config file.
	 */
	public static String getDefaultAppName() {
		return _appNameIdentifier;
	}
	
	/**
	 * the general handler config, to be used by the WebServer, can be used by other 
	 * RequestHandler implementation as well. It provides all the config from the
	 * handlers section
	 * @return a map containing a config map for all handlers in the XML config file
	 */
	public static Map<String, Map<String, String>> getHandlersConfig() {
		return _generalHandlerConfig;
	}
	
	/**
	 * the sole ClassLoader, easy access for plugins. Use this to load your own librairies. 
	 * if you omit that, you'll run into some issue using third party libraries (this server
	 * support EVERY thing that is runnable from a jar file!)
	 */
	public static ClassLoader getDefaultClassLoader() {
		return _classLoader;
	}
	
	private static void instanciatePlugins() {
		
		// using the general config plugin
		for (String appname : _generalPluginConfig.keySet()) {
			
			RequestHandler handle = null;
			Map<String, String> config = _generalPluginConfig.get(appname);
			
			try {
				// loading the class and checking if it's a RequestHandler
				Class<?> c = _classLoader.loadClass(config.get("class"));
				handle = (RequestHandler)c.newInstance();

				try {
					handle.onInit(config);						
				}
				catch (Exception ex) {
					// catching any exception here in case something went wrong, no need to prevent the app
					// from running
					LOGGER.log(Level.WARNING, "Problem while calling onInit for the plugin " + config.get(_appNameIdentifier) + ": " + ex.getMessage(), ex);
				}
				
				// we got the RequestHandler, now inject it to the generic server listener
				ServerListener listener = new ServerListener(handle);
				
				// get the application config
				listener.initConfig(config);
				
				// run it on another thread, for multiple instance
				int port = Integer.parseInt(config.get("port"));
				AppInitializer appInit = new AppInitializer(listener, port);
				
				// need a new thread for every listener - fix issue #8
				Thread t = new Thread(appInit);
				t.start();

				// let the user know we were able to start a thread for this plugin
				LOGGER.log(Level.INFO, "Application " + config.get(_appNameIdentifier) + " started and listening on port " + port);
				
			}
			catch (ClassCastException ex) {
				LOGGER.log(Level.WARNING, "Application " + config.get(_appNameIdentifier) + " generated a ClassCastException. " + config.get("class"));
			}
			catch (NumberFormatException ex) {
				LOGGER.log(Level.WARNING, "Application " + config.get(_appNameIdentifier) + " does not have a valid config file for class: " + config.get("class"));
			}
			catch (InstantiationException ex) {
				LOGGER.log(Level.WARNING, "Application " + config.get(_appNameIdentifier) + " cound not be instanciated: " + config.get("class"));
			}
			catch (Exception ex) {
				LOGGER.log(Level.WARNING, "failed to load application " + config.get(_appNameIdentifier) + " with class " + config.get("class"), ex);
			}
		}
		
	}
	
	@SuppressWarnings("deprecation")
	private static URL[] loadJarFiles(String[] folders) {
		List<URL> urls = new ArrayList<URL>();
		
		for (String f : folders) {
			File[] listFiles = getFileList(f, _pluginExtension);
			for (File file : listFiles) {
				try {
					urls.add(file.toURL());	
					LOGGER.info("Found a valid file: " + f + System.getProperty("file.separator") + file.getName());
				}
				catch (MalformedURLException ex) {
					LOGGER.log(Level.WARNING, "Could not add the file " + file.getName(), ex);
				}
			}
		}
		
		return urls.toArray(new URL[0]);
	}
	
	private static Map<String, Map<String, String>> getGeneralConfig(String nodeName) {
		
		Map<String, Map<String, String>> config = new HashMap<String, Map<String, String>>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			Document dom = db.parse(_defaultGeneralConfig);
			
			Element docEle = dom.getDocumentElement();
			
			//get a nodelist of elements
			NodeList nl = docEle.getElementsByTagName(nodeName);
			if(nl != null && nl.getLength() > 0) {
				for(int i = 0 ; i < nl.getLength();i++) {

					// creating a general config element
					Map<String, String> singleConfig = new HashMap<String, String>();
					
					// creating an appconfig for each plugin we find
					Element el = (Element)nl.item(i);
					
					NodeList elNodeList = el.getChildNodes();
					for (int j = 0; j < elNodeList.getLength(); j++) {
						Node nodeElement = elNodeList.item(j);							
						singleConfig.put(nodeElement.getNodeName(), nodeElement.getTextContent());
					}
					
					if (singleConfig.containsKey(_appNameIdentifier)) {
						config.put(singleConfig.get(_appNameIdentifier), singleConfig);
						
						LOGGER.info("Found a valid configuration from node " + nodeName + ": " + singleConfig.get(_appNameIdentifier));
					}
				}
			}
		
		}
		catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		catch(SAXException se) {
			se.printStackTrace();
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		return config;
	}

	private static File[] getFileList(String folder, final String extension) {
		// load all the jar file we find in the current directory, might have apps to start in them
		File dir = new File(folder);
		FilenameFilter filter = new FilenameFilter() {
			
			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.toLowerCase().endsWith(extension);
			}
		};
		
		return dir.listFiles(filter);
	}
	
	private static void displayInternalConfig() {
		LOGGER.info("Server started with the following values:");
		LOGGER.info("_pluginExtension: " + _pluginExtension);
		LOGGER.info("_defaultIndexFilename: " + _defaultIndexFilename);
		LOGGER.info("_defaultServerLogFilename: " + _defaultServerLogFilename);
		LOGGER.info("_defaultPluginFolder: " + _defaultPluginFolder);
		LOGGER.info("_defaultLibFolder: " + _defaultLibFolder);
		LOGGER.info("_defaultLogFolder: " + _defaultLogFolder);
		LOGGER.info("_defaultWWWFolder: " + _defaultWWWFolder);
		LOGGER.info("_defaultPluginNodeName: " + _defaultPluginNodeName);
		LOGGER.info("_defaultHandlerNodeName: " + _defaultHandlerNodeName);
		LOGGER.info("_defaultGeneralConfig: " + _defaultGeneralConfig);
		LOGGER.info("_appNameIdentifier: " + _appNameIdentifier);
	}
	
	// defining the default formatter to have nicer timestamp
	private static Formatter _formatter = new Formatter() {
						
		SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
		
		@Override
		public String format(LogRecord record) {
			StringBuffer buffer = new StringBuffer();					
			buffer.append("[" + formatter.format(new Date(System.currentTimeMillis())) + "]: " + 
							record.getMessage() + System.getProperty("line.separator"));
			Throwable throwable = record.getThrown();
			
			if (throwable != null) {
				for (StackTraceElement trace : throwable.getStackTrace()) {
					buffer.append("    " + trace + System.getProperty("line.separator"));
				}	
			}
			
			return buffer.toString();
		}
	};
	
	private static void createConsoleLogger() {
		// removing the handler from the global logger and replacing it with
		// our own stuff
		Handler[] handlers = Logger.getLogger("").getHandlers();
		for(Handler handler : handlers) {
			Logger.getLogger("").removeHandler(handler);
		}
		
		// creating another handler for the console
		_consoleHandler = new ConsoleHandler();
		_consoleHandler.setFormatter(_formatter);
		_consoleHandler.setLevel(getLoggingLevel("console", Level.INFO));
		LOGGER.addHandler(_consoleHandler);
		
		// sending message to the console logger
		LOGGER.info("Server starting...");
	}
	
	private static void createFileLogger() {
		
		try {		
			// creating a file handler to log in the server log file
			FileHandler fHandler = new FileHandler(_defaultLogFolder + System.getProperty("file.separator") + _defaultServerLogFilename, true);
			fHandler.setFormatter(_formatter);
			fHandler.setLevel(getLoggingLevel("file", Level.CONFIG));
			LOGGER.addHandler(fHandler);
		}
		catch (Exception ex) {
			LOGGER.log(Level.SEVERE, 
					   "An error occured while initializing the logger, check the values in " + _defaultGeneralConfig,
					   ex);
		}
	}
	
	private static void createDirectories() {
		String[] dirs = new String[] { _defaultPluginFolder, _defaultLogFolder, _defaultLibFolder, _defaultWWWFolder };
		for (String s : dirs) {
			
			try {
				File dir = new File(s);
				if (!dir.exists()) {
					LOGGER.info(s + " not found. Creating an empty folder.");
					dir.mkdir();
				}
				else {
					LOGGER.info(s + " folder found!");
				}
			}
			catch (Exception ex) {
				LOGGER.log(Level.SEVERE, "Error with " + s + " folder: " + ex.getMessage(), ex);
			}		
		}
	}
	
	private static void createDefaultFile() {
		
		try {
			File f = new File(_defaultGeneralConfig);
			if (!f.exists()) {

				Writer w = new FileWriter(f);
				w.write(_DEFAULTCONFIGFILE);
				w.flush();
				w.close();
				
				LOGGER.info(_defaultGeneralConfig + " file not found. Creating a default configuration file.");
			}
			else {
				LOGGER.info(_defaultGeneralConfig + " configuration file found!");
			}
			
			File indexFile = new File(_defaultWWWFolder + System.getProperty("file.separator") + _defaultIndexFilename);
			if (!indexFile.exists()) {
				Writer w = new FileWriter(indexFile);
				w.write(_DEFAULT_INDEX_FILE);
				w.flush();
				w.close();
				
				LOGGER.info(_defaultIndexFilename + " file not found. Creating a default index web page.");
			}
			
			File blockedIP = new File(_defaultRequestIPBlockedFile);
			if (!blockedIP.exists()) {
				blockedIP.createNewFile();
				LOGGER.info(_defaultRequestIPBlockedFile + " file not found. Creating an empty one.");
			}
			
			File blockedPattern = new File(_defaultRequestPatternBlockFile);
			if (!blockedPattern.exists()) {
				blockedPattern.createNewFile();
				LOGGER.info(_defaultRequestPatternBlockFile + " file not found. Creating an empty one.");
			}
						
		}
		catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Problem while creating default files: " + ex.getMessage(), ex);
		}
	}
	
	private static Level getLoggingLevel(String prop, Level defaultLevel) {
		
		// read the xml config file
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			Document dom = db.parse(_defaultGeneralConfig);
			
			Element docEle = dom.getDocumentElement();
			
			//get a nodelist of elements
			NodeList nl = docEle.getElementsByTagName("logging");
			if(nl != null && nl.getLength() > 0) {
				for(int i = 0 ; i < nl.getLength();i++) {

					// creating a general config element
					Node node = nl.item(i);
					NodeList elNodeList = node.getChildNodes();
					for (int j = 0; j < elNodeList.getLength(); j++) {
						Node nodeElement = elNodeList.item(j);							
						if (nodeElement.getNodeName().equalsIgnoreCase(prop)) {
							return Level.parse(nodeElement.getTextContent());
						}
					}
				}
			}
		}
		catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Could not read logging level for " + prop + ". Returning default value " + defaultLevel.toString(), ex);
		}
		
		return defaultLevel;
	}
	
	
	private static final String _DEFAULTCONFIGFILE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
													 "<icerealm>\n\n" +
													 "	<!-- server logging configuration, possible value SEVERE, WARNING, INFO, FINE, FINER, FINEST, OFF, ALL -->\n" +
													 "	<logging>\n" +
													 "		<console>FINE</console>\n" +
													 "		<file>INFO</file>\n" +
													 "	</logging>\n\n" +
													 "	<!-- the list of plugins to be used when the server starts -->\n" +
													 "	<plugins>\n" +
		 											 "	<!-- default Web Server -->\n" +
		 											 "		<plugin>\n" +
		 											 "			<name>WebServer</name>\n" +
		 											 "			<active>true</active>\n" + 
		 											 "			<class>com.icerealm.server.web.WebServerHandler</class>\n" +
		 											 "			<port>80</port>\n" +
		 											 "			<publicfolder>html</publicfolder>\n" +
		 											 "			<thread>20</thread>\n" +
		 											 "			<!--\n" +
		 											 "			this is optional. If there is no custom handler, you can remove\n" +
		 											 "			this node. if you create your own handler, the name in this node\n" +
		 											 "			must match the name attribute in the handler section.\n" +
		 											 "			Ex.: <handlers>myOwnHandler;mySecondHandler</handlers>\n" +
		 											 "			-->\n" +
		 											 "			<!--\n" +
		 											 "			<handlers>defaultGEThandler;mySecondHandler</handlers>\n" +
		 											 "			-->\n" +
		 											 "		</plugin>\n" +
		 											 "	</plugins>\n" +
		 											 "	<!-- the list of handler to be used with the web server (web app) -->\n" +
		 											 "	<handlers>\n" +
		 											 "		<!-- this is a simple handler example, the <name> must be the same from\n" +
		 											 "		<handlers> node in the WebServer config\n" +
		 											 "		-->\n" +
		 											 "		<!--\n" +
		 											 "		<handler>\n" +
		 											 "			<name>defaultGEThandler</name>\n" +
		 											 "			<active>true</active>\n" +
		 											 "			<class>com.icerealm.server.web.http.DefaultGETHandler</class>\n" +
		 											 "			<path>/</path>\n" +
		 											 "		</handler>\n" +
		 											 "		-->\n" +
		 											 "	</handlers>\n" +
		 											 "</icerealm>\n";

	private static final String _DEFAULT_INDEX_FILE = "<html>" +
													  "<head>" +
													  "<title>Icerealm Server Test Page</title>" +
													  "</head>" +
													  "<body>" +
													  "<h1>Icerealm Web Servier is running!</h1>" +
													  "<p>If you see this text, you successfully installed the Icerealm Server and it is currently running.</p>" +
													  "<p>Create your own <i>RequestHandler</i> to have absolute control over the client <i>Socket</i></p>" +
													  "<p>Extend the class <i>PureWebSocketHandler</i> to start an application that let you communicate with client via WebSocket" +
													  "<p>Extend the class <i>ChainedHTTPHandler</i> to create a web application accessible from the default web server" +
													  "<p>Take a look at the source code on Google Code (search for 'icerealm') and participate to the project!" +
													  "</body>" +
													  "</html>";
}
