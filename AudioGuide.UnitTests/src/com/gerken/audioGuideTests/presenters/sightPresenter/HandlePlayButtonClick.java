package com.gerken.audioGuideTests.presenters.sightPresenter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.Test;

import com.gerken.audioGuide.interfaces.ApplicationSettingsStorage;
import com.gerken.audioGuide.interfaces.AssetStreamProvider;
import com.gerken.audioGuide.interfaces.AudioPlayer;
import com.gerken.audioGuide.interfaces.DownscalableBitmapCreator;
import com.gerken.audioGuide.interfaces.Logger;
import com.gerken.audioGuide.interfaces.SightPresenterDependencyCreator;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.objectModel.Sight;
import com.gerken.audioGuide.objectModel.SightLook;
import com.gerken.audioGuide.presenters.SightPresenter;

public class HandlePlayButtonClick {
	
	private Random _random = new Random(System.currentTimeMillis());
	
	@Test
	public void Given_PlayerIsNotPlaying__Then_PlayerRequestedToPlay_ShownAsPlaying() throws Exception {

		final boolean PLAYER_IS_NOT_PLAYING = false;		
		
		SightView view = mock(SightView.class);
		AudioPlayer player = mock(AudioPlayer.class);
		when(player.isPlaying()).thenReturn(PLAYER_IS_NOT_PLAYING);
		
		SightPresenter sut = CreateSut(CreateSingleSightLookModel(), view, player);
		
		// --- Act
		sut.handlePlayButtonClick();
		
		// --- Assert
		verify(player).play();
		verify(view).displayPlayerPlaying();
		verify(view, never()).displayPlayerStopped();
	}
	
	@Test
	public void Given_PlayerIsPlaying__Then_PlayerRequestedToPause_ShownAsPaused() throws Exception {
		final boolean PLAYER_IS_PLAYING = true;		
		
		SightView view = mock(SightView.class);
		AudioPlayer player = mock(AudioPlayer.class);
		when(player.isPlaying()).thenReturn(PLAYER_IS_PLAYING);
		
		SightPresenter sut = CreateSut(CreateSingleSightLookModel(), view, player);
		
		// --- Act
		sut.handlePlayButtonClick();
		
		// --- Assert
		verify(player).pause();
		verify(view).displayPlayerStopped();
	}
	
	private SightPresenter CreateSut(City city,	SightView view, AudioPlayer player) {		
		SightPresenterDependencyCreator factory = mock(SightPresenterDependencyCreator.class);
		when(factory.createApplicationSettingsStorage()).thenReturn(mock(ApplicationSettingsStorage.class));
		when(factory.createAssetStreamProvider()).thenReturn(mock(AssetStreamProvider.class));
		
		return new SightPresenter(city, view, player, factory);
	}
	
	private City CreateSingleSightLookModel() {
		final String WHATEVER_STRING = "whatever";	
		final double WHATEVER_DOUBLE = 42;
		
		SightLook expectedSightLook = new SightLook(
				WHATEVER_DOUBLE, WHATEVER_DOUBLE, WHATEVER_STRING);
		Sight expectedSight = new Sight(_random.nextInt(), WHATEVER_STRING, WHATEVER_STRING);
		expectedSight.addLook(expectedSightLook);
		City city = new City(_random.nextInt(), WHATEVER_STRING, WHATEVER_STRING);
		city.getSights().add(expectedSight);
		
		return city;
	}

}
