package com.gerken.audioGuide.interfaces;

import java.io.IOException;

import android.media.MediaPlayer.OnCompletionListener;

public interface AudioPlayer {
	void signalSightInRange();
	
	void playAudioAsset(String asetFileName) throws IOException;
	
	void setAudioAssetCompletionListener(OnCompletionListener listener);
}
