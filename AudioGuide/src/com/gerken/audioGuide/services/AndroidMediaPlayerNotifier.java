package com.gerken.audioGuide.services;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.interfaces.AudioNotifier;

public class AndroidMediaPlayerNotifier implements AudioNotifier {
	private Context _context;
	private MediaPlayer _player;

	private OnErrorListener _errorListener = new OnErrorListener() {
		
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			mp.release();
			initPlayer();
			return true;
		}
	};
	
	public AndroidMediaPlayerNotifier(Context context) {
		_context = context;
		initPlayer();
	}
	
	@Override
	public void signalSightInRange() {
		_player.start();
	}
	
	private void initPlayer() {
		_player = MediaPlayer.create(_context, R.raw.inrange);
		_player.setOnErrorListener(_errorListener);
	}
}
