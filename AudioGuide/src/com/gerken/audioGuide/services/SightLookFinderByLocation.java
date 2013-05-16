package com.gerken.audioGuide.services;

import java.util.ArrayList;

import com.gerken.audioGuide.interfaces.LocationTracker;
import com.gerken.audioGuide.interfaces.NewSightLookGotInRangeRaiser;
import com.gerken.audioGuide.interfaces.OnLocationChangedListener;
import com.gerken.audioGuide.interfaces.OnSightLookGotInRangeListener;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.objectModel.Sight;
import com.gerken.audioGuide.objectModel.SightLook;

public class SightLookFinderByLocation implements NewSightLookGotInRangeRaiser {
	private final float SIGHT_ACTIVATION_RADIUS = 20.0f;
	private final double EARTH_RADIUS = 6371.0;
	
	private City _city;
	private LocationTracker _locationTracker;
	
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
	}

	@Override
	public void addSightLookGotInRangeListener(
			OnSightLookGotInRangeListener listener) {
		_sightLookGotInRangeListeners.add(listener);		
	}
	
	private void handleLocationChanged(double latitude, double longitude) {
		SightLook sightLook = findClosestSightLookInRange(latitude, longitude);
		for(OnSightLookGotInRangeListener l : _sightLookGotInRangeListeners)
			l.onSightLookGotInRange(sightLook);
	}

	private SightLook findClosestSightLookInRange(double latitude, double longitude){
		SightLook closestSightLook = null;
		double closestSightLookDistance = Double.POSITIVE_INFINITY;
		for(Sight s : _city.getSights()) {
			for(SightLook sl : s.getSightLooks()) {
				double distance = calcDistance(latitude, longitude, sl.getLatitude(), sl.getLongitude());
				if(distance < SIGHT_ACTIVATION_RADIUS && distance < closestSightLookDistance) {
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
        return angle * EARTH_RADIUS;
	}
	
	private double deg2rad(double deg) {
		return deg * (Math.PI/180.0);
	}
}
