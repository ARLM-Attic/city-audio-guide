package com.gerken.audioGuideTests.presenters.audioPlayerPresenter;

import static org.mockito.Mockito.*;

import java.util.Random;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.containers.FileInfo;
import com.gerken.audioGuide.interfaces.AudioPlayer;
import com.gerken.audioGuide.interfaces.MediaAssetManager;
import com.gerken.audioGuide.interfaces.NewSightLookGotInRangeRaiser;
import com.gerken.audioGuide.interfaces.OnSightLookGotInRangeListener;
import com.gerken.audioGuide.interfaces.views.AudioPlayerView;
import com.gerken.audioGuide.objectModel.*;
import com.gerken.audioGuide.presenters.AudioPlayerPresenter;

public class HandleSightLookIsInRange {
	private Random _random = new Random(System.currentTimeMillis());
	
	@Test
	public void Given_NewSightGotInRange__Then_PlayerViewShownAsStopped() {
		City city = createSingleSightLookModel();
		AudioPlayerView playerView = mock(AudioPlayerView.class);
		SutSetupResult sutSetupResult = setupSut(playerView);
		
		// --- Act
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(
				city.getSights().get(0).getSightLooks().get(0));
		
		// --- Assert
		verify(playerView).displayPlayerStopped();
	}

	@Test
	public void Given_NewSightGotInRange__Then_PlayerViewDisplaysZeroPosition() throws Exception {
		City city = createSingleSightLookModel();
		AudioPlayerView playerView = mock(AudioPlayerView.class);
		SutSetupResult sutSetupResult = setupSut(playerView);
		
		// --- Act
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(
				city.getSights().get(0).getSightLooks().get(0));
		
		// --- Assert
		verify(playerView).setAudioProgressPosition(0);
		verify(playerView).setAudioPosition("0:00");
	}
	
	@Test
	public void Given_NewSightGotInRange__Then_PlayerPreparedToPlaySightAudioTrack() throws Exception {
		final String AUDIO_NAME = "audio1.ogg";
		City city = createSingleSightLookModel(_random.nextDouble(), _random.nextDouble(),
				"", AUDIO_NAME);
		FileInfo dummyFileInfo = new FileInfo(null, 0);
		
		AudioPlayerView playerView = mock(AudioPlayerView.class);
		AudioPlayer player = mock(AudioPlayer.class);
		NewSightLookGotInRangeRaiser raiser = mock(NewSightLookGotInRangeRaiser.class);
		MediaAssetManager mediaAssetManager = mock(MediaAssetManager.class);
		when(mediaAssetManager.prepareAudioAsset(AUDIO_NAME)).thenReturn(dummyFileInfo);
		
		SutSetupResult sutSetupResult = setupSut(playerView, player, mediaAssetManager, raiser);
		
		// --- Act
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(
				city.getSights().get(0).getSightLooks().get(0));
		
		// --- Assert
		verify(mediaAssetManager).prepareAudioAsset(AUDIO_NAME);
		verify(player).prepareAudioAsset(dummyFileInfo);
	}

	@Test
	public void Given_SameSightOtherLookGotInRange__Then_PlayerPreparedOnlyOnce() throws Exception {
		final String WHATEVER_STRING = "whatever";
		final String AUDIO_NAME = "audio1.ogg";
		City city = createSingleSightLookModel(_random.nextDouble(), _random.nextDouble(),
				WHATEVER_STRING, AUDIO_NAME);
		SightLook otherSightLook = new SightLook(
				_random.nextDouble(), _random.nextDouble(), WHATEVER_STRING);
		Sight sight = city.getSights().get(0);
		sight.getSightLooks().add(otherSightLook);
		otherSightLook.setSight(sight);
		FileInfo dummyFileInfo = new FileInfo(null, 0);
		
		AudioPlayerView playerView = mock(AudioPlayerView.class);
		AudioPlayer player = mock(AudioPlayer.class);
		NewSightLookGotInRangeRaiser raiser = mock(NewSightLookGotInRangeRaiser.class);
		MediaAssetManager mediaAssetManager = mock(MediaAssetManager.class);
		when(mediaAssetManager.prepareAudioAsset(AUDIO_NAME)).thenReturn(dummyFileInfo);
		
		SutSetupResult sutSetupResult = setupSut(playerView, player, mediaAssetManager, raiser);
		
		// --- Act
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(
				city.getSights().get(0).getSightLooks().get(0));
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(otherSightLook);
		
		// --- Assert
		verify(mediaAssetManager, times(1)).prepareAudioAsset(AUDIO_NAME);
		verify(player, times(1)).prepareAudioAsset(dummyFileInfo);
	}
	
	private City createSingleSightLookModel() {
		final String WHATEVER_STRING = "whatever";
		return createSingleSightLookModel(_random.nextDouble(), _random.nextDouble(), 
				WHATEVER_STRING, WHATEVER_STRING);
	}
	
	private City createSingleSightLookModel(double latitude, double longitude, 
			String sightName, String audioName) {
		
		SightLook expectedSightLook = new SightLook(
				latitude, longitude, createRandomString());
		Sight expectedSight = new Sight(_random.nextInt(), sightName, audioName);
		expectedSight.addLook(expectedSightLook);
		City city = new City(_random.nextInt(), createRandomString());
		city.getSights().add(expectedSight);
		
		return city;
	}
	
	private String createRandomString() {
		return String.valueOf(_random.nextLong());
	}
	
	private SutSetupResult setupSut(AudioPlayerView playerView) {
		return setupSut(playerView, mock(AudioPlayer.class),
				mock(MediaAssetManager.class), mock(NewSightLookGotInRangeRaiser.class));
	}
	
	private SutSetupResult setupSut(AudioPlayerView playerView, 
			AudioPlayer audioPlayer, MediaAssetManager mediaAssetManager,
			NewSightLookGotInRangeRaiser newSightLookGotInRangeRaiser) {
		SutSetupResult result = new SutSetupResult();

		ArgumentCaptor<OnSightLookGotInRangeListener> sightLookGotInRangeCaptor = 
				ArgumentCaptor.forClass(OnSightLookGotInRangeListener.class);
		doNothing().when(newSightLookGotInRangeRaiser).addSightLookGotInRangeListener(
				sightLookGotInRangeCaptor.capture());		
		
		AudioPlayerPresenter sut = new AudioPlayerPresenter(playerView, audioPlayer);
		sut.setNewSightLookGotInRangeRaiser(newSightLookGotInRangeRaiser);
		sut.setMediaAssetManager(mediaAssetManager);
		
		result.sut = sut;
		result.sightLookGotInRangeListener = sightLookGotInRangeCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		public AudioPlayerPresenter sut;
		public OnSightLookGotInRangeListener sightLookGotInRangeListener;
	}
}
