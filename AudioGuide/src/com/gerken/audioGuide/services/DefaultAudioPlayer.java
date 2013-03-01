package com.gerken.audioGuide.services;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.gerken.audioGuide.interfaces.AudioPlayer;

public class DefaultAudioPlayer implements AudioPlayer {

	private Context _context;
	private MediaPlayer _mediaPlayer;
	
	public DefaultAudioPlayer(Context context) {
		_context = context;
	}
	
	@Override
	public void signalSightInRange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playAudioAsset(String asetFileName) throws IOException {
		AssetManager assetManager = _context.getAssets();
		AssetFileDescriptor descriptor =  assetManager.openFd(asetFileName);
        _mediaPlayer.reset();
       _mediaPlayer.setDataSource(descriptor.getFileDescriptor(), 
    		   descriptor.getStartOffset(), descriptor.getLength() );
       descriptor.close();
       _mediaPlayer.prepare();		
	}

	@Override
	public void setAudioAssetCompletionListener(OnCompletionListener listener) {
		// TODO Auto-generated method stub
		
	}

}
