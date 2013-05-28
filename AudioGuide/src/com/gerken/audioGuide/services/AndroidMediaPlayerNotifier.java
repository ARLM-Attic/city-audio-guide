package com.gerken.audioGuide.services;

import android.content.Context;
import android.media.MediaPlayer;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.interfaces.AudioNotifier;

public class AndroidMediaPlayerNotifier implements AudioNotifier {
	private Context _context;
	
	public AndroidMediaPlayerNotifier(Context context) {
		_context = context;
	}
	
	@Override
	public void signalSightInRange() {
		MediaPlayer player = MediaPlayer.create(_context, R.raw.inrange);
		player.start();
		//player.release();		
	}

}
