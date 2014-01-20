package com.gerken.audioGuideTests.presenters.audioPlayerPresenter;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.interfaces.AudioPlayer;
import com.gerken.audioGuide.interfaces.MediaAssetManager;
import com.gerken.audioGuide.interfaces.listeners.OnEventListener;
import com.gerken.audioGuide.interfaces.Scheduler;
import com.gerken.audioGuide.interfaces.views.AudioPlayerView;
import com.gerken.audioGuide.presenters.AudioPlayerPresenter;

public class HandleRewindButtonRelease {
	
	@Test
	public void Given_PlayerHasPlayedBeforeRewind__Then_PlayerResumesPlaying() throws Exception {
		final boolean PLAYER_IS_PLAYING = true;
		
		AudioPlayerView playerView = mock(AudioPlayerView.class);
		AudioPlayer player = mock(AudioPlayer.class);
		when(player.isPlaying()).thenReturn(PLAYER_IS_PLAYING);
		
		Scheduler rewindScheduler = mock(Scheduler.class);
		
		SutSetupResult sutSetupResult = setupSut(playerView, player, rewindScheduler);
		sutSetupResult.rewindButtonPressedListener.onEvent();
		
		// --- Act
		sutSetupResult.rewindButtonReleasedListener.onEvent();
		
		// --- Assert
		verify(rewindScheduler).cancel();
		verify(player).play();
	}
	
	@Test
	public void Given_PlayerHasNotPlayedBeforeRewind__Then_PlayerDoesNotResumePlaying() throws Exception {
		final boolean PLAYER_IS_NOT_PLAYING = false;
		
		AudioPlayerView playerView = mock(AudioPlayerView.class);
		AudioPlayer player = mock(AudioPlayer.class);
		when(player.isPlaying()).thenReturn(PLAYER_IS_NOT_PLAYING);
		
		Scheduler rewindScheduler = mock(Scheduler.class);
		
		SutSetupResult sutSetupResult = setupSut(playerView, player, rewindScheduler);
		sutSetupResult.rewindButtonPressedListener.onEvent();
		
		// --- Act
		sutSetupResult.rewindButtonReleasedListener.onEvent();
		
		// --- Assert
		verify(rewindScheduler).cancel();
		verify(player, never()).play();
	}
	
	private SutSetupResult setupSut(AudioPlayerView playerView, 
			AudioPlayer audioPlayer, Scheduler rewindScheduler) {
		SutSetupResult result = new SutSetupResult();

		ArgumentCaptor<OnEventListener> rewindButtonPressedListenerCaptor = 
				ArgumentCaptor.forClass(OnEventListener.class);
		doNothing().when(playerView).addRewindPressedListener(rewindButtonPressedListenerCaptor.capture());
		
		ArgumentCaptor<OnEventListener> rewindButtonReleasedListenerCaptor = 
				ArgumentCaptor.forClass(OnEventListener.class);
		doNothing().when(playerView).addRewindReleasedListener(rewindButtonReleasedListenerCaptor.capture());
		
		AudioPlayerPresenter sut = new AudioPlayerPresenter(playerView, audioPlayer);
		sut.setAudioUpdateScheduler(mock(Scheduler.class));
		sut.setMediaAssetManager(mock(MediaAssetManager.class));
		sut.setAudioRewindScheduler(rewindScheduler);
		
		result.sut = sut;
		result.rewindButtonPressedListener = rewindButtonPressedListenerCaptor.getValue();
		result.rewindButtonReleasedListener = rewindButtonReleasedListenerCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		@SuppressWarnings("unused")
		public AudioPlayerPresenter sut;
		public OnEventListener rewindButtonPressedListener;
		public OnEventListener rewindButtonReleasedListener;
	}
}
