package com.gerken.audioGuide.interfaces;

public interface Logger {
	void logError(String messgage);
	void logError(String messgage, Throwable tr);
	void logDebug(String messgage);
}
