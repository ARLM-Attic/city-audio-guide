package com.gerken.audioGuideTests;

import java.util.Hashtable;

import com.gerken.audioGuide.interfaces.ViewStateContainer;

public class SimpleViewStateContainer implements ViewStateContainer {
	private Hashtable<String, Boolean> _boolBag = new Hashtable<String, Boolean>();
	private Hashtable<String, Integer> _intBag = new Hashtable<String, Integer>();
	private Hashtable<String, Float> _floatBag = new Hashtable<String, Float>();

	@Override
	public boolean getBoolean(String key) {
		return _boolBag.containsKey(key) ? _boolBag.get(key) : false;
	}

	@Override
	public int getInt(String key) {
		return _intBag.containsKey(key) ? _intBag.get(key) : 0;
	}

	@Override
	public float getFloat(String key) {
		return _floatBag.containsKey(key) ? _floatBag.get(key) : 0;
	}

	@Override
	public void putBoolean(String key, boolean value) {
		_boolBag.put(key, value);
	}

	@Override
	public void putFloat(String key, float value) {
		_floatBag.put(key, value);		
	}

	@Override
	public void putInt(String key, int value) {
		_intBag.put(key, value);		
	}

}
