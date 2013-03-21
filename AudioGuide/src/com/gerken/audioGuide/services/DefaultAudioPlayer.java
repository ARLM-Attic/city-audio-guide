package com.gerken.audioGuide.services;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.gerken.audioGuide.interfaces.AudioPlayer;
import com.gerken.audioGuide.interfaces.OnEventListener;

public class DefaultAudioPlayer implements AudioPlayer {

	private Context _context;
	private MediaPlayer _mediaPlayer;
	
	private boolean _isPlaying = false;
	private boolean _needsPreparation = false;
	
	private ArrayList<OnEventListener> _completionListeners;
	
	private OnCompletionListener _audioAssetCompletionListener = new OnCompletionListener() {		
		@Override
		public void onCompletion(MediaPlayer mp) {
			for(OnEventListener l: _completionListeners)
				l.onEvent();			
		}
	};
	
	public DefaultAudioPlayer(Context context) {
		_context = context;
		_mediaPlayer = new MediaPlayer();
		
		_completionListeners = new ArrayList<OnEventListener>();
		_mediaPlayer.setOnCompletionListener(_audioAssetCompletionListener);
	}
	
	@Override
	public void signalSightInRange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepareAudioAsset(String asetFileName) throws IOException {
		AssetManager assetManager = _context.getAssets();
		AssetFileDescriptor descriptor =  assetManager.openFd(asetFileName);
        _mediaPlayer.reset();
       _mediaPlayer.setDataSource(descriptor.getFileDescriptor(), 
    		   descriptor.getStartOffset(), descriptor.getLength() );
       descriptor.close();
       _mediaPlayer.prepare();		
	}

	@Override
	public void addAudioAssetCompletionListener(OnEventListener listener) {
		_completionListeners.add(listener);		
	}

	@Override
	public void play() throws IOException {
		if(_needsPreparation) 
			_mediaPlayer.prepare();
		_mediaPlayer.start();
		_isPlaying = true;
	}

	@Override
	public void pause() {
		_mediaPlayer.pause();
		_isPlaying = false;
	}

	@Override
	public void stop() {
		_mediaPlayer.stop();
		_needsPreparation = true;
		_isPlaying = false;
	}

	@Override
	public boolean isPlaying() {
		return _isPlaying;
	}

}
