package com.gerken.audioGuideTests.presenters.sightPresenter;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.interfaces.AudioPlayer;
import com.gerken.audioGuide.interfaces.listeners.OnEventListener;
import com.gerken.audioGuide.interfaces.views.AudioPlayerView;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.presenters.SightPresenter;

public class HandleViewStoppedEvent {
	@Test
	public void Given_AudioPlayerIsPlaying__Then_AudioPlayerIsPaused() throws Exception {
		final boolean PLAYER_IS_PLAYING = true;
		
		SightView sightView = mock(SightView.class);
		AudioPlayer player = mock(AudioPlayer.class);
		when(player.isPlaying()).thenReturn(PLAYER_IS_PLAYING);

		SutSetupResult sutSetupResult = setupSut(sightView, player);
		
		// --- Act
		sutSetupResult.sightViewStoppedListener.onEvent();
		
		// --- Assert
		verify(player).pause();
	}
	
	@Test
	public void Given_AudioPlayerIsNotPlaying__Then_AudioPlayerIsNotPaused() throws Exception {
		final boolean PLAYER_IS_NOT_PLAYING = false;
		
		SightView sightView = mock(SightView.class);
		AudioPlayer player = mock(AudioPlayer.class);
		when(player.isPlaying()).thenReturn(PLAYER_IS_NOT_PLAYING);

		SutSetupResult sutSetupResult = setupSut(sightView, player);
		
		// --- Act
		sutSetupResult.sightViewStoppedListener.onEvent();
		
		// --- Assert
		verify(player, never()).pause();
	}
	
	private SutSetupResult setupSut(SightView sightView, AudioPlayer player) {
		SutSetupResult result = new SutSetupResult();
		
		ArgumentCaptor<OnEventListener> sightViewStoppedListenerCaptor = 
				ArgumentCaptor.forClass(OnEventListener.class);
		doNothing().when(sightView).addViewStoppedListener(sightViewStoppedListenerCaptor.capture());		
		
		SightPresenter sut = new SightPresenter(new City(), sightView, mock(AudioPlayerView.class));
		sut.setAudioPlayer(player);
		
		result.sut = sut;
		result.sightViewStoppedListener = sightViewStoppedListenerCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		public SightPresenter sut;
		public OnEventListener sightViewStoppedListener;
	}
}
