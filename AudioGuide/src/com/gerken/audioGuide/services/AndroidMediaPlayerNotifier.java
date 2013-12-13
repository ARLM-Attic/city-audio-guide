package com.gerken.audioGuide.services;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.interfaces.AudioNotifier;

public class AndroidMediaPlayerNotifier implements AudioNotifier {
	private Context _context;
	private MediaPlayer _player;
	
	private OnCompletionListener _completionListener = new OnCompletionListener() {		
		@Override
		public void onCompletion(MediaPlayer mp) {
			mp.release();
			_player = null;
		}
	};
	
	public AndroidMediaPlayerNotifier(Context context) {
		_context = context;
	}
	
	@Override
	public void signalSightInRange() {
		_player = MediaPlayer.create(_context, R.raw.inrange);
		_player.setOnCompletionListener(_completionListener);
		_player.start();
	}

}
