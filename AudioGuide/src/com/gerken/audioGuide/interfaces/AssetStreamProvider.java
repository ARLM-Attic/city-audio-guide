package com.gerken.audioGuide.interfaces;

import java.io.InputStream;

public interface AssetStreamProvider {
	InputStream getImageAssetStream(String imageName) throws Exception;
	//AssetFileDescriptor getAudioAssetFileDescriptor(String audioName) throws Exception;
}
