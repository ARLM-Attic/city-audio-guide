package com.gerken.audioGuide.services.logging;

import com.gerken.audioGuide.interfaces.Logger;

import android.util.Log;

public class DefaultLoggingAdapter implements Logger {
	private String _tag;
	
	public DefaultLoggingAdapter(String tag) {
		_tag = tag;
	}

	@Override
	public void logError(String message) {
		Log.e(_tag, message);		
	}

	@Override
	public void logError(String message, Throwable tr) {
		Log.e(_tag, message, tr);		
	}

	@Override
	public void logDebug(String message) {
		Log.d(_tag, message);		
	}

	@Override
	public void logWarning(String message) {
		Log.w(_tag, message);		
	}
}
