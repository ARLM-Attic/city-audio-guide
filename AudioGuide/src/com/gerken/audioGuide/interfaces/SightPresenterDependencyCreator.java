package com.gerken.audioGuide.interfaces;

public interface SightPresenterDependencyCreator {
	AssetStreamProvider createAssetStreamProvider();
	ApplicationSettingsStorage createApplicationSettingsStorage();
	DownscalableBitmapCreator createDownscalableBitmapCreator();
	AudioPlayerRewinder createAudioPlayerRewinder();
	Logger createLogger();
}
