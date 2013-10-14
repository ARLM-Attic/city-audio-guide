package com.gerken.audioGuide.services;

import java.io.FileInputStream;
import java.io.InputStream;

import com.gerken.audioGuide.containers.FileInfo;
import com.gerken.audioGuide.interfaces.MediaAssetManager;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

public class PlainMediaAssetManager implements MediaAssetManager {
	private Context _context;
	
	private static final String AUDIO_FOLDER = "audio";
	
	public PlainMediaAssetManager(Context context){
		_context = context;
	}
	
	public InputStream getImageAssetStream(String imageName) throws Exception {
		AssetManager am = _context.getAssets();
		InputStream imageStream = am.open("images/" + imageName);
		return imageStream;
	}	
	
	@Override
	public FileInfo prepareAudioAsset(String audioFileName) throws Exception {
		AssetManager am = _context.getAssets();		
		String audioAssetPath = String.format("%s/%s", AUDIO_FOLDER, audioFileName);
		AssetFileDescriptor afd = am.openFd(audioAssetPath);
		FileInputStream audioFileInputStream = afd.createInputStream();	
		
		FileInfo result = new FileInfo(audioFileInputStream, afd.getStartOffset(), afd.getLength());
		return result;
	}

	@Override
	public void cleanupAudioAsset() {
	}
}
