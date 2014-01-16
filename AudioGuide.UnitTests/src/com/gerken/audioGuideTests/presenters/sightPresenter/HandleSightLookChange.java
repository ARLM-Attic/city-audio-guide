package com.gerken.audioGuideTests.presenters.sightPresenter;

import static org.mockito.Mockito.*;

import java.util.Random;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.interfaces.*;
import com.gerken.audioGuide.interfaces.listeners.OnSightLookGotInRangeListener;
import com.gerken.audioGuide.interfaces.views.AudioPlayerView;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.objectModel.*;
import com.gerken.audioGuide.presenters.SightPresenter;;

public class HandleSightLookChange {
	private Random _random = new Random(System.currentTimeMillis());

	@Test
	public void Given_NewSightGotInRange__Then_ViewNotifiedAboutNewSight() throws Exception {
		final double EXPECTED_LOCATION_LATITUDE = 12.345;
		final double EXPECTED_LOCATION_LONGITUDE = 24.567;
		final String EXPECTED_SIGHT_LOOK_IMAGE_NAME = "colosseum.jpg";
		final String EXPECTED_SIGHT_NAME = "Colosseum";
		final int EXPECTED_VIEW_WIDTH = 240;
		final int EXPECTED_VIEW_HEIGHT = 320;
		
		Sight sight = createSightWithSingleSightLook(
				EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE, EXPECTED_SIGHT_NAME, EXPECTED_SIGHT_LOOK_IMAGE_NAME);	
		SightLook expectedSightLook = sight.getSightLooks().get(0);
		
		SightView sightView = mock(SightView.class);
		when(sightView.getWidth()).thenReturn(EXPECTED_VIEW_WIDTH);
		when(sightView.getHeight()).thenReturn(EXPECTED_VIEW_HEIGHT);
		DownscalingBitmapLoader bmpLoader = mock(DownscalingBitmapLoader.class);
		
		SutSetupResult sutSetupResult = setupSut(sightView, bmpLoader);
		
		// --- Act
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(expectedSightLook);
		
		// --- Assert
		verify(sightView).setInfoPanelCaptionText(EXPECTED_SIGHT_NAME);
		verify(bmpLoader).load(EXPECTED_SIGHT_LOOK_IMAGE_NAME,
				EXPECTED_VIEW_WIDTH, EXPECTED_VIEW_HEIGHT);
	}

	@Test
	public void Given_NewSightGotInRange__Then_AudioNotificationPlayed() throws Exception {
		Sight sight = createSightWithSingleSightLook();
		SightLook expectedSightLook = sight.getSightLooks().get(0);
		
		SightView sightView = mock(SightView.class);
		AudioNotifier audioNotifier = mock(AudioNotifier.class);
		
		SutSetupResult sutSetupResult = setupSut(sightView, audioNotifier);
		
		// --- Act
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(expectedSightLook);
		
		// --- Assert
		verify(audioNotifier).signalSightInRange();
	}
	
	@Test
	public void Given_SightGotOutOfRange__Then_CaptionReset_NextSightDirectionHidden() throws Exception {
		
		Sight sight = createSightWithSingleSightLook();	
		SightLook previousSightLook = sight.getSightLooks().get(0);
		
		SightView sightView = mock(SightView.class);
		AudioPlayer audioPlayer = mock(AudioPlayer.class);
		
		SutSetupResult sutSetupResult = setupSut(sightView, audioPlayer);
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(previousSightLook);
		
		// --- Act
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(null);
		
		// --- Assert
		verify(sightView).resetInfoPanelCaptionText();
		verify(sightView, atLeastOnce()).hideNextSightDirection();
	}
	
	@Test
	public void Given_SightGotOutOfRange__Then_NoSightInRangeImageLoaded() throws Exception {
		final String EXPECTED_NO_SIGHT_IN_RANGE_IMAGE_NAME = "nosight.jpg";
		
		Sight sight = createSightWithSingleSightLook();	
		SightLook previousSightLook = sight.getSightLooks().get(0);
		
		CityConfiguration config = new CityConfiguration(EXPECTED_NO_SIGHT_IN_RANGE_IMAGE_NAME,
				createRandomString(), createRandomString());
		City city = new City(_random.nextInt(), createRandomString(), config);
		SightView sightView = mock(SightView.class);
		when(sightView.getWidth()).thenReturn(_random.nextInt());
		when(sightView.getHeight()).thenReturn(_random.nextInt());
		DownscalingBitmapLoader bmpLoader = mock(DownscalingBitmapLoader.class);
		//when(bmpLoader.load(anyString(), anyInt(), anyInt())).thenReturn(null);
		
		SutSetupResult sutSetupResult = setupSut(city, sightView, bmpLoader);
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(previousSightLook);
		
		// --- Act
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(null);
		
		// --- Assert
		verify(bmpLoader).load(eq(EXPECTED_NO_SIGHT_IN_RANGE_IMAGE_NAME), anyInt(), anyInt());
	}	
	
