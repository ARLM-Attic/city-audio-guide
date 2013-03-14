package com.gerken.audioGuide.services;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.SharedPreferenceStorage;

public class SharedPreferenceManager implements SharedPreferenceStorage, OnSharedPreferenceChangeListener {
	private final String KEY_ROUTE_ID = "currentRouteId";
	private final String KEY_LAST_SIGHT_ID = "lastSightId";
	
	private Context _context;
	
	private ArrayList<OnEventListener> _currentRouteChangedListeners;
	
	public SharedPreferenceManager(Context ctx) {
		_context = ctx;
		_currentRouteChangedListeners = new ArrayList<OnEventListener>();
		
		getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public boolean isRouteChosen() {
		return getSharedPreferences().contains(KEY_ROUTE_ID);
	}

	@Override
	public int getCurrentRouteId() {
		return getSharedPreferences().getInt(KEY_ROUTE_ID, 0);
	}

	@Override
	public void setCurrentRouteId(int id) {
		storeInt(KEY_ROUTE_ID, id);
	}

	@Override
	public void resetCurrentRoute() {
		remove(KEY_ROUTE_ID);		
	}

	@Override
	public int getLastSightId() {
		return getSharedPreferences().getInt(KEY_LAST_SIGHT_ID, 0);
	}

	@Override
	public void setLastSightId(int id) {
		storeInt(KEY_LAST_SIGHT_ID, id);
	}

	@Override
	public void setOnCurrentRouteChangedListener(OnEventListener listener) {
		_currentRouteChangedListeners.add(listener);		
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if(key==KEY_ROUTE_ID) {
			for(OnEventListener l: _currentRouteChangedListeners)
				l.onEvent();
		}
		
	}
	
	private SharedPreferences getSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(_context);
	}
	
	private void storeInt(String key, int value) {
		Editor ed = getSharedPreferences().edit();
		ed.putInt(key, value);
		ed.commit();	
	}

	private void remove(String key) {
		Editor ed = getSharedPreferences().edit();
		ed.remove(key);
		ed.commit();
	}

}
