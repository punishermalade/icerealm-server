package com.icerealm.server.web;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.icerealm.server.request.RequestBlocker;


/**
 * This class implements a simple blocker based on IP adress read from a text file
 * @author neilson
 *
 */
public class IPAddressBlocker extends RequestBlocker {

	/**
	 * default logger for the entire project
	 */
	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * Contains the list of blocked address in a set, for performence when searching
	 */
	private Set<String> _blockedAddress = null;	
	
	/**
	 * default constructor
	 */
	public IPAddressBlocker() {
		_blockedAddress = new HashSet<String>();
	}

	@Override
	public boolean isBlocked(Object s) {	
		// working with socket direclty to get the ip adresses
		try {
			return _blockedAddress.contains(((Socket)s).getInetAddress().getHostAddress());
		}
		catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "IPAddressBlocker tries to work with Socket but cannot get a valid instance", ex);
		}
		return false;
	}

	@Override
	public void storeTemplateInMemory(String s) {
		_blockedAddress.add(s);
	}

	@Override
	public boolean isValidTemplate(String s) throws Exception {
		
		// validating IP adresses, will try to create a InetAddress.
		try {
			InetAddress.getByName(s);
			return true;
		}
		catch (UnknownHostException uhex) {
			return false;
		}
		catch (Exception ex) {
			throw ex;
		}
	}
}
