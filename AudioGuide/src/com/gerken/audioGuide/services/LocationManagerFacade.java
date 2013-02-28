package com.gerken.audioGuide.services;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

public class LocationManagerFacade {
	private final long UPDATE_FREQ_MIN_MS = 10000;
	private final float UPDATE_DISTANCE_MIN_M = 5.0f;
	
	private LocationManager _manager;
	private LocationListener _locationListener;

	public LocationManagerFacade(Context ctx, LocationListener locationListener) {
		_manager = (LocationManager)
				ctx.getSystemService(Context.LOCATION_SERVICE);
		_locationListener = locationListener;
	}
	
	public void startTracking() {
		_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
			UPDATE_FREQ_MIN_MS, UPDATE_DISTANCE_MIN_M,
			_locationListener);
	}
	
	public void stopTracking() {
		_manager.removeUpdates(_locationListener);
	}
}
