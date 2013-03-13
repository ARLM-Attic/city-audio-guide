package com.gerken.audioGuide.util;

import android.content.Intent;

public class IntentExtraManager {
	private final int DEFAULT_INT = Integer.MIN_VALUE;
	private final String KEY_ROUTE_ID = "routeId";	
	
	private Intent _intent;
	
	public IntentExtraManager(Intent intent) {
		_intent = intent;
	}

	public boolean hasRouteId() {
		return _intent.hasExtra(KEY_ROUTE_ID);
	}
	public int getRouteId() {
		return _intent.getIntExtra(KEY_ROUTE_ID, DEFAULT_INT);
	}
	public void setRouteId(int routeId) {
		_intent.putExtra(KEY_ROUTE_ID, routeId);
	}
}
