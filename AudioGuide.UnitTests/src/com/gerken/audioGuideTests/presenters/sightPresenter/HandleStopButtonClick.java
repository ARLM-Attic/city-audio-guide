package com.gerken.audioGuideTests.presenters.sightPresenter;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import com.gerken.audioGuide.interfaces.*;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.objectModel.*;
import com.gerken.audioGuide.presenters.SightPresenter;;

public class HandleStopButtonClick {
	
	@Test
	public void Given_PlayerHasBeenPlaying__Then_PlayerGetsStopped() throws Exception {
		final String WHATEVER_STRING = "whatever";		
		
		City city = new City(1, "Default", WHATEVER_STRING);
		SightView view = mock(SightView.class);
		AudioPlayer player = mock(AudioPlayer.class);
		when(player.isPlaying()).thenReturn(true);
		
		SightPresenter sut = CreateSut(city, view, player);
		
		// --- Act
		sut.handleStopButtonClick();
		
		// --- Assert
		verify(player).stop();
	}

	private SightPresenter CreateSut(City city, SightView view, AudioPlayer player) {
		AssetStreamProvider assetStreamProvider = mock(AssetStreamProvider.class);
		ApplicationSettingsStorage prefStorage = mock(ApplicationSettingsStorage.class);
		Logger logger = mock(Logger.class);
		
		return new SightPresenter(city, view, assetStreamProvider,
				player, prefStorage, logger);
	}
}
