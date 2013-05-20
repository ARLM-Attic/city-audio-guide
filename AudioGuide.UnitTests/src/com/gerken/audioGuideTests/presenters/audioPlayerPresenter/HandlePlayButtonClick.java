package com.gerken.audioGuideTests.presenters.audioPlayerPresenter;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.interfaces.ApplicationSettingsStorage;
import com.gerken.audioGuide.interfaces.AssetStreamProvider;
import com.gerken.audioGuide.interfaces.AudioPlayer;
import com.gerken.audioGuide.interfaces.DownscalableBitmapCreator;
import com.gerken.audioGuide.interfaces.Logger;
import com.gerken.audioGuide.interfaces.NewSightLookGotInRangeRaiser;
import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.OnSightLookGotInRangeListener;
import com.gerken.audioGuide.interfaces.views.AudioPlayerView;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.objectModel.Sight;
import com.gerken.audioGuide.objectModel.SightLook;
import com.gerken.audioGuide.presenters.AudioPlayerPresenter;

public class HandlePlayButtonClick {
	
	private Random _random = new Random(System.currentTimeMillis());

	@Test
	public void Given_PlayerIsNotPlaying__Then_PlayerRequestedToPlay_ShownAsPlaying() throws Exception {

		final boolean PLAYER_IS_NOT_PLAYING = false;		
		
		AudioPlayerView playerView = mock(AudioPlayerView.class);
		AudioPlayer player = mock(AudioPlayer.class);
		when(player.isPlaying()).thenReturn(PLAYER_IS_NOT_PLAYING);
		
		SutSetupResult sutSetupResult = setupSut(playerView, player);
		
		// --- Act
		sutSetupResult.playButtonPressedListener.onEvent();
		
		// --- Assert
		verify(player).play();
		verify(playerView).displayPlayerPlaying();
		verify(playerView, never()).displayPlayerStopped();
	}

	@Test
	public void Given_PlayerIsPlaying__Then_PlayerRequestedToPause_ShownAsPaused() throws Exception {
		final boolean PLAYER_IS_PLAYING = true;		
		
		AudioPlayerView playerView = mock(AudioPlayerView.class);
		AudioPlayer player = mock(AudioPlayer.class);
		when(player.isPlaying()).thenReturn(PLAYER_IS_PLAYING);
		
		SutSetupResult sutSetupResult = setupSut(playerView, player);
		
		// --- Act
		sutSetupResult.playButtonPressedListener.onEvent();
		
		// --- Assert
		verify(player).pause();
		verify(playerView).displayPlayerStopped();
		verify(playerView, never()).displayPlayerPlaying();
	}

	
	private SutSetupResult setupSut(AudioPlayerView playerView, AudioPlayer audioPlayer) {
		SutSetupResult result = new SutSetupResult();

		ArgumentCaptor<OnEventListener> playButtonPressedListenerCaptor = 
				ArgumentCaptor.forClass(OnEventListener.class);
		doNothing().when(playerView).addPlayPressedListener(playButtonPressedListenerCaptor.capture());
		
		
		AudioPlayerPresenter sut = new AudioPlayerPresenter(playerView, audioPlayer, mock(Logger.class));
		
		result.sut = sut;
		result.playButtonPressedListener = playButtonPressedListenerCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		public AudioPlayerPresenter sut;
		public OnEventListener playButtonPressedListener;
	}
}
