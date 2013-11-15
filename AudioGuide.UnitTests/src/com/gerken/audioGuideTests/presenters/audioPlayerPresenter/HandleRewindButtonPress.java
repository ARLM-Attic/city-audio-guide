package com.gerken.audioGuideTests.presenters.audioPlayerPresenter;

import static org.mockito.Mockito.*;

import java.util.Random;
import java.util.TimerTask;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.interfaces.AudioPlayer;
import com.gerken.audioGuide.interfaces.MediaAssetManager;
import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.Scheduler;
import com.gerken.audioGuide.interfaces.views.AudioPlayerView;
import com.gerken.audioGuide.presenters.AudioPlayerPresenter;

public class HandleRewindButtonPress {
	private Random _random = new Random(System.currentTimeMillis());
	
	@Test
	public void Given_UiUpdaterTaskRun__Then_AudioProgressPositionSet_AudioPositionSet() throws Exception {
		final int MSEC_IN_MIN = 6000;
		int expectedAudioPositionMs = _random.nextInt(MSEC_IN_MIN);
		
		AudioPlayerView playerView = mock(AudioPlayerView.class);
		AudioPlayer player = mock(AudioPlayer.class);
		when(player.isPlaying()).thenReturn(true);
		when(player.getCurrentPosition()).thenReturn(expectedAudioPositionMs);
		
		Scheduler audioUpdateScheduler = mock(Scheduler.class);
		Scheduler rewindScheduler = mock(Scheduler.class);
		
		SutSetupResult sutSetupResult = setupSut(playerView, player, audioUpdateScheduler, rewindScheduler);		
		
		// --- Act
		sutSetupResult.rewindButtonPressedListener.onEvent();
		sutSetupResult.uiUpdateTaskCaptor.getValue().run();
		
		// --- Assert
		verify(playerView).setAudioProgressPosition(expectedAudioPositionMs);
		verify(playerView).setAudioPosition(String.format("0:%02d", expectedAudioPositionMs/1000));
	}
	
	@Test
	public void Given_AudioPlayerUpdaterTaskRun__Then_AudioPlayerPositionChanged() throws Exception {
		final float REWIND_STEP_RATIO = 0.05f;
		final int AUDIO_DURATION_MS = 100000;
		final int AUDIO_CURRENT_POSITION_MS = 10000;
		final int AUDIO_NEW_POSITION_MS = 5000;
		
		AudioPlayerView playerView = mock(AudioPlayerView.class);
		AudioPlayer player = mock(AudioPlayer.class);
		when(player.isPlaying()).thenReturn(true);
		when(player.getCurrentPosition()).thenReturn(AUDIO_CURRENT_POSITION_MS);
		when(player.getDuration()).thenReturn(AUDIO_DURATION_MS);
		
		Scheduler audioUpdateScheduler = mock(Scheduler.class);
		Scheduler rewindScheduler = mock(Scheduler.class);
		
		SutSetupResult sutSetupResult = setupSut(playerView, player, audioUpdateScheduler, rewindScheduler);
		sutSetupResult.sut.setRewindStepRatio(REWIND_STEP_RATIO);		
		
		// --- Act
		sutSetupResult.rewindButtonPressedListener.onEvent();
		sutSetupResult.playerUpdateTaskCaptor.getValue().run();
		
		// --- Assert
		verify(player).seekTo(AUDIO_NEW_POSITION_MS);
	}
	
	private SutSetupResult setupSut(AudioPlayerView playerView, AudioPlayer audioPlayer,
			Scheduler audioUpdateScheduler, Scheduler rewindScheduler) {
		SutSetupResult result = new SutSetupResult();

		ArgumentCaptor<OnEventListener> rewindButtonPressedListenerCaptor = 
				ArgumentCaptor.forClass(OnEventListener.class);
		doNothing().when(playerView).addRewindPressedListener(rewindButtonPressedListenerCaptor.capture());
		
		ArgumentCaptor<TimerTask> audioUpdateTaskCaptor = 
				ArgumentCaptor.forClass(TimerTask.class);
		doNothing().when(audioUpdateScheduler).scheduleAtFixedRate(audioUpdateTaskCaptor.capture(), 
				anyLong(), anyLong());
		
		ArgumentCaptor<TimerTask> rewindTaskCaptor = 
				ArgumentCaptor.forClass(TimerTask.class);
		doNothing().when(rewindScheduler).scheduleAtFixedRate(rewindTaskCaptor.capture(), 
				anyLong(), anyLong());
		
		AudioPlayerPresenter sut = new AudioPlayerPresenter(playerView, audioPlayer);
		sut.setMediaAssetManager(mock(MediaAssetManager.class));
		sut.setAudioUpdateScheduler(audioUpdateScheduler);
		sut.setAudioRewindScheduler(rewindScheduler);
		
		result.sut = sut;
		result.rewindButtonPressedListener = rewindButtonPressedListenerCaptor.getValue();
		result.uiUpdateTaskCaptor = audioUpdateTaskCaptor;
		result.playerUpdateTaskCaptor = rewindTaskCaptor;
		
		return result;
	}
	
	private class SutSetupResult {
		public AudioPlayerPresenter sut;
		public OnEventListener rewindButtonPressedListener;
		public ArgumentCaptor<TimerTask> uiUpdateTaskCaptor;
		public ArgumentCaptor<TimerTask> playerUpdateTaskCaptor;
	}
}
