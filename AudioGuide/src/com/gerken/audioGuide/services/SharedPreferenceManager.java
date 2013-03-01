package com.gerken.audioGuide.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.gerken.audioGuide.interfaces.SharedPreferenceStorage;

public class SharedPreferenceManager implements SharedPreferenceStorage {
	private final String KEY_ROUTE_ID = "currentRouteId";
	
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
		// TODO Auto-generated method stub
		return getSharedPreferences().getInt(KEY_ROUTE_ID, 0);
	}

	@Override
	public void setCurrentRouteId(int id) {
		Editor ed = getSharedPreferences().edit();
		ed.putInt(KEY_ROUTE_ID, id);
		ed.commit();		
	}
	
	private SharedPreferences getSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(_context);
	}
}
