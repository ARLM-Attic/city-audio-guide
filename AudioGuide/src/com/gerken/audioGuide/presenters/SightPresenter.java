package com.gerken.audioGuide.presenters;

import java.io.InputStream;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.gerken.audioGuide.interfaces.*;
import com.gerken.audioGuide.objectModel.*;

public class SightPresenter implements LocationListener {
	private final float SIGHT_ACTIVATION_RADIUS = 20.0f;
	private final double EARTH_RADIUS = 6371.0;
	private final String AUDIO_FOLDER = "audio";
	
	private City _city;
	private SightView _sightView;
	private AssetStreamProvider _assetStreamProvider;
	private Logger _logger;
	
	private Sight _currentSight = null;
	private SightLook _currentSightLook = null;
	
	public SightPresenter(City city, SightView sightView, AssetStreamProvider assetStreamProvider, Logger logger) {
		_city = city;
		_sightView = sightView;
		_assetStreamProvider = assetStreamProvider;
		_logger = logger;
	}


	@Override
	public void onLocationChanged(Location location) {
		if(location != null) {
			SightLook newSightLook = findClosestSightLookInRange(location.getLatitude(), location.getLongitude());
			if(newSightLook != null && !newSightLook.equals(_currentSightLook)) {
				if(!newSightLook.getSight().equals(_currentSight)) {
					notifyViewAboutNewSight(newSightLook);
					_currentSight = newSightLook.getSight();
				}					
				else
					notifyViewAboutNewSightLook(newSightLook);
				
				_currentSightLook = newSightLook;
			}
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	private void notifyViewAboutNewSight(SightLook newSightLook) {
		Sight newSight = newSightLook.getSight();
		InputStream imgStream = null;
		try{
			imgStream = _assetStreamProvider.getImageAssetStream(newSightLook.getImageName());
		}
		catch(Exception ex) {
			_logger.logError("Unable to get the sight image " + newSightLook.getImageName(), ex);
		}
		
		_sightView.acceptNewSightGotInRange(newSight.getName(), 
				imgStream,
				String.format("%s/%s", AUDIO_FOLDER, newSight.getAudioName()));
	}
	
	private void notifyViewAboutNewSightLook(SightLook newSightLook) {
		InputStream imgStream = null;
		try{
			imgStream = _assetStreamProvider.getImageAssetStream(newSightLook.getImageName());
		}
		catch(Exception ex) {
			_logger.logError("Unable to get the sight image " + newSightLook.getImageName(), ex);
		}
		
		_sightView.acceptNewSightLookGotInRange(imgStream);
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
