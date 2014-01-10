package com.gerken.audioGuide.util;

import android.os.Bundle;

import com.gerken.audioGuide.interfaces.ViewStateContainer;

public class BundleViewStateContainer implements ViewStateContainer {
	private Bundle _bundle;
	
	public BundleViewStateContainer(Bundle bundle) {
		_bundle = bundle;
	}

	@Override
	public boolean getBoolean(String key) {
		return _bundle.getBoolean(key);
	}

	@Override
	public int getInt(String key) {
		return _bundle.getInt(key);
	}

	@Override
	public float getFloat(String key) {
		return _bundle.getFloat(key);
	}

	@Override
	public void putBoolean(String key, boolean value) {
		_bundle.putBoolean(key, value);
	}

	@Override
	public void putFloat(String key, float value) {
		_bundle.putFloat(key, value);
	}

	@Override
	public void putInt(String key, int value) {
		_bundle.putInt(key, value);
	}

}
