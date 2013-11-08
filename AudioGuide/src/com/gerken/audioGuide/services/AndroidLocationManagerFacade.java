package com.gerken.audioGuide.services;

import java.util.ArrayList;

import com.gerken.audioGuide.interfaces.LocationTracker;
import com.gerken.audioGuide.interfaces.Logger;
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
	private Logger _logger;
	
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
			if(location == null)
				return;
			logDebug(String.format("Location changed: lat=%.5f long=%.5f; acc=%.1f m",  
					location.getLatitude(), location.getLongitude(), location.getAccuracy()));
			for(OnLocationChangedListener l : _locationChangedListeners)
				l.onLocationChanged(location.getLatitude(), location.getLongitude());			
		}
	};
	

	public AndroidLocationManagerFacade(Context ctx) {
		_manager = (LocationManager)
				ctx.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public void setLogger(Logger logger) {
		_logger = logger;
	}
	
	public void startTracking() {
		logDebug("Starting location tracking");
		Location location = _manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(location != null)
			logDebug(String.format("Last known location of %tT: lat=%.5f long=%.5f; acc=%.1f m",  
					new java.util.Date(location.getTime()),
					location.getLatitude(), location.getLongitude(), location.getAccuracy()));
		_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
			UPDATE_FREQ_MIN_MS, UPDATE_DISTANCE_MIN_M,
			_locationListener);
	}
	
	public void stopTracking() {
		logDebug("Stopping location tracking");
		_manager.removeUpdates(_locationListener);
	}

	@Override
	public void addLocationChangedListener(OnLocationChangedListener listener) {
		_locationChangedListeners.add(listener);		
	}
	
	private void logDebug(String message) {
		if(_logger != null)
			_logger.logDebug(message);
	}
}
