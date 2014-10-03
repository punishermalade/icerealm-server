package com.icerealm.server.request;

/**
 * this custom exception class is used to have a distinct exception when a request 
 * is blocked
 * @author neilson
 *
 */
public class RequestBlockedException extends Exception {

	/**
	 * an automated serial version UID
	 */
	private static final long serialVersionUID = -7138876088645885676L;
	
	/**
	 * default constructor to allow a custom message to be written. It uses
	 * the Exception class construction
	 * @param msg the message of the exception
	 */
	public RequestBlockedException(String msg) {
		super(msg);
	}

}
