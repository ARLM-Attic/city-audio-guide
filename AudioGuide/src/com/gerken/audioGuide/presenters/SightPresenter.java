package com.gerken.audioGuide.presenters;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.graphics.DownscalableBitmap;
import com.gerken.audioGuide.interfaces.*;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.objectModel.*;

public class SightPresenter {
	private final float SIGHT_ACTIVATION_RADIUS = 20.0f;
	private final double EARTH_RADIUS = 6371.0;
	private final String AUDIO_FOLDER = "audio";
	private final long PLAYER_PANEL_HIDING_DELAY_MS = 5000L;
	
	private City _city;
	private SightView _sightView;
	private AssetStreamProvider _assetStreamProvider;
	private AudioPlayer _audioPlayer;
	private ApplicationSettingsStorage _prefStorage;
	private DownscalableBitmapCreator _downscalableBitmapCreator;
	private AudioPlayerRewinder _audioRewinder;
	private Logger _logger;
	
	private Sight _currentSight = null;
	private SightLook _currentSightLook = null;
	
	
	private Timer _playerPanelHidingTimer;
	private boolean _isPlayerPanelVisible = false;
	private boolean _isNextRoutePointInfoShown = false;
	
	private int _currentSightLookImageHeight;
	private int _currentSightLookImageVerticalPadding;
	
	
	private AudioPositionUpdater _audioPositionUpdater;
	
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
	
	public SightPresenter(City city, 
			SightView sightView, AudioPlayer audioPlayer,
			SightPresenterDependencyCreator sightPresenterDependencyCreator) {
		_city = city;
		_sightView = sightView;
		_audioPlayer = audioPlayer;				
		
		_assetStreamProvider = sightPresenterDependencyCreator.createAssetStreamProvider();		
		_prefStorage = sightPresenterDependencyCreator.createApplicationSettingsStorage();
		_downscalableBitmapCreator = 
				sightPresenterDependencyCreator.createDownscalableBitmapCreator();
		_logger = sightPresenterDependencyCreator.createLogger();
		_audioRewinder = sightPresenterDependencyCreator.createAudioPlayerRewinder();
				
		_playerPanelHidingTimer = new Timer();
		
		_audioPlayer.addAudioAssetCompletionListener(_mediaPlayerCompletionListener);
		_prefStorage.setOnCurrentRouteChangedListener(_routeChangeListener);
		
		_audioPositionUpdater = new AudioPositionUpdater(_audioPlayer, _sightView);
	}
	
	public void handleViewInit() {
		if(_prefStorage.showHelpAtStartup()) {
			_sightView.showHelp();
			_prefStorage.setShowHelpAtStartup(false);			
		}		
	}
	
	public void handleLocationChange(double latitude, double longitude) {
		SightLook newSightLook = findClosestSightLookInRange(latitude, longitude);
		if(newSightLook != null) {
			if(!newSightLook.equals(_currentSightLook)) {
				if(!newSightLook.getSight().equals(_currentSight)) {
					notifyViewAboutNewSight(newSightLook);
					_audioPlayer.signalSightInRange();
					_currentSight = newSightLook.getSight();
				}					
				else
					notifyViewAboutNewSightLook(newSightLook);
				
				_currentSightLook = newSightLook;
			}
		}
		else if(_currentSightLook != null) {
			_sightView.acceptNoSightInRange();					
			_audioPlayer.stop();
			_currentSight = null;
			_currentSightLook = null;
		}
		_isNextRoutePointInfoShown = false;
	}	
	
	public void handlePlayButtonClick() {
		restartPlayerPanelHidingTimer();
		if(_audioPlayer.isPlaying()) {
			_audioPlayer.pause();
			_sightView.displayPlayerStopped();		
			_audioPositionUpdater.stopAudioUpdateTimer();
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
			_audioPositionUpdater.startAudioUpdateTimer();
		}
	}	
	
	public void handleStopButtonClick() {
		resetPlayerPanelHidingTimer();
		if(_audioPlayer.isPlaying()) {			
			_audioPlayer.stop();
		}
		
		_audioPositionUpdater.stopAudioUpdateTimer();
		_audioPositionUpdater.resetPlayerDisplayedPosition();
		_sightView.displayPlayerStopped();	
		_sightView.hidePlayerPanel();
		_isPlayerPanelVisible = false;
		if(_prefStorage.isRouteChosen()) {
			NextRoutePoint nrp = getNextRoutePoint();
			float heading = (float)(Math.PI*nrp.getHeading()/180.0);
			_sightView.displayNextSightDirection(heading, getAdjustedHorizon(nrp.getHorizon()));
			_sightView.setInfoPanelCaptionText(nrp.getName());
			_isNextRoutePointInfoShown = true;
		}
	}
	
