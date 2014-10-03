package com.icerealm.server.web;

import java.util.logging.Logger;

import com.icerealm.server.WebContainer;
import com.icerealm.server.stats.Statistic;

public class PingRequest implements Statistic {

	/** 
	 * default Logger
	 */
	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * start time of the request
	 */
	private long startTime = 0;
	
	/**
	 * end time of the request
	 */
	private long endTime = 0;
	
	/**
	 * empty constrcutor. use this class with the static function createNewRequest.
	 */
	public PingRequest() {
	}
	
	
	public static PingRequest createNewRequest() {
		return new PingRequest();
	}
	
	public PingRequest start() {
		startTime = System.currentTimeMillis();
		return this;
	}
	
	public long getRunningInterval() {
		return System.currentTimeMillis() - startTime;
	}
	
	public long getFinalInterval() {
		return endTime - startTime;
	}
	
	public void stop() {
		endTime = System.currentTimeMillis();
	}
	
	@Override
	public String toString() {
		return "Elasped time is " + getFinalInterval() + "ms";
	}
	
}
