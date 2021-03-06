package com.gerken.audioGuide.presenters;

import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.Executor;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.containers.FileInfo;
import com.gerken.audioGuide.interfaces.AudioPlayer;
import com.gerken.audioGuide.interfaces.LockProvider;
import com.gerken.audioGuide.interfaces.Logger;
import com.gerken.audioGuide.interfaces.MediaAssetManager;
import com.gerken.audioGuide.interfaces.NewSightLookGotInRangeRaiser;
import com.gerken.audioGuide.interfaces.listeners.OnEventListener;
import com.gerken.audioGuide.interfaces.listeners.OnSightLookGotInRangeListener;
import com.gerken.audioGuide.interfaces.Scheduler;
import com.gerken.audioGuide.interfaces.views.AudioPlayerView;
import com.gerken.audioGuide.objectModel.Sight;
import com.gerken.audioGuide.objectModel.SightLook;
import com.gerken.audioGuide.util.ParametrizedRunnable;

public class AudioPlayerPresenter {
	private final int AUDIO_PLAYER_POLLING_INTERVAL_MS = 250;
	private final float DEFAULT_REWIND_STEP_RATIO = 0.02f;
	private final long REWIND_REPEAT_INTERVAL_MS = 500L;
	
	
	private AudioPlayer _audioPlayer;
	private AudioPlayerView _audioPlayerView;
	private MediaAssetManager _mediaAssetManager;
	private NewSightLookGotInRangeRaiser _newSightLookGotInRangeRaiser;
	private Logger _logger;
	private Scheduler _audioUpdateScheduler;
	private Scheduler _rewindScheduler;
	private Executor _longTaskExecutor;
	private LockProvider _lockProvider;
	
	private boolean _resumePlayerAfterRewinding = false;
	
	private Sight _currentSight = null;
	
	private float _rewindStepRatio = DEFAULT_REWIND_STEP_RATIO;
	
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
	
	public AudioPlayerPresenter(AudioPlayerView audioPlayerView, AudioPlayer audioPlayer) {
		_audioPlayer = audioPlayer;
		_audioPlayerView = audioPlayerView;		
		
		_audioPlayer.addAudioAssetCompletionListener(_mediaPlayerCompletionListener);
		
		_audioPlayerView.addPlayPressedListener(_playPressedListener);
		_audioPlayerView.addStopPressedListener(_stopPressedListener);
		_audioPlayerView.addRewindPressedListener(_rewindPressedListener);
		_audioPlayerView.addRewindReleasedListener(_rewindReleasedListener);
	}
	
	public void setMediaAssetManager(MediaAssetManager mediaAssetManager) {
		_mediaAssetManager = mediaAssetManager;
	}
	
	public void setNewSightLookGotInRangeRaiser(
			NewSightLookGotInRangeRaiser newSightLookGotInRangeRaiser) {
		_newSightLookGotInRangeRaiser = newSightLookGotInRangeRaiser;
		_newSightLookGotInRangeRaiser.addSightLookGotInRangeListener(_sightLookGotInRangeListener);
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
	
	public void setAudioUpdateScheduler(Scheduler scheduler) {
		_audioUpdateScheduler = scheduler;
	}
	
	public void setAudioRewindScheduler(Scheduler scheduler) {
		_rewindScheduler = scheduler;
	}
	
	public void setRewindStepRatio(float value) {
		_rewindStepRatio = value;
	}
	
	private void handleSightLookIsInRange(SightLook sightLook) {
		if(sightLook != null) {
			Sight newSight = sightLook.getSight();
			if(!newSight.equals(_currentSight)) {
				if(_audioPlayer.isPlaying())
					_audioPlayer.stop();
				prepareNewAudio(newSight.getAudioName());
				_currentSight = newSight;
				
				_audioPlayerView.displayPlayerStopped();
				resetPlayerDisplayedPosition();
			}			
		}
	}
	
	private void prepareNewAudio(String audioFileName) {
		Runnable r =
			new ParametrizedRunnable<String>(audioFileName) {
				public void run(String audioFileName) {	
					lockAudioPlayer();
					try {
						FileInfo fi = _mediaAssetManager.prepareAudioAsset(audioFileName);
						_audioPlayer.prepareAudioAsset(fi);
						fi.close();

						initPlayerDisplayedDuration();
					}
					catch(Exception ex){ 
			        	String logMsg=String.format("Error when setting %s as the new MediaPlayer datasource", audioFileName);
			        	logError(logMsg, ex);
			        	_audioPlayerView.displayError(R.string.error_invalid_sight_audio);
			    	}
					finally {
						unlockAudioPlayer();
					}
				}
			};
		if(_longTaskExecutor != null)
			_longTaskExecutor.execute(r);
		else
			r.run();
	}
	
	private void lockAudioPlayer() {
		if(_lockProvider != null)
			_lockProvider.acquireAudioPreparationLock();
	}
	private void unlockAudioPlayer() {
		if(_lockProvider != null)
			_lockProvider.releaseAudioPreparationLock();
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
				String sightName = (_currentSight != null) ?
					_currentSight.getName() : "[unknown]";
				logError("Unable to play audio track for the sight " + sightName, ex);
			}
			_audioPlayerView.displayPlayerPlaying();
			startAudioUpdateTimer();
		}
	}
	
	private void handleStopButtonClick() {
		if(_audioPlayer.isPlaying())		
			_audioPlayer.stop();
		_mediaAssetManager.cleanupAudioAsset();
		stopAudioUpdateTimer();
		resetPlayerDisplayedPosition();
		_audioPlayerView.displayPlayerStopped();	
	}
	
	private void handleRewindButtonPress() {
		logDebug("handleRewindButtonPress");
		startAudioUpdateTimer();
		startRewinding();
	}
	
	private void handleRewindButtonRelease() {
		logDebug("handleRewindButtonRelease");
		try {
			stopRewinding();			
		}
		catch(Exception ex) {
			logError("Unable to resume playing after rewinding", ex);
		}
	}

	private void startAudioUpdateTimer() {
		_audioUpdateScheduler.scheduleAtFixedRate(
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
	}
	
	private void stopAudioUpdateTimer() {
		if(_audioUpdateScheduler != null)
			_audioUpdateScheduler.cancel();
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
		int step = (int)(_rewindStepRatio * (float)_audioPlayer.getDuration());
		
		if(_rewindScheduler != null) {
			_rewindScheduler.scheduleAtFixedRate(
					new RewindTimerTask(step), 0, REWIND_REPEAT_INTERVAL_MS);
		}		
	}
	
	private void stopRewinding() throws IOException {
		if(_rewindScheduler != null)
			_rewindScheduler.cancel();
		if(_resumePlayerAfterRewinding)
			_audioPlayer.play();
	}	
	
	private void doRewindStep(int step) {
		int newPosition = Math.max(0, _audioPlayer.getCurrentPosition()-step);
		_audioPlayer.seekTo(newPosition);
	}
	
	private void logDebug(String message) {
		if(_logger != null)
			_logger.logDebug(message);
	}
	
	private void logError(String message, Throwable ex) {
		if(_logger != null)
			_logger.logError(message, ex);
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
