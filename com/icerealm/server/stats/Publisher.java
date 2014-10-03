package com.icerealm.server.stats;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * represents a simple publisher designed to write statistics into files. it handles the add/removal operation
 * for the Statistic instances. override those functions for a customized behavior
 * @author neilson
 *
 */
public abstract class Publisher {
	
	/**
	 * the default folder for the stats to be written, override it to customize
	 */
	public String DEFAULT_STAT_FOLDER = "stats";
	
	/**
	 * he default file separator. this field is made public only to finetuned the Publisher.
	 * Defaut value is system <b>System.getenv("file.separator")</b>
	 * 
	 */
	public String DEFAULT_STAT_FILE_SEPARATOR = System.getenv("file.separator");
	
	/**
	 * define the new line character to be appended after every stat been streamed in
	 */
	public String DEFAULT_STAT_NEW_LINE = System.getenv("new.line"); 
		
	/**
	 * the default filename for the stats, override it to customize
	 */
	public static String DEFAULT_STAT_FILE = "stats.dat";
		
	/**
	 * the default LOGGER, if you want ot use your own, override the value
	 */
	public static Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * the file writer to persist data, get renewed every time there is publishing
	 */
	private FileWriter out = null;
	
	/**
	 * the collection of statistic to be persisted
	 */
	protected Collection<Statistic> _statsColl = null;
	
	/**
	 * protected construtror that initialise a HashSet<Statistic>;
	 */
	protected Publisher() {
		_statsColl = new HashSet<Statistic>();
	}
	
	/**
	 * add a new statistic to be persisted
	 * @param stat a valid instance of Statistic, recommended to implement toString();
	 */
	public void addStat(Statistic s) {
		_statsColl.add(s);
	}
	
	/**
	 * remove the instance from the collection of stat. this method
	 * will return null on default implementation; override this
	 * this method for customized removal of stat behavior
	 * @param s the statistic to be remove
	 * @return null on default Publisher class; override this class to customize return type
	 */
	public Statistic removeStat(Statistic s) {
		try {
			_statsColl.remove(s);
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, "problem removing stat from collection", ex);
		}
		// instead of a finally statement
		return null;
	}
			
	/**
	 * this function open a file write stream and loop through the 
	 * current Statistic object list, recording all information
	 * the stream is close after this function call
	 * @param stat A collection containing instances of Statistic
	 */
	public void publishStats(Collection<Statistic> stat) {
		
		// open the file stream and will log any error, for debugging purpose
		try {
			out = new FileWriter(new File(DEFAULT_STAT_FOLDER + 
								 System.getenv("file.separator") +
								 DEFAULT_STAT_FILE));
			
			// going through the stat collection
			for (Statistic s : stat) {
				out.write(s.toString());
				out.write(DEFAULT_STAT_NEW_LINE);
			}
						
			//flushing and writing the informations to the file		
			out.flush();
			out.close();
			
		}
		catch (IOException ex) {
			LOGGER.log(Level.WARNING, "Problem writing in stat file", ex);
		}				
	}	
}