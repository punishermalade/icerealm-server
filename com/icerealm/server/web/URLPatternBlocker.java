package com.icerealm.server.web;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import com.icerealm.server.request.RequestBlocker;
import com.icerealm.server.request.RequestEntireContent;

public class URLPatternBlocker extends RequestBlocker {

	/**
	 * a default logger
	 */
	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * the set of url pattern to be blocked
	 */
	private Set<String> _urlPattern = null;

	/**
	 * default constructor
	 */
	public URLPatternBlocker() {
		_urlPattern = new HashSet<String>();
	}
	
	@Override
	public boolean isBlocked(Object s) {
		
		// casting the object to the desired working instance we
		// need for this implementation
		RequestEntireContent content = (RequestEntireContent)s;
		
		boolean blocked = false;
		Iterator<String> i = _urlPattern.iterator();
		
		// extracting the resources wanted by the client
		String[] tokenized = content.getFirstHeaderLine().split(" ");
		
		// need to do some checking to avoid exception
		String ressource = "";
		if (tokenized.length > 1 && tokenized[1].length() > 1) {
			ressource = tokenized[1].substring(1);
		}
		
		while (i.hasNext() && !blocked) {
			blocked = ressource.contains(i.next());
		}
		
		return blocked;
	}

	@Override
	public void storeTemplateInMemory(String s) {
		LOGGER.finest("Pattern " + s + " blocked");
		_urlPattern.add(s);
	}

	@Override
	public boolean isValidTemplate(String s) throws Exception {
		// since this is a template checker, we will accept anything
		// from the file
		return true;
	}

}
