package com.gerken.audioGuide.presenters;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.graphics.DownscalableBitmap;
import com.gerken.audioGuide.interfaces.*;
import com.gerken.audioGuide.interfaces.views.AudioPlayerView;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.objectModel.*;

public class SightPresenter {
	
	
	private final long PLAYER_PANEL_HIDING_DELAY_MS = 5000L;
	
	private City _city;
	private SightView _sightView;
	private AudioPlayerView _audioPlayerView;
	private AssetStreamProvider _assetStreamProvider;
	private AudioPlayer _audioPlayer;
	private ApplicationSettingsStorage _prefStorage;
	private DownscalableBitmapCreator _downscalableBitmapCreator;
	private NewSightLookGotInRangeRaiser _newSightLookGotInRangeRaiser;
	private Logger _logger;
	
	private Sight _currentSight = null;
	private SightLook _currentSightLook = null;	
	
	private Timer _playerPanelHidingTimer;
	private boolean _isPlayerPanelVisible = false;
	private boolean _isNextRoutePointInfoShown = false;
	
	private int _currentSightLookImageHeight;
	private int _currentSightLookImageVerticalPadding;
	
	
	private OnEventListener _mediaPlayerCompletionListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			//_sightView.displayPlayerStopped();	
		}
	};
	
	private OnEventListener _playPressedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			restartPlayerPanelHidingTimer();	
		}
	};
	
	private OnEventListener _stopPressedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			handleStopButtonClick();	
		}
	};
	
	private OnEventListener _rewindPressedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			resetPlayerPanelHidingTimer();
		}
	};
	
	private OnEventListener _rewindReleasedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			startPlayerPanelHidingTimer();	
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
	
	private OnSightLookGotInRangeListener _sightLookGotInRangeListener = new OnSightLookGotInRangeListener() {
		
		@Override
		public void onSightLookGotInRange(SightLook closestSightLookInRange) {
			handleSightLookIsInRange(closestSightLookInRange);			
		}
	};
	
	public SightPresenter(SightView sightView, AudioPlayerView audioPlayerView, AudioPlayer audioPlayer) {
		_sightView = sightView;
		_audioPlayerView = audioPlayerView;
		_audioPlayer = audioPlayer;
		
		_playerPanelHidingTimer = new Timer();
		
		_audioPlayer.addAudioAssetCompletionListener(_mediaPlayerCompletionListener);		
		
		_audioPlayerView.addPlayPressedListener(_playPressedListener);
		_audioPlayerView.addStopPressedListener(_stopPressedListener);
		_audioPlayerView.addRewindPressedListener(_rewindPressedListener);
		_audioPlayerView.addRewindReleasedListener(_rewindReleasedListener);
	}
	
	public void setAssetStreamProvider(AssetStreamProvider assetStreamProvider) {
		_assetStreamProvider = assetStreamProvider;
	}
	
	public void setApplicationSettingsStorage(ApplicationSettingsStorage storage) {
		_prefStorage = storage;
		_prefStorage.setOnCurrentRouteChangedListener(_routeChangeListener);
	}
	
	public void setDownscalableBitmapCreator(DownscalableBitmapCreator downscalableBitmapCreator) {
		_downscalableBitmapCreator = downscalableBitmapCreator;
	}
	
	public void setNewSightLookGotInRangeRaiser(
			NewSightLookGotInRangeRaiser newSightLookGotInRangeRaiser) {
		_newSightLookGotInRangeRaiser = newSightLookGotInRangeRaiser;
		_newSightLookGotInRangeRaiser.addSightLookGotInRangeListener(_sightLookGotInRangeListener);
	}
	
	public void setLogger(Logger logger) {
		_logger = logger;
	}
	
	public void handleViewInit() {
		if(_prefStorage.showHelpAtStartup()) {
			_sightView.showHelp();
			_prefStorage.setShowHelpAtStartup(false);			
		}		
	}
	
	private void handleSightLookIsInRange(SightLook sightLook) {
		if(sightLook != null) {
			if(!sightLook.equals(_currentSightLook)) {
				if(!sightLook.getSight().equals(_currentSight)) {
					notifyViewAboutNewSight(sightLook);
					_audioPlayer.signalSightInRange();
					_currentSight = sightLook.getSight();
				}					
				else
					notifyViewAboutNewSightLook(sightLook);
				
				_currentSightLook = sightLook;
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

	public void handleStopButtonClick() {
		resetPlayerPanelHidingTimer();

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
