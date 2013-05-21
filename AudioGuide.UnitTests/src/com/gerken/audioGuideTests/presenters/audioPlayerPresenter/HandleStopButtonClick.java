package com.gerken.audioGuideTests.presenters.audioPlayerPresenter;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.interfaces.AudioPlayer;
import com.gerken.audioGuide.interfaces.Logger;
import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.views.AudioPlayerView;
import com.gerken.audioGuide.presenters.AudioPlayerPresenter;


public class HandleStopButtonClick {
	
	@Test
	public void Given_PlayerHasBeenPlaying__Then_PlayerGetsStopped() throws Exception {
		
		AudioPlayerView playerView = mock(AudioPlayerView.class);
		AudioPlayer player = mock(AudioPlayer.class);
		when(player.isPlaying()).thenReturn(true);
		
		SutSetupResult sutSetupResult = setupSut(playerView, player);
		
		// --- Act
		sutSetupResult.stopButtonPressedListener.onEvent();
		
		// --- Assert
		verify(player).stop();
	}
	
	private SutSetupResult setupSut(AudioPlayerView playerView, AudioPlayer audioPlayer) {
		SutSetupResult result = new SutSetupResult();

		ArgumentCaptor<OnEventListener> stopButtonPressedListenerCaptor = 
				ArgumentCaptor.forClass(OnEventListener.class);
		doNothing().when(playerView).addStopPressedListener(stopButtonPressedListenerCaptor.capture());
		
		
		AudioPlayerPresenter sut = new AudioPlayerPresenter(playerView, audioPlayer);
		
		result.sut = sut;
		result.stopButtonPressedListener = stopButtonPressedListenerCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		public AudioPlayerPresenter sut;
		public OnEventListener stopButtonPressedListener;
	}

}
