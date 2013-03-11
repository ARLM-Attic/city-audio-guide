package com.gerken.audioGuide.interfaces;

import java.io.IOException;

import android.media.MediaPlayer.OnCompletionListener;

public interface AudioPlayer {
	void signalSightInRange();
	
	void prepareAudioAsset(String asetFileName) throws IOException;
	
	void play() throws IOException;
	void pause();
	void stop();
	boolean isPlaying();
	
	void setAudioAssetCompletionListener(OnCompletionListener listener);
}