	private float getAdjustedHorizon(byte originalHorizonPerc) {
		int originalHeight = _currentSightLookImageHeight + 2*_currentSightLookImageVerticalPadding;
		int originalHorizon = Math.round(0.01f*originalHorizonPerc*originalHeight);
		return (float)(originalHorizon - _currentSightLookImageVerticalPadding)/(float)_currentSightLookImageHeight;
	}
	
	public void handleRewindButtonPress() {
		_logger.logDebug("handleRewindButtonPress");
		_audioPositionUpdater.startAudioUpdateTimer();
		resetPlayerPanelHidingTimer();
		_audioRewinder.startRewinding();
	}
	
	public void handleRewindButtonRelease() {
		_logger.logDebug("handleRewindButtonRelease");
		try {
			_audioRewinder.stopRewinding();			
		}
		catch(Exception ex) {
			_logger.logError("Unable to resume playing after rewinding", ex);
		}
		startPlayerPanelHidingTimer();
	}
	
	public void handleWindowClick() {
		if(isSightInRange() && !_isPlayerPanelVisible) {
			_sightView.showPlayerPanel();
			_isPlayerPanelVisible = true;
			startPlayerPanelHidingTimer();
			
			if(_isNextRoutePointInfoShown) {
				_sightView.hideNextSightDirection();
				_sightView.setInfoPanelCaptionText(_currentSight.getName());
				_isNextRoutePointInfoShown = false;
			}
		}
	}
	
	public void handleActivityPause() {
		
	}
	
	public void handleActivityResume() {
		
	}
	
	private boolean isSightInRange() {
		return (_currentSightLook != null);
	}
	
	private void notifyViewAboutNewSight(SightLook newSightLook) {
		notifyViewAboutNewSightLook(newSightLook);
		
		Sight newSight = newSightLook.getSight();
		_sightView.setInfoPanelCaptionText(newSight.getName());		
		_sightView.displayPlayerStopped();
		prepareNewAudio(newSight.getAudioName());
	}
	
	private void notifyViewAboutNewSightLook(SightLook newSightLook) {
		InputStream imgStream = getSightLookImageStream(newSightLook);
		if(imgStream != null)		
			setViewBackgroundImage(imgStream);
		
		_sightView.hideNextSightDirection();
	}
	
	private InputStream getSightLookImageStream(SightLook newSightLook) {
		InputStream imgStream = null;
		try{
			imgStream = _assetStreamProvider.getImageAssetStream(newSightLook.getImageName());
		}
		catch(Exception ex) {
			_logger.logError("Unable to get the sight image " + newSightLook.getImageName(), ex);
		}
		return imgStream;
	}
	
	private void setViewBackgroundImage(InputStream imgStream) {
		try {
			DownscalableBitmap bmp = _downscalableBitmapCreator.CreateDownscalableBitmap(
					imgStream, _sightView.getWidth(), _sightView.getHeight());
			_currentSightLookImageHeight = bmp.getFinalHeight();
			_currentSightLookImageVerticalPadding = bmp.getFinalVerticalPadding();
			_sightView.setBackgroundImage(bmp);
		}
		catch(Exception ex){
			_logger.logError("Unable to set the background image", ex);
        }
	}
	
	private void prepareNewAudio(String audioFileName) {
		try {
			_audioPlayer.prepareAudioAsset(
					String.format("%s/%s", AUDIO_FOLDER, audioFileName));
			_audioPositionUpdater.initPlayerDisplayedDuration();
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

	
	private void startPlayerPanelHidingTimer() {
		_playerPanelHidingTimer.schedule(
			new TimerTask() {				
				@Override
				public void run() {
					_sightView.hidePlayerPanel();
					_isPlayerPanelVisible = false;
				}
			},
			PLAYER_PANEL_HIDING_DELAY_MS
		);
	}
	
	private void resetPlayerPanelHidingTimer() {
		_playerPanelHidingTimer.cancel();
		_playerPanelHidingTimer = new Timer();
	}
	
	private void restartPlayerPanelHidingTimer() {
		resetPlayerPanelHidingTimer();
		startPlayerPanelHidingTimer();
	}
	
}
