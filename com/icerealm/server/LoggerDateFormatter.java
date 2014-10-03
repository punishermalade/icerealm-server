package com.icerealm.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LoggerDateFormatter extends Formatter {

	
	private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS");
	
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

	
}
