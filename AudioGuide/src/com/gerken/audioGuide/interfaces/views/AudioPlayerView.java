package com.gerken.audioGuide.interfaces.views;

public interface AudioPlayerView {
	void setAudioProgressMaximum(int ms);
	void setAudioProgressPosition(int ms);
	void setAudioDuration(String formattedDuration);
	void setAudioPosition(String formattedPosition);
}
