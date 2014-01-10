package com.gerken.audioGuide.interfaces;

public interface ViewStateContainer {
	boolean getBoolean(String key);
	int getInt(String key);
	float getFloat(String key);
	
	void putBoolean(String key, boolean value);
	void putFloat(String key, float value);
	void putInt(String key, int value);
}
