package com.gerken.audioGuide.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.gerken.audioGuide.interfaces.SharedPreferenceStorage;

public class SharedPreferenceManager implements SharedPreferenceStorage {
	private final String KEY_ROUTE_ID = "currentRouteId";
	private final String KEY_LAST_SIGHT_ID = "lastSightId";
	
	private Context _context;
	
	public SharedPreferenceManager(Context ctx) {
		_context = ctx;
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
	public int getLastSightId() {
		return getSharedPreferences().getInt(KEY_LAST_SIGHT_ID, 0);
	}

	@Override
	public void setLastSightId(int id) {
		storeInt(KEY_LAST_SIGHT_ID, id);
	}
	
	private SharedPreferences getSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(_context);
	}
	
	private void storeInt(String key, int value) {
		Editor ed = getSharedPreferences().edit();
		ed.putInt(key, value);
		ed.commit();	
	}

}
