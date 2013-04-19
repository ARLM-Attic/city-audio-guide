package com.gerken.audioGuide.presenters;

import java.util.Timer;
import java.util.TimerTask;

import com.gerken.audioGuide.interfaces.AudioPlayer;
import com.gerken.audioGuide.interfaces.views.AudioPlayerView;

public class AudioPositionUpdater {
	private final int AUDIO_PLAYER_POLLING_INTERVAL_MS = 250;
	
	private AudioPlayer _audioPlayer;
	private AudioPlayerView _audioPlayerView;
	
	private Timer _audioUpdateTimer;
	private boolean _isTimerStarted = false;
	
	public AudioPositionUpdater(AudioPlayer audioPlayer, AudioPlayerView audioPlayerView) {
		_audioPlayer = audioPlayer;
		_audioPlayerView = audioPlayerView;
	}

	public void startAudioUpdateTimer() {
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
	
	public void stopAudioUpdateTimer() {
		if(_audioUpdateTimer != null)
			_audioUpdateTimer.cancel();
		_isTimerStarted = false;
	}
	
	public void initPlayerDisplayedDuration() {
		int duration = _audioPlayer.getDuration();
		_audioPlayerView.setAudioProgressMaximum(duration);
		_audioPlayerView.setAudioDuration(MsToString(duration));
	}
	
	public void resetPlayerDisplayedPosition() {
		_audioPlayerView.setAudioProgressPosition(0);
		_audioPlayerView.setAudioPosition(MsToString(0));
	}
	
	private String MsToString(int ms) {
		int s = ms / 1000;
		int m = s / 60;
		s -= m*60;
		return String.format("%d:%02d", m, s);
	}
}
