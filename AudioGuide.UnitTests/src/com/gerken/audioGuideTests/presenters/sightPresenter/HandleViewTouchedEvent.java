package com.gerken.audioGuideTests.presenters.sightPresenter;

import static org.mockito.Mockito.*;

import java.util.Random;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.interfaces.ApplicationSettingsStorage;
import com.gerken.audioGuide.interfaces.DownscalingBitmapLoader;
import com.gerken.audioGuide.interfaces.NewSightLookGotInRangeRaiser;
import com.gerken.audioGuide.interfaces.listeners.OnEventListener;
import com.gerken.audioGuide.interfaces.listeners.OnSightLookGotInRangeListener;
import com.gerken.audioGuide.interfaces.views.AudioPlayerView;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.objectModel.NextRoutePoint;
import com.gerken.audioGuide.objectModel.Sight;
import com.gerken.audioGuide.objectModel.SightLook;
import com.gerken.audioGuide.presenters.SightPresenter;

public class HandleViewTouchedEvent {	
	private Random _random = new Random(System.currentTimeMillis());

	@Test
	public void Given_NoSightInRange__Then_PlayerPanelIsNotDisplayed() throws Exception {
		
		SightView sightView = mock(SightView.class);
		NewSightLookGotInRangeRaiser sightLookFinder = mock(NewSightLookGotInRangeRaiser.class);

		SutSetupResult sutSetupResult = setupSut(sightView, sightLookFinder);
		
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(null);
		
		// --- Act
		sutSetupResult.sightViewTouchListener.onEvent();
		
		// --- Assert
		verify(sightView, never()).showPlayerPanel();
	}
	
	@Test
	public void Given_SightIsInRange__Then_PlayerPanelIsDisplayed() throws Exception {
		
		SightView sightView = mock(SightView.class);
		NewSightLookGotInRangeRaiser sightLookFinder = mock(NewSightLookGotInRangeRaiser.class);
		
		SutSetupResult sutSetupResult = setupSut(sightView, sightLookFinder);
		
		// set to locate a sight look
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(
				CreateSightWithSingleSightLook().getSightLooks().get(0));
		reset(sightView);
		
		// --- Act		
		sutSetupResult.sightViewTouchListener.onEvent();
		
		// --- Assert
		verify(sightView).showPlayerPanel();
	}

	@Test
	public void Given_SightIsInRange_AndNextRoutePointIsShown__Then_SightNameIsShown_AndNextRoutePointIsHidden() throws Exception {

		final double EXPECTED_LOCATION_LATITUDE = 12.345;
		final double EXPECTED_LOCATION_LONGITUDE = 24.567;
		int expectedRouteId = _random.nextInt();
		short someHeadingDeg = (short)_random.nextInt(Short.MAX_VALUE);
		byte someHorizonPerc = (byte)_random.nextInt(Byte.MAX_VALUE);
		final String EXPECTED_SIGHT_NAME = "Colosseum";
		final String EXPECTED_NEXT_ROUTE_POINT_NAME = "Eiffel Tower";		
		
		NextRoutePoint expectedRoutePoint = new NextRoutePoint(expectedRouteId, 
				someHeadingDeg, someHorizonPerc, EXPECTED_NEXT_ROUTE_POINT_NAME);		
		Sight sight = CreateSightWithSingleSightLook(
				EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE, EXPECTED_SIGHT_NAME);
		sight.getSightLooks().get(0).getNextRoutePoints().add(expectedRoutePoint);
		
		SightView sightView = mock(SightView.class);
		AudioPlayerView playerView = mock(AudioPlayerView.class);
		NewSightLookGotInRangeRaiser sightLookFinder = mock(NewSightLookGotInRangeRaiser.class);

		ApplicationSettingsStorage settingsStorage = 
				CreateApplicationSettingsStorageWithRouteChosen(expectedRouteId);
		
		// --- SUT
		SutSetupResult sutSetupResult = setupSut(sightView, playerView, sightLookFinder, settingsStorage);
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(sight.getSightLooks().get(0));
		sutSetupResult.stopButtonPressedListener.onEvent();
		reset(sightView);

		// --- Act		
		sutSetupResult.sightViewTouchListener.onEvent();
		
		// --- Assert
		verify(sightView).hideNextSightDirection();
		verify(sightView).setInfoPanelCaptionText(EXPECTED_SIGHT_NAME);
	}

