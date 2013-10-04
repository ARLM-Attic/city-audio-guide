package com.gerken.audioGuideTests.presenters.sightPresenter;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.util.Random;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.interfaces.*;
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
	public void Given_NewSightGotInRange__Then_AudioNotificationPalyed() throws Exception {
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
	/*
	@Test
	public void Given_NewSightGotInRange__Then_NextRoutePointArrowHidden() throws Exception {
		final double EXPECTED_LOCATION_LATITUDE = 12.345;
		final double EXPECTED_LOCATION_LONGITUDE = 24.567;

		City city = CreateSingleSightLookModel(EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE);
		
		SightView view = mock(SightView.class);
		AssetStreamProvider assetStreamProvider = mock(AssetStreamProvider.class);
		when(assetStreamProvider.getImageAssetStream(anyString()))
			.thenReturn(new ByteArrayInputStream(new byte[]{1,2,3}));
		
		SightPresenter sut = CreateSut(city, view, assetStreamProvider);
		
		// --- Act
		sut.handleLocationChange(EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE);
		
		// --- Assert
		verify(view).hideNextSightDirection();
	}
	
	@Test
	public void Given_NewSightLookGotInRange__Then_ViewNotifiedAboutNewSightLook() throws Exception {
		final double FIRST_SIGHTLOOK_LATITUDE = 12.345;
		final double FIRST_SIGHTLOOK_LONGITUDE = 24.567;
		final double SECOND_SIGHTLOOK_LATITUDE = 54.321;
		final double SECOND_SIGHTLOOK_LONGITUDE = 65.432;
		final String FIRST_SIGHT_LOOK_IMAGE_NAME = "colosseum1.jpg";
		final String SECOND_SIGHT_LOOK_IMAGE_NAME = "colosseum2.jpg";
		final String EXPECTED_SIGHT_NAME = "Colosseum";
		final int EXPECTED_VIEW_WIDTH = 240;
		final int EXPECTED_VIEW_HEIGHT = 320;
		ByteArrayInputStream FIRST_SIGHT_LOOK_IMAGE_STREAM = 
				new ByteArrayInputStream(new byte[]{1,2,3});
		ByteArrayInputStream SECOND_SIGHT_LOOK_IMAGE_STREAM = 
				new ByteArrayInputStream(new byte[]{4,5,6});
		
		final String WHATEVER_STRING = "whatever";		
		
		SightLook firstSightLook = new SightLook(
				FIRST_SIGHTLOOK_LATITUDE, FIRST_SIGHTLOOK_LONGITUDE, FIRST_SIGHT_LOOK_IMAGE_NAME);
		SightLook secondSightLook = new SightLook(
				SECOND_SIGHTLOOK_LATITUDE, SECOND_SIGHTLOOK_LONGITUDE, SECOND_SIGHT_LOOK_IMAGE_NAME);
		Sight expectedSight = new Sight(1, EXPECTED_SIGHT_NAME, "audio.mp3");
		expectedSight.addLook(firstSightLook);
		expectedSight.addLook(secondSightLook);
		Sight unexpectedSight = new Sight(2, "", "audio.mp3");
		City city = new City(1, "Default", WHATEVER_STRING);
		city.getSights().add(expectedSight);
		city.getSights().add(unexpectedSight);			
		
		
		SightView view = mock(SightView.class);
		when(view.getWidth()).thenReturn(EXPECTED_VIEW_WIDTH);
		when(view.getHeight()).thenReturn(EXPECTED_VIEW_HEIGHT);
		AssetStreamProvider assetStreamProvider = mock(AssetStreamProvider.class);
		when(assetStreamProvider.getImageAssetStream(FIRST_SIGHT_LOOK_IMAGE_NAME))
			.thenReturn(FIRST_SIGHT_LOOK_IMAGE_STREAM);
		when(assetStreamProvider.getImageAssetStream(SECOND_SIGHT_LOOK_IMAGE_NAME))
			.thenReturn(SECOND_SIGHT_LOOK_IMAGE_STREAM);
		DownscalableBitmapCreator bmpCreator = mock(DownscalableBitmapCreator.class);
		
		SightPresenter sut = CreateSut(city, view, assetStreamProvider, bmpCreator);
		sut.handleLocationChange(FIRST_SIGHTLOOK_LATITUDE, FIRST_SIGHTLOOK_LONGITUDE);
		
		// --- Act
		sut.handleLocationChange(SECOND_SIGHTLOOK_LATITUDE, SECOND_SIGHTLOOK_LONGITUDE);
		
		// --- Assert
		verify(bmpCreator).CreateDownscalableBitmap(SECOND_SIGHT_LOOK_IMAGE_STREAM,
				EXPECTED_VIEW_WIDTH, EXPECTED_VIEW_HEIGHT);
	}
	
	private City CreateSingleSightLookModel(double latitude, double longitude) {
		final String WHATEVER_STRING = "whatever";	
		return CreateSingleSightLookModel(latitude, longitude, WHATEVER_STRING);
	}
	
	private City CreateSingleSightLookModel(double latitude, double longitude, String sightName) {
		final String WHATEVER_STRING = "whatever";	
		
		SightLook expectedSightLook = new SightLook(
				latitude, longitude, WHATEVER_STRING);
		Sight expectedSight = new Sight(_random.nextInt(), sightName, WHATEVER_STRING);
		expectedSight.addLook(expectedSightLook);
		City city = new City(_random.nextInt(), WHATEVER_STRING, WHATEVER_STRING);
		city.getSights().add(expectedSight);
		
		return city;
	}
	
	private SightPresenter CreateSut(City city, SightView view, AssetStreamProvider assetStreamProvider) {
		return CreateSut(city, view, assetStreamProvider, 
				mock(AudioPlayer.class), mock(DownscalableBitmapCreator.class));
	}
	
	private SightPresenter CreateSut(City city, SightView view, AssetStreamProvider assetStreamProvider, DownscalableBitmapCreator bmpCreator) {		
		return CreateSut(city, view, assetStreamProvider, 
				mock(AudioPlayer.class), 
				bmpCreator);
	}
	
	private SightPresenter CreateSut(City city, SightView view, AssetStreamProvider assetStreamProvider, AudioPlayer player) {		
		return CreateSut(city, view, assetStreamProvider, player, 
				mock(DownscalableBitmapCreator.class));
	}
	
	private SightPresenter CreateSut(City city, SightView view, AssetStreamProvider assetStreamProvider, AudioPlayer player, DownscalableBitmapCreator bmpCreator) {		
		ApplicationSettingsStorage settingsStorage = mock(ApplicationSettingsStorage.class);
		
		SightPresenterDependencyCreator factory = mock(SightPresenterDependencyCreator.class);
		when(factory.createApplicationSettingsStorage()).thenReturn(settingsStorage);
		when(factory.createAssetStreamProvider()).thenReturn(assetStreamProvider);
		when(factory.createDownscalableBitmapCreator()).thenReturn(bmpCreator);
		when(factory.createLogger()).thenReturn(mock(Logger.class));
		
		return new SightPresenter(city, view, player, factory);
	}
*/
	private Sight createSightWithSingleSightLook() {
		final String WHATEVER_STRING = "whatever";	
		final double WHATEVER_DOUBLE = 12.3456;
		return createSightWithSingleSightLook(WHATEVER_DOUBLE, WHATEVER_DOUBLE, WHATEVER_STRING, WHATEVER_STRING);
	}
	
	private Sight createSightWithSingleSightLook(double latitude, double longitude, 
			String sightName, String lookImageName) {
		final String WHATEVER_STRING = "whatever";	
		
		SightLook expectedSightLook = new SightLook(
				latitude, longitude, lookImageName);
		Sight expectedSight = new Sight(_random.nextInt(), sightName, WHATEVER_STRING);
		expectedSight.addLook(expectedSightLook);
		
		return expectedSight;
	}
	
	private SutSetupResult setupSut(SightView sightView, DownscalingBitmapLoader bmpLoader) {
		return setupSut(sightView, mock(AudioPlayerView.class), 
				mock(AudioPlayer.class), mock(AudioNotifier.class),
				mock(NewSightLookGotInRangeRaiser.class), bmpLoader);
	}
	
	private SutSetupResult setupSut(SightView sightView, AudioNotifier notifier) {
		return setupSut(sightView, mock(AudioPlayerView.class), mock(AudioPlayer.class), notifier,
				mock(NewSightLookGotInRangeRaiser.class), mock(DownscalingBitmapLoader.class));
	}
	
	private SutSetupResult setupSut(SightView sightView, AudioPlayer audioPlayer) {
		return setupSut(sightView, mock(AudioPlayerView.class), 
				audioPlayer, mock(AudioNotifier.class),
				mock(NewSightLookGotInRangeRaiser.class), mock(DownscalingBitmapLoader.class));
	}
	
	private SutSetupResult setupSut(SightView sightView, AudioPlayerView playerView, 
			AudioPlayer player, AudioNotifier notifier,
			NewSightLookGotInRangeRaiser sightLookFinder,
			DownscalingBitmapLoader bmpLoader) {
		SutSetupResult result = new SutSetupResult();
		
		ArgumentCaptor<OnSightLookGotInRangeListener> sightLookGotInRangeListenerCaptor = 
				ArgumentCaptor.forClass(OnSightLookGotInRangeListener.class);
		doNothing().when(sightLookFinder)
			.addSightLookGotInRangeListener(sightLookGotInRangeListenerCaptor.capture());
		
		SightPresenter sut = new SightPresenter(new City(), sightView, playerView);
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