	@Test
	public void Given_SightGotOutOfRange_PlayerIsPlaying__Then_PlayerIsStopped() throws Exception {
		final double EXPECTED_LOCATION_LATITUDE = 12.345;
		final double EXPECTED_LOCATION_LONGITUDE = 24.567;
		final String EXPECTED_SIGHT_LOOK_IMAGE_NAME = "colosseum.jpg";
		final String EXPECTED_SIGHT_NAME = "Colosseum";
		final boolean PLAYER_IS_PLAYING = true;
		
		Sight sight = createSightWithSingleSightLook(
				EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE, EXPECTED_SIGHT_NAME, EXPECTED_SIGHT_LOOK_IMAGE_NAME);	
		SightLook previousSightLook = sight.getSightLooks().get(0);
		
		SightView sightView = mock(SightView.class);
		AudioPlayer audioPlayer = mock(AudioPlayer.class);
		when(audioPlayer.isPlaying()).thenReturn(PLAYER_IS_PLAYING);
		
		SutSetupResult sutSetupResult = setupSut(sightView, audioPlayer);
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(previousSightLook);
		
		// --- Act
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(null);
		
		// --- Assert
		verify(audioPlayer).stop();
	}
	
	@Test
	public void Given_SightGotOutOfRange_PlayerIsNotPlaying__Then_PlayerIsNotStopped() throws Exception {
		final double EXPECTED_LOCATION_LATITUDE = 12.345;
		final double EXPECTED_LOCATION_LONGITUDE = 24.567;
		final String EXPECTED_SIGHT_LOOK_IMAGE_NAME = "colosseum.jpg";
		final String EXPECTED_SIGHT_NAME = "Colosseum";
		final boolean PLAYER_IS_NOT_PLAYING = false;
		
		Sight sight = createSightWithSingleSightLook(
				EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE, EXPECTED_SIGHT_NAME, EXPECTED_SIGHT_LOOK_IMAGE_NAME);	
		SightLook previousSightLook = sight.getSightLooks().get(0);
		
		SightView sightView = mock(SightView.class);
		AudioPlayer audioPlayer = mock(AudioPlayer.class);
		when(audioPlayer.isPlaying()).thenReturn(PLAYER_IS_NOT_PLAYING);
		
		SutSetupResult sutSetupResult = setupSut(sightView, audioPlayer);
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(previousSightLook);
		
		// --- Act
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(null);
		
		// --- Assert
		verify(audioPlayer, never()).stop();
	}
	
	@Test
	public void Given_NewSightGotInRange__Then_NextRoutePointArrowHidden() throws Exception {
		final double EXPECTED_LOCATION_LATITUDE = 12.345;
		final double EXPECTED_LOCATION_LONGITUDE = 24.567;

		Sight sight = createSightWithSingleSightLook(
				EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE);	
		SightLook expectedSightLook = sight.getSightLooks().get(0);
		
		SightView sightView = mock(SightView.class);
		DownscalingBitmapLoader bmpLoader = mock(DownscalingBitmapLoader.class);
		
		SutSetupResult sutSetupResult = setupSut(sightView, bmpLoader);
		
		// --- Act
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(expectedSightLook);
		
		// --- Assert
		verify(sightView).hideNextSightDirection();
	}
	
