package com.gerken.audioGuide.presenters;

import java.util.TimerTask;
import java.util.concurrent.Executor;

import com.gerken.audioGuide.graphics.BitmapDownscalingResult;
import com.gerken.audioGuide.interfaces.*;
import com.gerken.audioGuide.interfaces.views.AudioPlayerView;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.objectModel.*;
import com.gerken.audioGuide.util.ParametrizedRunnable;

public class SightPresenter {	
	private final long PLAYER_PANEL_HIDING_DELAY_MS = 5000L;
	
	private City _city;
	private SightView _sightView;
	private AudioPlayerView _audioPlayerView;
	private DownscalingBitmapLoader _bitmapLoader;
	private AudioPlayer _audioPlayer;
	private AudioNotifier _audioNotifier;
	private ApplicationSettingsStorage _prefStorage;
	private NewSightLookGotInRangeRaiser _newSightLookGotInRangeRaiser;
	private Scheduler _playerPanelHidingScheduler;
	private MediaAssetManager _mediaAssetManager;
	private LocationTracker _locationTracker;
	private Logger _logger;
	private Executor _longTaskExecutor;
	private LockProvider _lockProvider;
	
	private Sight _currentSight = null;
	private SightLook _currentSightLook = null;	

	private boolean _isPlayerPanelVisible = false;
	private boolean _isNextRoutePointInfoShown = false;
	private boolean _hasPlayerBeenPausedOnViewStop = false;
	private boolean _isViewBackgroundInitialized = false;
	
	private int _currentSightLookImageHeight;
	private int _currentSightLookImageVerticalPadding;	
	
