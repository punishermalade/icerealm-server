package com.icerealm.server.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class attemps to read all the InputStreamReader until this is at the end
 * of it.
 * @author punisher
 *
 */
public class RequestEntireContent {

	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * The stream that is used to read
	 */
	private InputStreamReader _stream = null;
		
	/**
	 * The content of the HTTP request header
	 */
	private List<String> _header = null;
	
	/**
	 * The content of the HTTP request body
	 */
	private List<String> _content = null;
	
	/**
	 * the content of the body response, for easy retreival
	 */
	private Map<String, String> _postContent = null; 
	
	/**
	 * Initialize the attributes and read the entire request. All the operations
	 * are made in the constructor one time.
	 * @param stream The stream that is used to read the content of the request
	 */
	public RequestEntireContent(InputStreamReader stream) {
		_stream = stream;
		_header = new ArrayList<String>();
		_content = new ArrayList<String>();
		readEntireRequest();
	}
	
	/**
	 * this constructor can be used to make a copy of a request or creating
	 * a new one
	 * @param h the HTTP request header
	 * @param c the body content
	 */
	public RequestEntireContent(List<String> h, List<String> c) {
		_header = h;
		_content = c;
	}
	
	/**
	 * Returns the first line of the request. Typically, it woul be a GET, POST, etc... Use this
	 * method to determine what the server should do with a particular request.
	 * @return a string that is the first line of the HTTP request, if the header empty, an empty string
	 */
	public String getFirstHeaderLine() {
		if (_header.size() > 0) {
			return _header.get(0);	
		}
		return "";
		
	}

	/**
	 * The complete request header
	 * @return A list of String that represents each header line sent by the client request
	 */
	public List<String> getRequestHeader() {
		return _header;
	}
	
	/**
	 * The complete request body
	 * @return a list of String that represents each body line sent by the client request
	 */
	public List<String> getRequestBody() {
		return _content;
	}
	
	/**
	 * return the value of a particular key from the body content
	 * @param key the key to look for
	 * @return the value found for this key, otherwise null
	 */
	public String getValueFromKey(String key) {
		if (_postContent == null && _content.size() > 0) {
			_postContent = new HashMap<String, String>();
			
			for (String s : _content) {
				String[] pair = s.split("=");
				_postContent.put(pair[0], pair[1]);
			}			
		}
		
		return _postContent.get(key);
	}
	
	/**
	 * Return the value of particular field of the HTTP request.
	 * @param key Represents the key that should be looked at
	 * @return The value based on the key. For example, if the key is 'Content-Type', then it would returns 'text/xml'.
	 */
	public String getHeaderLine(String key) {
		
		for (String s : _header) {
			if (s.toLowerCase().contains(key)) {
				return s.substring(s.lastIndexOf(":") + 1).trim();
			}
		}
		
		return null;
	}
	

	/**
	 * Read the entire request and saves it in memory
	 */
	private void readEntireRequest() {
		try {
		
			String contentLengthIdentifier = "Content-Length: ";
			BufferedReader br = new BufferedReader(_stream);
			String line = null;
			int bodyContentSize = 0;
			
			// read each line
			while ((line = br.readLine()) != null && !line.isEmpty()) {
				_header.add(line);
				
				// detecting the content length, to know the size of the body
				if (line.contains(contentLengthIdentifier)) {
					try {
						bodyContentSize = Integer.parseInt(line.substring(contentLengthIdentifier.length()));
					}
					catch (NumberFormatException ex) {
						LOGGER.log(Level.WARNING, "Content-Length from request not parsable: " + line, ex);
						bodyContentSize = 0;
					}
				}
			}
			
			// we know what to expect for the body content
			StringBuilder buffer = new StringBuilder();
			for (int i = 0; i < bodyContentSize; i++) {
				
				// read a character
				char c = (char)br.read();

				// reached one parameter
				if (c == '&') {
					_content.add(buffer.toString());
					buffer = new StringBuilder();
				}
				else {
					// continue to append character
					buffer.append(c);
				}
				
				// reached the last character
				if (i == (bodyContentSize - 1)) {
					_content.add(buffer.toString());
				}
			}
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Could not read raw client request: " + ex.getMessage(), ex);
		}
	}
}
