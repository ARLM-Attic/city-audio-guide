package com.gerken.audioGuide.interfaces.views;

import com.gerken.audioGuide.interfaces.OnEventListener;

public interface AudioPlayerView {
	void displayPlayerPlaying();
	void displayPlayerStopped();
	
	void setAudioProgressMaximum(int ms);
	void setAudioProgressPosition(int ms);
	void setAudioDuration(String formattedDuration);
	void setAudioPosition(String formattedPosition);
	
	void addPlayPressedListener(OnEventListener listener);
	void addStopPressedListener(OnEventListener listener);
	void addRewindPressedListener(OnEventListener listener);
	void addRewindReleasedListener(OnEventListener listener);
	
	void displayError(int messageResourceId);
}