	private OnEventListener _mediaPlayerCompletionListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			if(_currentSightLook != null)
				showNextRoutePointDirection();	
		}
	};
	
	private OnEventListener _sightViewInitializedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			if(_prefStorage.showHelpAtStartup()) {
				_sightView.showHelp();
				_prefStorage.setShowHelpAtStartup(false);
			}
			handleRouteSelection();
		}
	};
	
	private OnEventListener _sightViewStartedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			handleSightViewStart();	
		}
	};
	
	private OnEventListener _sightViewLayoutCompleteListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			handleSightViewLayoutComplete();	
		}
	};
	
	private OnEventListener _playPressedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			reschedulePlayerPanelHiding();	
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
			unschedulePlayerPanelHiding();
		}
	};
	
	private OnEventListener _rewindReleasedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			schedulePlayerPanelHiding();	
		}
	};
	
	
	private OnEventListener _sightViewTouchedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			handleSightViewTouch();	
		}
	};
	
	private OnEventListener _sightViewStoppedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			handleSightViewStop();	
		}
	};
	
	private OnEventListener _sightViewDestroyedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			handleSightViewDestroy();	
		}
	};
	
	private OnEventListener _sightViewRestartedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			handleSightViewRestart();	
		}
	};
	
	private OnEventListener _routeChangeListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			handleRouteSelection();			
		}
	};
	
	private OnSightLookGotInRangeListener _sightLookGotInRangeListener = new OnSightLookGotInRangeListener() {
		
		@Override
		public void onSightLookGotInRange(SightLook closestSightLookInRange) {
			handleSightLookIsInRange(closestSightLookInRange);			
		}
	};
	
	public SightPresenter(City city, SightView sightView, AudioPlayerView audioPlayerView) {
		_city = city;
		_sightView = sightView;
		_sightView.addViewInitializedListener(_sightViewInitializedListener);
		_sightView.addViewStartedListener(_sightViewStartedListener);
		_sightView.addViewLayoutCompleteListener(_sightViewLayoutCompleteListener);
		_sightView.addViewTouchedListener(_sightViewTouchedListener);
		_sightView.addViewStoppedListener(_sightViewStoppedListener);
		_sightView.addViewDestroyedListener(_sightViewDestroyedListener);
		_sightView.addViewRestartedListener(_sightViewRestartedListener);
		
		_audioPlayerView = audioPlayerView;
		_audioPlayerView.addPlayPressedListener(_playPressedListener);
		_audioPlayerView.addStopPressedListener(_stopPressedListener);
		_audioPlayerView.addRewindPressedListener(_rewindPressedListener);
		_audioPlayerView.addRewindReleasedListener(_rewindReleasedListener);
	}
	
	public void setAudioPlayer(AudioPlayer audioPlayer) {
		_audioPlayer = audioPlayer;
		_audioPlayer.addAudioAssetCompletionListener(_mediaPlayerCompletionListener);
	}
		
	public void setAudioNotifier(AudioNotifier audioNotifier) {
		_audioNotifier = audioNotifier;
	}
	
	public void setBitmapLoader(DownscalingBitmapLoader bitmapLoader) {
		_bitmapLoader = bitmapLoader;
	}
	
	public void setApplicationSettingsStorage(ApplicationSettingsStorage storage) {
		_prefStorage = storage;
		_prefStorage.setOnCurrentRouteChangedListener(_routeChangeListener);
	}

	public void setNewSightLookGotInRangeRaiser(
			NewSightLookGotInRangeRaiser newSightLookGotInRangeRaiser) {
		_newSightLookGotInRangeRaiser = newSightLookGotInRangeRaiser;
		_newSightLookGotInRangeRaiser.addSightLookGotInRangeListener(_sightLookGotInRangeListener);
	}
	
	public void setPlayerPanelHidingScheduler(Scheduler scheduler) {
		_playerPanelHidingScheduler = scheduler;
	}
	
	public void setMediaAssetManager(MediaAssetManager mediaAssetManager) {
		_mediaAssetManager = mediaAssetManager;
	}
	
	public void setLocationTracker(LocationTracker tracker) {
		_locationTracker = tracker;
		//_locationTracker.addLocationChangedListener(_locationChangedListener);
	}
		
	public void setLogger(Logger logger) {
		_logger = logger;
	}
	
	public void setLongTaskExecutor(Executor longTaskExecutor) {
		_longTaskExecutor = longTaskExecutor;
	}
	
	public void setLockProvider(LockProvider lockProvider) {
		_lockProvider = lockProvider;
	}
	
	private void handleSightLookIsInRange(SightLook sightLook) {
		if(sightLook != null) {
			if(!sightLook.equals(_currentSightLook)) {
				_currentSightLook = sightLook;
				if(!sightLook.getSight().equals(_currentSight)) {
					_currentSight = sightLook.getSight();
					playAudioNotification();
					notifyViewAboutNewSight(sightLook);					
				}					
				else
					notifyViewAboutNewSightLook(sightLook);	
			}
		}
		else if(_currentSightLook != null) {
			notifyViewAboutNoSightViewInRange();	
			if(_audioPlayer != null && _audioPlayer.isPlaying())
				_audioPlayer.stop();
			_currentSight = null;
			_currentSightLook = null;
		}
		_isNextRoutePointInfoShown = false;
	}
	
	private void playAudioNotification() {
		if(_audioNotifier != null) {
			if(_lockProvider != null)
				_lockProvider.acquireAudioPreparationLock();
			_audioNotifier.signalSightInRange();
			if(_lockProvider != null)
				_lockProvider.releaseAudioPreparationLock();
		}
	}

	private void handleStopButtonClick() {
		unschedulePlayerPanelHiding();
		_sightView.hidePlayerPanel();
		_isPlayerPanelVisible = false;

		showNextRoutePointDirection();
	}
	
	private void showNextRoutePointDirection() {
		if(_prefStorage.isRouteChosen()) {
			NextRoutePoint nrp = getNextRoutePoint();
			if(nrp != null) {
				float heading = (float)(Math.PI*nrp.getHeading()/180.0);
				_sightView.displayNextSightDirection(heading, getAdjustedHorizon(nrp.getHorizon()));
				_sightView.setInfoPanelCaptionText(nrp.getName());
				_isNextRoutePointInfoShown = true;
			}
		}
	}
	
	private float getAdjustedHorizon(byte originalHorizonPerc) {
		int originalHeight = _currentSightLookImageHeight + 2*_currentSightLookImageVerticalPadding;
		int originalHorizon = Math.round(0.01f*originalHorizonPerc*originalHeight);
		return (float)(originalHorizon - _currentSightLookImageVerticalPadding)/(float)_currentSightLookImageHeight;
	}
	
	private void handleSightViewLayoutComplete() {
		if(!_isViewBackgroundInitialized){
			notifyViewAboutNoSightViewInRange();
			_isViewBackgroundInitialized = true;
		}
	}
	
	private void handleSightViewStart() {
		if(_locationTracker != null)
			_locationTracker.startTracking();
	}
	
	private void handleSightViewTouch() {
		ensureAudioPepared();
		if(isSightInRange() && !_isPlayerPanelVisible) {
			_sightView.showPlayerPanel();
			_isPlayerPanelVisible = true;
			schedulePlayerPanelHiding();
			
			if(_isNextRoutePointInfoShown) {
				_sightView.hideNextSightDirection();
				_sightView.setInfoPanelCaptionText(_currentSight.getName());
				_isNextRoutePointInfoShown = false;
			}
		}
	}
	
	private void ensureAudioPepared() {
		if(_lockProvider != null) {
			_lockProvider.acquireAudioPreparationLock();
			_lockProvider.releaseAudioPreparationLock();
		}
	}
	
	private void handleSightViewStop() {
		if(_locationTracker != null)
			_locationTracker.stopTracking();
		
		if(_audioPlayer.isPlaying()) {
			_audioPlayer.pause();
			_hasPlayerBeenPausedOnViewStop = true;
		}
	}
	
	private void handleSightViewDestroy() {
		if(_mediaAssetManager != null)
			_mediaAssetManager.cleanupAudioAsset();
	}
	
	private void handleSightViewRestart() {
		if(_hasPlayerBeenPausedOnViewStop) {
			try {
				_audioPlayer.play();
				_hasPlayerBeenPausedOnViewStop = false;
			}
			catch(Exception ex) {
				logError("Unable to play audio track for the current sight ", ex);
			}			
		}
	}
	
	private void handleRouteSelection() {
		if(_prefStorage.isRouteChosen())
			_sightView.enableRouteMapMenuItem(_prefStorage.getCurrentRouteId());
		else
			_sightView.disableRouteMapMenuItem();
	}

	private boolean isSightInRange() {
		return (_currentSightLook != null);
	}
	
	private void notifyViewAboutNewSight(SightLook newSightLook) {
		notifyViewAboutNewSightLook(newSightLook);
	}
	
	private void notifyViewAboutNewSightLook(SightLook newSightLook) {
		_sightView.setInfoPanelCaptionText(newSightLook.getSight().getName());
		_sightView.hideNextSightDirection();
		
		Runnable bmpRunnable = 
			new ParametrizedRunnable<SightLook>(newSightLook) {
				public void run(SightLook newSightLook) {
					BitmapDownscalingResult sightLookImage = getSightLookImage(newSightLook);
					if(sightLookImage != null)		
						_sightView.setBackgroundImage(sightLookImage);					
				}
			};
		if(_longTaskExecutor != null)
			_longTaskExecutor.execute(bmpRunnable);
		else
			bmpRunnable.run();
	}
	
	private void notifyViewAboutNoSightViewInRange() {
		try{
			BitmapDownscalingResult bmp = _bitmapLoader.load(
				_city.getConfiguration().getOutOfRangeImageName(), 
				_sightView.getWidth(), _sightView.getHeight());
			if(bmp != null)		
				_sightView.setBackgroundImage(bmp);		
		}
		catch(Exception ex) {
			logError("Unable to retrieve the no sight in range image", ex);
		}
		
		_sightView.resetInfoPanelCaptionText();
		_sightView.hideNextSightDirection();
	}
	
	private BitmapDownscalingResult getSightLookImage(SightLook newSightLook) {
		BitmapDownscalingResult sightLookImage = null;
		try{
			BitmapDownscalingResult bmp = _bitmapLoader.load(newSightLook.getImageName(), 
					_sightView.getWidth(), _sightView.getHeight());
			_currentSightLookImageHeight = bmp.getFinalHeight();
			_currentSightLookImageVerticalPadding = bmp.getFinalVerticalPadding();
			sightLookImage = bmp;
		}
		catch(Exception ex) {
			logError("Unable to get the sight image " + newSightLook.getImageName(), ex);
		}
		return sightLookImage;
	}

	private NextRoutePoint getNextRoutePoint() {
		if(_currentSightLook == null)
			return null;
		
		if(_currentSightLook.getNextRoutePoints() == null)
			return null;
		
		int routeId = _prefStorage.getCurrentRouteId();		
		for(NextRoutePoint nrp: _currentSightLook.getNextRoutePoints()) {
			if(nrp.getRouteId() == routeId)
				return nrp;
		}
		
		return null;
	}	
	
	private void schedulePlayerPanelHiding() {
		if(_playerPanelHidingScheduler != null)
			_playerPanelHidingScheduler.schedule(
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
	
	private void unschedulePlayerPanelHiding() {
		if(_playerPanelHidingScheduler != null)
			_playerPanelHidingScheduler.cancel();
	}
	
	private void reschedulePlayerPanelHiding() {
		unschedulePlayerPanelHiding();
		schedulePlayerPanelHiding();
	}
	
	private void logError(String message, Throwable ex) {
		if(_logger != null)
			_logger.logError(message, ex);
	}
}
