package com.gerken.audioGuide.services;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.containers.FileInfo;
import com.gerken.audioGuide.interfaces.AudioPlayer;
import com.gerken.audioGuide.interfaces.MediaAssetManager;
import com.gerken.audioGuide.interfaces.OnEventListener;

public class AndroidMediaPlayerFacade implements AudioPlayer {	

	private Context _context;
	private MediaPlayer _mediaPlayer;
	private MediaAssetManager _mediaAssetManager;
	
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
	
	public AndroidMediaPlayerFacade(Context context, MediaAssetManager mediaAssetManager) {
		_context = context;
		_mediaAssetManager = mediaAssetManager;
		_mediaPlayer = new MediaPlayer();
		
		_completionListeners = new ArrayList<OnEventListener>();
		_mediaPlayer.setOnCompletionListener(_audioAssetCompletionListener);
	}

	@Override
	public void prepareAudioAsset(String assetFileName) throws Exception {
		FileInfo fi = _mediaAssetManager.prepareAudioAsset(assetFileName);
        _mediaPlayer.reset();
       _mediaPlayer.setDataSource(fi.getFileDescriptor(), 0, fi.getLength() );       
       _mediaPlayer.prepare();	
       fi.close();
       _needsPreparation = false;
	}

	@Override
	public void addAudioAssetCompletionListener(OnEventListener listener) {
		_completionListeners.add(listener);		
	}

	@Override
	public void play() throws IOException {
		if(_needsPreparation) {
			_mediaPlayer.prepare();
			_needsPreparation = false;
		}
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
		_mediaPlayer.seekTo(0);
		_mediaPlayer.stop();
		_mediaAssetManager.cleanupAudioAsset();
		_needsPreparation = true;
		_isPlaying = false;
	}

	@Override
	public boolean isPlaying() {
		return _isPlaying;
	}

	@Override
	public int getCurrentPosition() {
		return _mediaPlayer.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		return _mediaPlayer.getDuration();
	}

	@Override
	public void seekTo(int ms) {
		_mediaPlayer.seekTo(ms);		
	}
	

}
