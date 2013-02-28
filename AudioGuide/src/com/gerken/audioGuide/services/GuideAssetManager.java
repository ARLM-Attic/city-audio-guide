package com.gerken.audioGuide.services;

import java.io.InputStream;

import com.gerken.audioGuide.interfaces.AssetStreamProvider;

import android.content.Context;
import android.content.res.AssetManager;

public class GuideAssetManager implements AssetStreamProvider {
	private Context _context;
	
	public GuideAssetManager(Context context){
		_context = context;
	}
	
	public InputStream getImageAssetStream(String imageName) throws Exception {
		AssetManager am = _context.getAssets();
		InputStream imageStream = am.open("images/" + imageName);
		return imageStream;
	}
	
	public InputStream getAudioAssetStream(String audioName) throws Exception {
		AssetManager am = _context.getAssets();
		InputStream imageStream = am.open(audioName);
		return imageStream;
	}
}
