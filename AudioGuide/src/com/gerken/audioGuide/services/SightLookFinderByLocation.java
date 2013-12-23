package com.gerken.audioGuide.services;

import java.util.ArrayList;

import com.gerken.audioGuide.interfaces.LocationTracker;
import com.gerken.audioGuide.interfaces.Logger;
import com.gerken.audioGuide.interfaces.NewSightLookGotInRangeRaiser;
import com.gerken.audioGuide.interfaces.OnLocationChangedListener;
import com.gerken.audioGuide.interfaces.OnSightLookGotInRangeListener;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.objectModel.Sight;
import com.gerken.audioGuide.objectModel.SightLook;
import com.gerken.audioGuide.util.BoundedNonBlockingQueue;

public class SightLookFinderByLocation implements NewSightLookGotInRangeRaiser {
	private static final float DEFAULT_SIGHT_ACTIVATION_RADIUS_M = 60.0f;
	private static final double EARTH_RADIUS_M = 6371000.0;
	private static final int DEFAULT_FOUND_SIGHT_LOOK_QUEUE_CAPACITY = 3;	
	
	private City _city;
	private LocationTracker _locationTracker;
	private Logger _logger;
	
	private BoundedNonBlockingQueue<SightLook> _foundSightLooksQueue;
	private SightLook[] _foundSightLooksArray;
	private final SightLook _nullSightLook = new SightLook(Double.NaN, Double.NaN, "null");
	
	private float _sightActivationRadius = DEFAULT_SIGHT_ACTIVATION_RADIUS_M;
	private int _foundSightLookQueueCapacity = DEFAULT_FOUND_SIGHT_LOOK_QUEUE_CAPACITY;
	private int _sightLookMissingCountThreshold = DEFAULT_FOUND_SIGHT_LOOK_QUEUE_CAPACITY;
	private boolean _wasAnyListenerNotificationSent = false;
	private boolean _lastListenerNotificationWasNull = false;
	
	private ArrayList<OnSightLookGotInRangeListener> _sightLookGotInRangeListeners = 
			new ArrayList<OnSightLookGotInRangeListener>();
	
	private OnLocationChangedListener _locationChangedListener = new OnLocationChangedListener() {		
		@Override
		public void onLocationChanged(double latitude, double longitude) {
			handleLocationChanged(latitude, longitude);			
		}
	};
	
	public SightLookFinderByLocation(City city, LocationTracker locationTracker) {
		_city = city;
		_locationTracker = locationTracker;
		_locationTracker.addLocationChangedListener(_locationChangedListener);
		
		_foundSightLooksQueue = new BoundedNonBlockingQueue<SightLook>(DEFAULT_FOUND_SIGHT_LOOK_QUEUE_CAPACITY);
		_foundSightLooksArray = new SightLook[DEFAULT_FOUND_SIGHT_LOOK_QUEUE_CAPACITY];
	}
	
	public void setLogger(Logger logger) {
		_logger = logger;
	}
	
	public void setSightActivationRadius(float radiusInMeters) {
		_sightActivationRadius = radiusInMeters;
	}
	
	public void setSightLookMissingCountThreshold(int threshold) {
		_sightLookMissingCountThreshold = threshold;
		_foundSightLookQueueCapacity = threshold;
		_foundSightLooksQueue = new BoundedNonBlockingQueue<SightLook>(_foundSightLookQueueCapacity);
		_foundSightLooksArray = new SightLook[_foundSightLookQueueCapacity];
	}

	@Override
	public void addSightLookGotInRangeListener(
			OnSightLookGotInRangeListener listener) {
		_sightLookGotInRangeListeners.add(listener);		
	}
	
	private void handleLocationChanged(double latitude, double longitude) {
		SightLook sightLook = findClosestSightLookInRange(latitude, longitude);
		logSightLook(sightLook);
		
		if(sightLook == null) {
			handleLocationHasNoSightLook();
		}
		else {
			_foundSightLooksQueue.offer(sightLook);
			notifyListeners(sightLook);
			_lastListenerNotificationWasNull = false;
			_wasAnyListenerNotificationSent = true;
		}		
	}
	
	private void handleLocationHasNoSightLook() {
		_foundSightLooksQueue.offer(_nullSightLook);
		
		if(!_wasAnyListenerNotificationSent)
			return;

		if(!_lastListenerNotificationWasNull && _foundSightLooksQueue.size() >= _sightLookMissingCountThreshold) {
			boolean queueTailNullCountReachedThreshold = true;
			_foundSightLooksArray = _foundSightLooksQueue.toArray(_foundSightLooksArray);
			int stIdx = _foundSightLooksQueue.size() - 1;
			int enIdx = _foundSightLooksQueue.size() - _sightLookMissingCountThreshold;
			for(int i=stIdx; i>=enIdx; i--) {
				if(!isSightLookNull(_foundSightLooksArray[i])) {
					queueTailNullCountReachedThreshold = false;
					break;
				}
			}
			
			if(queueTailNullCountReachedThreshold) {
				notifyListeners(null);
				logDebug("Sent NULL to listeners");
				_lastListenerNotificationWasNull = true;
				_wasAnyListenerNotificationSent = true;
			}
		}
	}
	
	private void notifyListeners(SightLook sightLook) {
		for(OnSightLookGotInRangeListener l : _sightLookGotInRangeListeners)
			l.onSightLookGotInRange(sightLook);
	}
	
	private boolean isSightLookNull(SightLook sightLook) {
		return (Double.isNaN(sightLook.getLatitude()) && Double.isNaN(sightLook.getLongitude()));
	}

	private SightLook findClosestSightLookInRange(double latitude, double longitude){
		SightLook closestSightLook = null;
		double closestSightLookDistance = Double.POSITIVE_INFINITY;
		for(Sight s : _city.getSights()) {
			for(SightLook sl : s.getSightLooks()) {
				double distance = calcDistance(latitude, longitude, sl.getLatitude(), sl.getLongitude());
				if(distance < _sightActivationRadius && distance < closestSightLookDistance) {
					closestSightLook = sl;
					closestSightLookDistance = distance;
				}
			}			
		}
		
		return closestSightLook;
	}
	
	
	private double calcDistance(double lat1, double long1, double lat2, double long2) {
		double dlon = deg2rad(long2 - long1);
        double dlat = deg2rad(lat2 - lat1);

        double a = (Math.sin(dlat / 2) * Math.sin(dlat / 2)) + 
        		Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * (Math.sin(dlon / 2) * Math.sin(dlon / 2));
        double angle = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        return Math.abs(angle * EARTH_RADIUS_M);
	}
	
	private double deg2rad(double deg) {
		return deg * (Math.PI/180.0);
	}
	
	private void logSightLook(SightLook sightLook) {
		if(sightLook != null)
			logDebug(String.format("Found sight look \"%s\" at %f,%f", 
				sightLook.getSight().getName(), sightLook.getLatitude(), sightLook.getLongitude()));
		else
			logDebug("No sight look found");
	}
	
	private void logDebug(String message) {
		if(_logger != null)
			_logger.logDebug(message);
	}
}
