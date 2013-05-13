package com.gerken.audioGuide.services;

import android.content.Context;

import com.gerken.audioGuide.interfaces.ApplicationSettingsStorage;
import com.gerken.audioGuide.interfaces.AssetStreamProvider;
import com.gerken.audioGuide.interfaces.AudioPlayer;
import com.gerken.audioGuide.interfaces.AudioPlayerRewinder;
import com.gerken.audioGuide.interfaces.DownscalableBitmapCreator;
import com.gerken.audioGuide.interfaces.Logger;
import com.gerken.audioGuide.interfaces.SightPresenterDependencyCreator;

public class SightPresenterDependencyFactory implements SightPresenterDependencyCreator {
	private Context _ctx;
	private AudioPlayer _audioPlayer;
	
	public SightPresenterDependencyFactory(Context ctx, AudioPlayer audioPlayer) {
		_ctx = ctx;
		_audioPlayer = audioPlayer;
	}

	@Override
	public AssetStreamProvider createAssetStreamProvider() {
		return new GuideAssetManager(_ctx);
	}

	@Override
	public ApplicationSettingsStorage createApplicationSettingsStorage() {
		return new SharedPreferenceManager(_ctx);
	}

	@Override
	public DownscalableBitmapCreator createDownscalableBitmapCreator() {
		return new DownscalableBitmapFactory();
	}

	@Override
	public AudioPlayerRewinder createAudioPlayerRewinder() {
		return new AudioPlayerRewindingHelper(_audioPlayer);
	}

	@Override
	public Logger createLogger() {
		return new DefaultLoggingAdapter("SightPresenter");
	}

}
