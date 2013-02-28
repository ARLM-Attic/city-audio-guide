package com.gerken.audioGuide.services;

import android.util.Log;

import com.gerken.audioGuide.interfaces.Logger;

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

}
