package com.gerken.audioGuide.presenters;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.interfaces.*;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.objectModel.*;

public class SightPresenter {
	private final float SIGHT_ACTIVATION_RADIUS = 20.0f;
	private final double EARTH_RADIUS = 6371.0;
	private final String AUDIO_FOLDER = "audio";
	private final int AUDIO_PLAYER_POLLING_INTERVAL_MS = 250;
	private final int PLAYER_PANEL_HIDING_DELAY_MS = 2000;
	
	private City _city;
	private SightView _sightView;
	private AssetStreamProvider _assetStreamProvider;
	private AudioPlayer _audioPlayer;
	private SharedPreferenceStorage _prefStorage;
	private Logger _logger;
	
	private Sight _currentSight = null;
	private SightLook _currentSightLook = null;
	
	private Timer _audioUpdateTimer;
	private Timer _playerPanelHidingTimer;
	
	private OnEventListener _mediaPlayerCompletionListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			_sightView.displayPlayerStopped();	
		}
	};
	
	private OnEventListener _routeChangeListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			if(_currentSight != null) {
				if(_prefStorage.isRouteChosen()) {
					String routeName = null;
					for(Route r: _city.getRoutes()) {
						if(r.getId()==_prefStorage.getCurrentRouteId()) {
							routeName = r.getName();
							break;
						}
					}
					_sightView.acceptNewRouteSelected(_currentSight.getName(), routeName);
				}
			}
			
		}
	};
	
	public SightPresenter(City city, SightView sightView, 
			AssetStreamProvider assetStreamProvider, AudioPlayer audioPlayer,
			SharedPreferenceStorage prefStorage, Logger logger) {
		_city = city;
		_sightView = sightView;
		_assetStreamProvider = assetStreamProvider;
		_audioPlayer = audioPlayer;
		_prefStorage = prefStorage;
		_logger = logger;
		
		_audioUpdateTimer = new Timer();
		_playerPanelHidingTimer = new Timer();
		
		_audioPlayer.addAudioAssetCompletionListener(_mediaPlayerCompletionListener);
		_prefStorage.setOnCurrentRouteChangedListener(_routeChangeListener);
	}
	
	public void handleLocationChange(double latitude, double longitude) {
		SightLook newSightLook = findClosestSightLookInRange(latitude, longitude);
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
	
	public void handlePlayButtonClick() {
		if(_audioPlayer.isPlaying()) {
			_audioPlayer.pause();
			_sightView.displayPlayerStopped();		
			resetAudioUpdateTimer();
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
			startAudioUpdateTimer();			
		}
	}	
	
	public void handleStopButtonClick() {
		if(_audioPlayer.isPlaying()) {
			_audioPlayer.stop();
			_sightView.displayPlayerStopped();	
			if(_prefStorage.isRouteChosen()) {
				NextRoutePoint nrp = getNextRoutePoint();
				float heading = (float)(Math.PI*nrp.getHeading()/180.0);
				_sightView.displayNextSightDirection(heading);
			}
			
			resetAudioUpdateTimer();
		}
	}
	
	public void handleWindowClick() {
		if(_currentSightLook != null) {
			_sightView.showPlayerPanel();
			startPlayerPanelHidingTimer();
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
			int duration = _audioPlayer.getDuration();
			_sightView.setAudioProgressMaximum(duration);
			_sightView.setAudioDuration(MsToString(duration));
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
	
	private NextRoutePoint getNextRoutePoint() {
		if(_currentSightLook == null)
			return null;
		
		int routeId = _prefStorage.getCurrentRouteId();
		for(NextRoutePoint nrp: _currentSightLook.getNextRoutePoints()) {
			if(nrp.getRouteId() == routeId)
				return nrp;
		}
		
		return null;
	}
	
	private String MsToString(int ms) {
		int s = ms / 1000;
		int m = s / 60;
		s -= m*60;
		return String.format("%d:%02d", m, s);
	}
	
	private void startAudioUpdateTimer() {
		_audioUpdateTimer.scheduleAtFixedRate(
			new TimerTask() {				
				@Override
				public void run() {
					int pos = _audioPlayer.getCurrentPosition();
					_sightView.setAudioProgressPosition(pos);
					_sightView.setAudioPosition(MsToString(pos));
					
				}
			},
			0, AUDIO_PLAYER_POLLING_INTERVAL_MS
		);
	}
	
	private void resetAudioUpdateTimer() {
		_audioUpdateTimer.cancel();
		_audioUpdateTimer = new Timer();
	}
	
	private void startPlayerPanelHidingTimer() {
		_audioUpdateTimer.schedule(
			new TimerTask() {				
				@Override
				public void run() {
					_sightView.hidePlayerPanel();
				}
			},
			PLAYER_PANEL_HIDING_DELAY_MS
		);
	}
	
	private void resetPlayerPanelHidingTimer() {
		_playerPanelHidingTimer.cancel();
		_playerPanelHidingTimer = new Timer();
	}
	
}
