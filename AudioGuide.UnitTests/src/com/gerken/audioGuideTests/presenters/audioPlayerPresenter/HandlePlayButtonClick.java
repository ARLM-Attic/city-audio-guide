package com.gerken.audioGuideTests.presenters.audioPlayerPresenter;

import static org.mockito.Mockito.*;

import java.util.Random;
import java.util.TimerTask;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.interfaces.AudioPlayer;
import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.Scheduler;
import com.gerken.audioGuide.interfaces.views.AudioPlayerView;
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

	@Test
	public void Given_PlayerIsNotPlaying__Then_AudioPositionUpdateScheduled() throws Exception {

		final boolean PLAYER_IS_NOT_PLAYING = false;	
		final int expectedPlayerPosition = _random.nextInt(); 
		
		AudioPlayerView playerView = mock(AudioPlayerView.class);
		AudioPlayer player = mock(AudioPlayer.class);
		when(player.getCurrentPosition()).thenReturn(expectedPlayerPosition);
		when(player.isPlaying()).thenReturn(PLAYER_IS_NOT_PLAYING);
		
		Scheduler scheduler = mock(Scheduler.class);
		ArgumentCaptor<TimerTask> timerTaskCaptor = 
				ArgumentCaptor.forClass(TimerTask.class);
		doNothing().when(scheduler).scheduleAtFixedRate(timerTaskCaptor.capture(), anyLong(), anyLong());
		
		SutSetupResult sutSetupResult = setupSut(playerView, player, scheduler);
		
		// --- Act
		sutSetupResult.playButtonPressedListener.onEvent();
		timerTaskCaptor.getValue().run();
		
		// --- Assert
		verify(playerView).setAudioProgressPosition(expectedPlayerPosition);
	}
	
	private SutSetupResult setupSut(AudioPlayerView playerView, AudioPlayer audioPlayer) {
		return setupSut(playerView, audioPlayer, mock(Scheduler.class));
	}
	
	private SutSetupResult setupSut(AudioPlayerView playerView, 
			AudioPlayer audioPlayer, Scheduler scheduler) {
		SutSetupResult result = new SutSetupResult();

		ArgumentCaptor<OnEventListener> playButtonPressedListenerCaptor = 
				ArgumentCaptor.forClass(OnEventListener.class);
		doNothing().when(playerView).addPlayPressedListener(playButtonPressedListenerCaptor.capture());
		
		
		AudioPlayerPresenter sut = new AudioPlayerPresenter(playerView, audioPlayer);
		sut.setAudioUpdateScheduler(scheduler);
		
		result.sut = sut;
		result.playButtonPressedListener = playButtonPressedListenerCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		public AudioPlayerPresenter sut;
		public OnEventListener playButtonPressedListener;
	}
}
