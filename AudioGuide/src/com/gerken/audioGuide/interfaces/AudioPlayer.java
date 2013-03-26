package com.gerken.audioGuide.interfaces;

import java.io.IOException;

public interface AudioPlayer {
	void signalSightInRange();
	
	void prepareAudioAsset(String asetFileName) throws IOException;
	
	void play() throws IOException;
	void pause();
	void stop();
	boolean isPlaying();
	
	int getCurrentPosition();
	int getDuration();
	
	void addAudioAssetCompletionListener(OnEventListener listener);
}
