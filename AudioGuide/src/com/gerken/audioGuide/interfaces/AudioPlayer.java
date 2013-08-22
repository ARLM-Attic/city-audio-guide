package com.gerken.audioGuide.interfaces;

import java.io.IOException;

import com.gerken.audioGuide.containers.FileInfo;

public interface AudioPlayer {	
	void prepareAudioAsset(FileInfo assetFileInfo) throws Exception;
	
	void play() throws IOException;
	void pause();
	void stop();
	boolean isPlaying();
	
	int getCurrentPosition();
	int getDuration();
	void seekTo(int ms);
	
	void addAudioAssetCompletionListener(OnEventListener listener);
}
