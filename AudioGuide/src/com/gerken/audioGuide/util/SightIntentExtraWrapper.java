package com.gerken.audioGuide.util;

import android.content.Intent;

public class SightIntentExtraWrapper {
	private final String KEY_IS_DEMO_MODE = "isDemoMode";
	private final boolean DEFAULT_IS_DEMO_MODE = false;
	
	private Intent _intent;
	
	public SightIntentExtraWrapper(Intent intent) {
		_intent = intent;
	}
	
	public boolean getIsDemoMode() {
		return _intent.getBooleanExtra(KEY_IS_DEMO_MODE, DEFAULT_IS_DEMO_MODE);
	}
	public void setIsDemoMode(boolean isDemoMode){
		_intent.putExtra(KEY_IS_DEMO_MODE, isDemoMode);
	}

}
