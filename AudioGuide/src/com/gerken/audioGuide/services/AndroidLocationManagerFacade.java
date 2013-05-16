package com.gerken.audioGuide.services;

import java.util.ArrayList;

import com.gerken.audioGuide.interfaces.LocationTracker;
import com.gerken.audioGuide.interfaces.OnLocationChangedListener;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class AndroidLocationManagerFacade implements LocationTracker {
	private final long UPDATE_FREQ_MIN_MS = 10000;
	private final float UPDATE_DISTANCE_MIN_M = 5.0f;
	
	private LocationManager _manager;	
	
	private ArrayList<OnLocationChangedListener> _locationChangedListeners = 
			new ArrayList<OnLocationChangedListener>();
	
	private LocationListener _locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
		
		@Override
		public void onProviderEnabled(String provider) {
		}
		
		@Override
		public void onProviderDisabled(String provider) {
		}
		
		@Override
		public void onLocationChanged(Location location) {
			for(OnLocationChangedListener l : _locationChangedListeners)
				l.onLocationChanged(location.getLatitude(), location.getLongitude());			
		}
	};
	

	public AndroidLocationManagerFacade(Context ctx) {
		_manager = (LocationManager)
				ctx.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public void startTracking() {
		_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
			UPDATE_FREQ_MIN_MS, UPDATE_DISTANCE_MIN_M,
			_locationListener);
	}
	
	public void stopTracking() {
		_manager.removeUpdates(_locationListener);
	}

	@Override
	public void addLocationChangedListener(OnLocationChangedListener listener) {
		_locationChangedListeners.add(listener);		
	}
}