	@Test
	public void Given_NewLookOfTheSameSightGotInRange__Then_CaptionSetToCurrentSightName() throws Exception {
		final String EXPECTED_SIGHT_NAME = "Colosseum";
		final int EXPECTED_VIEW_WIDTH = 240;
		final int EXPECTED_VIEW_HEIGHT = 320;
		
		Sight sight = createSightWithSingleSightLook(
				_random.nextDouble(), _random.nextDouble(), EXPECTED_SIGHT_NAME, createRandomString());	
		SightLook firstSightLook = sight.getSightLooks().get(0);
		SightLook secondSightLook = new SightLook(
				_random.nextDouble(), _random.nextDouble(),	createRandomString());
		secondSightLook.setSight(sight);
		sight.getSightLooks().add(secondSightLook);
		
		SightView sightView = mock(SightView.class);
		when(sightView.getWidth()).thenReturn(EXPECTED_VIEW_WIDTH);
		when(sightView.getHeight()).thenReturn(EXPECTED_VIEW_HEIGHT);
		DownscalingBitmapLoader bmpLoader = mock(DownscalingBitmapLoader.class);
		
		SutSetupResult sutSetupResult = setupSut(sightView, bmpLoader);
		
		// --- Act
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(firstSightLook);
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(secondSightLook);
		
		// --- Assert
		verify(sightView, times(2)).setInfoPanelCaptionText(EXPECTED_SIGHT_NAME);
	}
	

	private String createRandomString() {
		return String.valueOf(_random.nextLong());
	}
	
	private Sight createSightWithSingleSightLook() {
		return createSightWithSingleSightLook(_random.nextDouble(), _random.nextDouble(), 
			createRandomString(), createRandomString());
	}
	
	private Sight createSightWithSingleSightLook(double latitude, double longitude) {
		return createSightWithSingleSightLook(latitude, longitude, 
			createRandomString(), createRandomString());
	}
	
	private Sight createSightWithSingleSightLook(double latitude, double longitude, 
			String sightName, String lookImageName) {
		
		SightLook expectedSightLook = new SightLook(
				latitude, longitude, lookImageName);
		Sight expectedSight = new Sight(_random.nextInt(), sightName, createRandomString());
		expectedSight.addLook(expectedSightLook);
		
		return expectedSight;
	}
	
	private SutSetupResult setupSut(SightView sightView, DownscalingBitmapLoader bmpLoader) {
		return setupSut(new City(), sightView, mock(AudioPlayerView.class), 
				mock(AudioPlayer.class), mock(AudioNotifier.class),
				mock(NewSightLookGotInRangeRaiser.class), bmpLoader);
	}
	
	private SutSetupResult setupSut(SightView sightView, AudioNotifier notifier) {
		return setupSut(new City(), sightView, mock(AudioPlayerView.class), mock(AudioPlayer.class), notifier,
				mock(NewSightLookGotInRangeRaiser.class), mock(DownscalingBitmapLoader.class));
	}
	
	private SutSetupResult setupSut(SightView sightView, AudioPlayer audioPlayer) {
		return setupSut(new City(), sightView, mock(AudioPlayerView.class), 
				audioPlayer, mock(AudioNotifier.class),
				mock(NewSightLookGotInRangeRaiser.class), mock(DownscalingBitmapLoader.class));
	}
	
	private SutSetupResult setupSut(City city, SightView sightView, 
			DownscalingBitmapLoader bmpLoader) {
		return setupSut(city, sightView, mock(AudioPlayerView.class), 
				mock(AudioPlayer.class), mock(AudioNotifier.class),
				mock(NewSightLookGotInRangeRaiser.class), bmpLoader);
	}
	
	private SutSetupResult setupSut(City city, SightView sightView, AudioPlayerView playerView, 
			AudioPlayer player, AudioNotifier notifier,
			NewSightLookGotInRangeRaiser sightLookFinder,
			DownscalingBitmapLoader bmpLoader) {
		SutSetupResult result = new SutSetupResult();
		
		ArgumentCaptor<OnSightLookGotInRangeListener> sightLookGotInRangeListenerCaptor = 
				ArgumentCaptor.forClass(OnSightLookGotInRangeListener.class);
		doNothing().when(sightLookFinder)
			.addSightLookGotInRangeListener(sightLookGotInRangeListenerCaptor.capture());
		
		SightPresenter sut = new SightPresenter(city, sightView, playerView);
		sut.setAudioPlayer(player);
		sut.setAudioNotifier(notifier);
		sut.setNewSightLookGotInRangeRaiser(sightLookFinder);
		sut.setBitmapLoader(bmpLoader);
		sut.setMediaAssetManager(mock(MediaAssetManager.class));
		
		result.sut = sut;
		result.sightLookGotInRangeListener = sightLookGotInRangeListenerCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		public SightPresenter sut;
		public OnSightLookGotInRangeListener sightLookGotInRangeListener;
	}
}
