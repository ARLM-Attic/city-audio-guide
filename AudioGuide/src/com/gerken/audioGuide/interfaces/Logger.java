package com.gerken.audioGuide.interfaces;

public interface Logger {
	void logError(String message);
	void logError(String message, Throwable tr);
	void logWarning(String message);
	void logDebug(String message);
}
