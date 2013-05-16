package com.gerken.audioGuide.presenters;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.interfaces.AudioPlayer;
import com.gerken.audioGuide.interfaces.Logger;
import com.gerken.audioGuide.interfaces.NewSightLookGotInRangeRaiser;
import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.OnSightLookGotInRangeListener;
import com.gerken.audioGuide.interfaces.views.AudioPlayerView;
import com.gerken.audioGuide.objectModel.Sight;
import com.gerken.audioGuide.objectModel.SightLook;

public class AudioPlayerPresenter {
	private final int AUDIO_PLAYER_POLLING_INTERVAL_MS = 250;
	private final float REWIND_STEP_RATIO = 0.02f;
	private final long REWIND_REPEAT_INTERVAL_MS = 500L;
	private final String AUDIO_FOLDER = "audio";
	
	private AudioPlayer _audioPlayer;
	private AudioPlayerView _audioPlayerView;
	private NewSightLookGotInRangeRaiser _newSightLookGotInRangeRaiser;
	private Logger _logger;
	
	private Timer _audioUpdateTimer;
	private Timer _rewindTimer;
	
	private boolean _isTimerStarted = false;
	private boolean _resumePlayerAfterRewinding = false;
	
	private Sight _currentSight = null;
	
	private OnEventListener _playPressedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			handlePlayButtonClick();	
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
			handleRewindButtonPress();	
		}
	};
	
	private OnEventListener _rewindReleasedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			handleRewindButtonRelease();	
		}
	};
	
	private OnSightLookGotInRangeListener _sightLookGotInRangeListener = new OnSightLookGotInRangeListener() {
		
		@Override
		public void onSightLookGotInRange(SightLook closestSightLookInRange) {
			handleSightLookIsInRange(closestSightLookInRange);			
		}
	};
	
	private OnEventListener _mediaPlayerCompletionListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			_audioPlayerView.displayPlayerStopped();	
		}
	};
	
	public AudioPlayerPresenter(AudioPlayerView audioPlayerView, AudioPlayer audioPlayer, Logger logger) {
		_audioPlayer = audioPlayer;
		_audioPlayerView = audioPlayerView;
		_logger = logger;
		
		_audioPlayer.addAudioAssetCompletionListener(_mediaPlayerCompletionListener);
		
		_audioPlayerView.addPlayPressedListener(_playPressedListener);
		_audioPlayerView.addStopPressedListener(_stopPressedListener);
		_audioPlayerView.addRewindPressedListener(_rewindPressedListener);
		_audioPlayerView.addRewindReleasedListener(_rewindReleasedListener);
	}
	
	public void setNewSightLookGotInRangeRaiser(
			NewSightLookGotInRangeRaiser newSightLookGotInRangeRaiser) {
		_newSightLookGotInRangeRaiser = newSightLookGotInRangeRaiser;
		_newSightLookGotInRangeRaiser.addSightLookGotInRangeListener(_sightLookGotInRangeListener);
	}
	
	private void handleSightLookIsInRange(SightLook sightLook) {
		if(sightLook != null) {
			Sight newSight = sightLook.getSight();
			if(!newSight.equals(_currentSight)) {
				prepareNewAudio(newSight.getAudioName());
				_currentSight = newSight;
			}
		}
	}
	
	private void prepareNewAudio(String audioFileName) {
		try {
			_audioPlayer.prepareAudioAsset(
					String.format("%s/%s", AUDIO_FOLDER, audioFileName));
			initPlayerDisplayedDuration();
		}
		catch(Exception ex){ 
        	String logMsg=String.format("Error when setting %s as the new MediaPlayer datasource", audioFileName);
        	_logger.logError(logMsg, ex);
        	_audioPlayerView.displayError(R.string.error_invalid_sight_audio);
    	}
	}
	
	private void handlePlayButtonClick() {
		if(_audioPlayer.isPlaying()) {
			_audioPlayer.pause();
			_audioPlayerView.displayPlayerStopped();		
			stopAudioUpdateTimer();
		}
		else {
			try {
				_audioPlayer.play();
			}
			catch(Exception ex) {
				//String sightName = (_currentSight != null) ?
				//	_currentSight.getName() : "[unknown]";
				_logger.logError("Unable to play audio track for the current sight ", ex);// + sightName, ex);
			}
			_audioPlayerView.displayPlayerPlaying();
			startAudioUpdateTimer();
		}
	}
	
	private void handleStopButtonClick() {
		//resetPlayerPanelHidingTimer();
		if(_audioPlayer.isPlaying())		
			_audioPlayer.stop();
		
		stopAudioUpdateTimer();
		resetPlayerDisplayedPosition();
		_audioPlayerView.displayPlayerStopped();	
		/*
		_sightView.hidePlayerPanel();
		_isPlayerPanelVisible = false;
		
		if(_prefStorage.isRouteChosen()) {
			NextRoutePoint nrp = getNextRoutePoint();
			float heading = (float)(Math.PI*nrp.getHeading()/180.0);
			_sightView.displayNextSightDirection(heading, getAdjustedHorizon(nrp.getHorizon()));
			_sightView.setInfoPanelCaptionText(nrp.getName());
			_isNextRoutePointInfoShown = true;
		}
		*/
	}
	
	private void handleRewindButtonPress() {
		_logger.logDebug("handleRewindButtonPress");
		startAudioUpdateTimer();
		//resetPlayerPanelHidingTimer();
		startRewinding();
	}
	
	private void handleRewindButtonRelease() {
		_logger.logDebug("handleRewindButtonRelease");
		try {
			stopRewinding();			
		}
		catch(Exception ex) {
			_logger.logError("Unable to resume playing after rewinding", ex);
		}
		//startPlayerPanelHidingTimer();
	}

	private void startAudioUpdateTimer() {
		if(!_isTimerStarted) {
			_audioUpdateTimer = new Timer();
			_audioUpdateTimer.scheduleAtFixedRate(
				new TimerTask() {				
					@Override
					public void run() {
						int pos = _audioPlayer.getCurrentPosition();
						_audioPlayerView.setAudioProgressPosition(pos);
						_audioPlayerView.setAudioPosition(MsToString(pos));
						
					}
				},
				0, AUDIO_PLAYER_POLLING_INTERVAL_MS
			);
			_isTimerStarted = true;
		}
	}
	
	private void stopAudioUpdateTimer() {
		if(_audioUpdateTimer != null)
			_audioUpdateTimer.cancel();
		_isTimerStarted = false;
	}
	
	private void initPlayerDisplayedDuration() {
		int duration = _audioPlayer.getDuration();
		_audioPlayerView.setAudioProgressMaximum(duration);
		_audioPlayerView.setAudioDuration(MsToString(duration));
	}
	
	private void resetPlayerDisplayedPosition() {
		_audioPlayerView.setAudioProgressPosition(0);
		_audioPlayerView.setAudioPosition(MsToString(0));
	}
	
	private String MsToString(int ms) {
		int s = ms / 1000;
		int m = s / 60;
		s -= m*60;
		return String.format("%d:%02d", m, s);
	}
	
	private void startRewinding() {
		_resumePlayerAfterRewinding = _audioPlayer.isPlaying();
		_audioPlayer.pause();
		int step = (int)(REWIND_STEP_RATIO * (float)_audioPlayer.getDuration());
		
		_rewindTimer = new Timer();
		_rewindTimer.scheduleAtFixedRate(
				new RewindTimerTask(step), 0, REWIND_REPEAT_INTERVAL_MS);
		
	}
	
	private void stopRewinding() throws IOException {
		_rewindTimer.cancel();
		if(_resumePlayerAfterRewinding)
			_audioPlayer.play();
	}	
	
	private void doRewindStep(int step) {
		int newPosition = Math.max(0, _audioPlayer.getCurrentPosition()-step);
		_audioPlayer.seekTo(newPosition);
	}
	
	private class RewindTimerTask extends TimerTask {
		private int _rewindStep;
		
		public RewindTimerTask(int rewindStep) {
			_rewindStep = rewindStep;
		}
		
		@Override
		public void run() {
			doRewindStep(_rewindStep);						
		}
	}
}