	private Sight CreateSightWithSingleSightLook() {
		final String WHATEVER_STRING = "whatever";	
		final double WHATEVER_DOUBLE = 12.3456;
		return CreateSightWithSingleSightLook(WHATEVER_DOUBLE, WHATEVER_DOUBLE, WHATEVER_STRING);
	}
	
	private Sight CreateSightWithSingleSightLook(double latitude, double longitude, String sightName) {
		final String WHATEVER_STRING = "whatever";	
		
		SightLook expectedSightLook = new SightLook(
				latitude, longitude, WHATEVER_STRING);
		Sight expectedSight = new Sight(_random.nextInt(), sightName, WHATEVER_STRING);
		expectedSight.addLook(expectedSightLook);
		
		return expectedSight;
	}
	
	private ApplicationSettingsStorage CreateApplicationSettingsStorageWithRouteChosen(int routeId) {
		final boolean ROUTE_IS_CHOSEN = true;
		ApplicationSettingsStorage settingsStorage = mock(ApplicationSettingsStorage.class);
		when(settingsStorage.isRouteChosen()).thenReturn(ROUTE_IS_CHOSEN);
		when(settingsStorage.getCurrentRouteId()).thenReturn(routeId);
		return settingsStorage;
	}
	
	private SutSetupResult setupSut(SightView sightView, NewSightLookGotInRangeRaiser sightLookFinder) {
		return setupSut(sightView, mock(AudioPlayerView.class), 
				sightLookFinder, mock(ApplicationSettingsStorage.class));
	}
	
	private SutSetupResult setupSut(SightView sightView, AudioPlayerView playerView, 
			NewSightLookGotInRangeRaiser sightLookFinder,
			ApplicationSettingsStorage settingsStorage) {
		SutSetupResult result = new SutSetupResult();
		
		ArgumentCaptor<OnEventListener> sightViewTouchedListenerCaptor = 
				ArgumentCaptor.forClass(OnEventListener.class);
		doNothing().when(sightView).addViewTouchedListener(sightViewTouchedListenerCaptor.capture());
		
		ArgumentCaptor<OnEventListener> stopButtonPressedListenerCaptor = 
				ArgumentCaptor.forClass(OnEventListener.class);
		doNothing().when(playerView).addStopPressedListener(stopButtonPressedListenerCaptor.capture());
		
		ArgumentCaptor<OnSightLookGotInRangeListener> sightLookGotInRangeListenerCaptor = 
				ArgumentCaptor.forClass(OnSightLookGotInRangeListener.class);
		doNothing().when(sightLookFinder)
			.addSightLookGotInRangeListener(sightLookGotInRangeListenerCaptor.capture());
		
		SightPresenter sut = new SightPresenter(new City(), sightView, playerView);
		sut.setNewSightLookGotInRangeRaiser(sightLookFinder);
		sut.setBitmapLoader(mock(DownscalingBitmapLoader.class));
		sut.setApplicationSettingsStorage(settingsStorage);
		
		result.sut = sut;
		result.sightViewTouchListener = sightViewTouchedListenerCaptor.getValue();
		result.sightLookGotInRangeListener = sightLookGotInRangeListenerCaptor.getValue();
		result.stopButtonPressedListener = stopButtonPressedListenerCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		public SightPresenter sut;
		public OnEventListener sightViewTouchListener;
		public OnSightLookGotInRangeListener sightLookGotInRangeListener;
		public OnEventListener stopButtonPressedListener;
	}
}
