package com.gerken.audioGuideTests.presenters.sightPresenter;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
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

public class HandleViewRestartedEvent {
	@Test
	public void Given_AudioPlayerWasPausedOnViewStop__Then_AudioPlayerResumesPlaying() throws Exception {
		final boolean PLAYER_IS_PLAYING = true;
		
		SightView sightView = mock(SightView.class);
		AudioPlayer player = mock(AudioPlayer.class);
		when(player.isPlaying()).thenReturn(PLAYER_IS_PLAYING);

		SutSetupResult sutSetupResult = setupSut(sightView, player);
		sutSetupResult.sightViewStoppedListener.onEvent();
		
		// --- Act
		sutSetupResult.sightViewRestartedListener.onEvent();
		
		// --- Assert
		verify(player).play();
	}
	
	private SutSetupResult setupSut(SightView sightView, AudioPlayer player) {
		SutSetupResult result = new SutSetupResult();
		
		ArgumentCaptor<OnEventListener> sightViewStoppedListenerCaptor = 
				ArgumentCaptor.forClass(OnEventListener.class);
		doNothing().when(sightView).addViewStoppedListener(sightViewStoppedListenerCaptor.capture());
		
		ArgumentCaptor<OnEventListener> sightViewRestartedListenerCaptor = 
				ArgumentCaptor.forClass(OnEventListener.class);
		doNothing().when(sightView).addViewRestartedListener(sightViewRestartedListenerCaptor.capture());
		
		SightPresenter sut = new SightPresenter(new City(), sightView, mock(AudioPlayerView.class));
		sut.setAudioPlayer(player);
		
		result.sut = sut;
		result.sightViewStoppedListener = sightViewStoppedListenerCaptor.getValue();
		result.sightViewRestartedListener = sightViewRestartedListenerCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		public SightPresenter sut;
		public OnEventListener sightViewStoppedListener;
		public OnEventListener sightViewRestartedListener;
	}
}
