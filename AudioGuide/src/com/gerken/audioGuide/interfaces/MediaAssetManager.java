package com.gerken.audioGuide.interfaces;

import java.io.InputStream;

import com.gerken.audioGuide.containers.FileInfo;

public interface MediaAssetManager {
	InputStream getImageAssetStream(String imageName) throws Exception;
	FileInfo prepareAudioAsset(String audioName) throws Exception;
	void cleanupAudioAsset();
}
