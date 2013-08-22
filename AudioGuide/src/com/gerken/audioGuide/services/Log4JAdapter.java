package com.gerken.audioGuide.services;

import org.apache.log4j.Logger;

public class Log4JAdapter implements com.gerken.audioGuide.interfaces.Logger{
	private final Logger _log;
	
	public Log4JAdapter(String logName) {
		_log =  Logger.getLogger(logName);
	}
	
	public Log4JAdapter(Class cls) {
		_log =  Logger.getLogger(cls);
	}

	@Override
	public void logError(String message) {
		_log.error(message);		
	}

	@Override
	public void logError(String message, Throwable tr) {
		_log.error(message, tr);		
	}

	@Override
	public void logDebug(String message) {
		_log.debug(message);		
	}

}
