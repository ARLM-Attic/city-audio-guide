package com.gerken.audioGuide.services;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.gerken.audioGuide.interfaces.AudioPlayer;
import com.gerken.audioGuide.interfaces.AudioPlayerRewinder;

public class AudioPlayerRewindingHelper implements AudioPlayerRewinder {
	private final float REWIND_STEP_RATIO = 0.02f;
	private final long REWIND_REPEAT_INTERVAL_MS = 500L;
	
	private AudioPlayer _player;
	private Timer _rewindTimer;
	
	private boolean _resumePlayerAfterRewinding = false;
	
	public AudioPlayerRewindingHelper(AudioPlayer player) {
		_player = player;
	}

	public void startRewinding() {
		_resumePlayerAfterRewinding = _player.isPlaying();
		_player.pause();
		int step = (int)(REWIND_STEP_RATIO * (float)_player.getDuration());
		
		_rewindTimer = new Timer();
		_rewindTimer.scheduleAtFixedRate(
				new RewindTimerTask(step), 0, REWIND_REPEAT_INTERVAL_MS);
		
	}
	
	public void stopRewinding() throws IOException {
		_rewindTimer.cancel();
		if(_resumePlayerAfterRewinding)
			_player.play();
	}	
	
	private void doRewindStep(int step) {
		int newPosition = Math.max(0, _player.getCurrentPosition()-step);
		_player.seekTo(newPosition);
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
