package com.gerken.audioGuide.presenters;

import java.io.InputStream;

import android.location.Location;
import android.location.LocationListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.interfaces.*;
import com.gerken.audioGuide.objectModel.*;

public class SightPresenter implements LocationListener {
	private final float SIGHT_ACTIVATION_RADIUS = 20.0f;
	private final double EARTH_RADIUS = 6371.0;
	private final String AUDIO_FOLDER = "audio";
	
	private City _city;
	private SightView _sightView;
	private AssetStreamProvider _assetStreamProvider;
	private AudioPlayer _audioPlayer;
	private Logger _logger;
	
	private Sight _currentSight = null;
	private SightLook _currentSightLook = null;
	
	private OnCompletionListener _mediaPlayerCompletionListener = new OnCompletionListener() {		
		@Override
		public void onCompletion(MediaPlayer mp) {
			_sightView.displayPlayerStopped();	
		}
	};
	
	public SightPresenter(City city, SightView sightView, 
			AssetStreamProvider assetStreamProvider, AudioPlayer audioPlayer,
			Logger logger) {
		_city = city;
		_sightView = sightView;
		_assetStreamProvider = assetStreamProvider;
		_audioPlayer = audioPlayer;
		_logger = logger;
		
		_audioPlayer.setAudioAssetCompletionListener(_mediaPlayerCompletionListener);
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
	
	public void handlePlayButtonClick() {
		if(_audioPlayer.isPlaying()) {
			_audioPlayer.pause();
			_sightView.displayPlayerStopped();			
		}
		else {
			try {
			_audioPlayer.play();
			}
			catch(Exception ex) {
				String sightName = (_currentSight != null) ?
					_currentSight.getName() : "[unknown]";
				_logger.logError("Unable to play audio track for the sight " + sightName, ex);
			}
			_sightView.displayPlayerPlaying();			
		}
	}	
	
	public void handleStopButtonClick() {
		if(_audioPlayer.isPlaying()) {
			_audioPlayer.stop();
			_sightView.displayPlayerStopped();	
		}
	}
	
	public void handleActivityPause() {
		
	}
	
	public void handleActivityResume() {
		
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
		
		_sightView.acceptNewSightGotInRange(newSight.getName(), imgStream);
		prepareNewAudio(newSight.getAudioName());
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
	
	private void prepareNewAudio(String audioFileName) {
		try {
			_audioPlayer.prepareAudioAsset(
					String.format("%s/%s", AUDIO_FOLDER, audioFileName));
		}
		catch(Exception ex){ 
        	String logMsg=String.format("Error when setting %s as the new MediaPlayer datasource", audioFileName);
        	_logger.logError(logMsg, ex);
        	_sightView.displayError(R.string.error_invalid_sight_audio);
    	}
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
