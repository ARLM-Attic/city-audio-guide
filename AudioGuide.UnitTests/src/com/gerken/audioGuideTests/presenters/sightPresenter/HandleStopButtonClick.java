package com.gerken.audioGuideTests.presenters.sightPresenter;

import static org.mockito.Mockito.*;

import java.util.Random;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.interfaces.*;
import com.gerken.audioGuide.interfaces.views.AudioPlayerView;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.objectModel.*;
import com.gerken.audioGuide.presenters.SightPresenter;

public class HandleStopButtonClick {
	private Random _random = new Random(System.currentTimeMillis());

	
	@Test
	public void Given_RouteHasNotBeenChosen__Then_NextRoutePointDirectionNotShown() throws Exception {
		final String WHATEVER_STRING = "whatever";	
		final boolean ROUTE_IS_NOT_CHOSEN = false;
		
		short someHeadingDeg = (short)_random.nextInt(Short.MAX_VALUE);
		byte someHorizonPerc = (byte)_random.nextInt(Byte.MAX_VALUE);
		NextRoutePoint expectedRoutePoint = new NextRoutePoint(_random.nextInt(), 
				someHeadingDeg, someHorizonPerc, WHATEVER_STRING);		
		Sight sight = createSightWithSingleSightLook();
		sight.getSightLooks().get(0).getNextRoutePoints().add(expectedRoutePoint);
		
		SightView sightView = mock(SightView.class);
		NewSightLookGotInRangeRaiser sightLookFinder = mock(NewSightLookGotInRangeRaiser.class);
		
		ApplicationSettingsStorage settingsStorage = mock(ApplicationSettingsStorage.class);
		when(settingsStorage.isRouteChosen()).thenReturn(ROUTE_IS_NOT_CHOSEN);
		
		SutSetupResult sutSetupResult = setupSut(sightView, sightLookFinder, settingsStorage);
		
		// --- Act
		sutSetupResult.stopButtonPressedListener.onEvent();
		
		// --- Assert
		verify(sightView, never()).displayNextSightDirection(anyFloat(), anyFloat());
	}
	
	@Test
	public void Given_RouteHasBeenChosen__Then_NextRoutePointNameAndDirectionShown() throws Exception {

		int expectedRouteId = _random.nextInt();
		short expectedHeadingDeg = (short)_random.nextInt(Short.MAX_VALUE);
		byte expectedHorizonPerc = (byte)_random.nextInt(Byte.MAX_VALUE);
		final String EXPECTED_NEXT_ROUTE_POINT_NAME = "Eiffel Tower";
		final boolean ROUTE_IS_CHOSEN = true;
		
		NextRoutePoint expectedRoutePoint = new NextRoutePoint(expectedRouteId, 
				expectedHeadingDeg, expectedHorizonPerc, EXPECTED_NEXT_ROUTE_POINT_NAME);		
		Sight sight = createSightWithSingleSightLook();
		sight.getSightLooks().get(0).getNextRoutePoints().add(expectedRoutePoint);
		
		SightView sightView = mock(SightView.class);
		NewSightLookGotInRangeRaiser sightLookFinder = mock(NewSightLookGotInRangeRaiser.class);
		ApplicationSettingsStorage settingsStorage = mock(ApplicationSettingsStorage.class);
		when(settingsStorage.isRouteChosen()).thenReturn(ROUTE_IS_CHOSEN);
		when(settingsStorage.getCurrentRouteId()).thenReturn(expectedRouteId);
		
		SutSetupResult sutSetupResult = setupSut(sightView, sightLookFinder, settingsStorage);
		
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(sight.getSightLooks().get(0));
		
		// --- Act
		sutSetupResult.stopButtonPressedListener.onEvent();
		
		// --- Assert
		float expectedHeadingRad = (float)(Math.PI*expectedHeadingDeg/180.0);
		verify(sightView).displayNextSightDirection(eq(expectedHeadingRad), anyFloat());
		verify(sightView).setInfoPanelCaptionText(EXPECTED_NEXT_ROUTE_POINT_NAME);
	}
	
	private Sight createSightWithSingleSightLook() {
		final String WHATEVER_STRING = "whatever";	
		final double WHATEVER_DOUBLE = 12.3456;
		return createSightWithSingleSightLook(WHATEVER_DOUBLE, WHATEVER_DOUBLE, WHATEVER_STRING);
	}
	
	private Sight createSightWithSingleSightLook(double latitude, double longitude, String sightName) {
		final String WHATEVER_STRING = "whatever";	
		
		SightLook expectedSightLook = new SightLook(
				latitude, longitude, WHATEVER_STRING);
		Sight expectedSight = new Sight(_random.nextInt(), sightName, WHATEVER_STRING);
		expectedSight.addLook(expectedSightLook);
		
		return expectedSight;
	}
	
	/*
	private SightPresenter CreateSut(City city, SightView view, AudioPlayer player) {
		
		return CreateSut(city, view, player, 
				mock(ApplicationSettingsStorage.class));
	}
	
	private SightPresenter CreateSut(City city, 
			SightView view, AudioPlayer player, ApplicationSettingsStorage settingsStorage) {
		DownscalableBitmapCreator bmpCreator = mock(DownscalableBitmapCreator.class);
		Logger logger = mock(Logger.class);
		
		SightPresenterDependencyCreator factory = mock(SightPresenterDependencyCreator.class);
		when(factory.createApplicationSettingsStorage()).thenReturn(settingsStorage);
		when(factory.createAssetStreamProvider()).thenReturn(mock(AssetStreamProvider.class));
		
		return new SightPresenter(city, view, player, factory);
	}*/
	
	private SutSetupResult setupSut(SightView sightView, 
			NewSightLookGotInRangeRaiser sightLookFinder,
			ApplicationSettingsStorage settingsStorage) {
		return setupSut(sightView, mock(AudioPlayerView.class), mock(AudioPlayer.class),
				sightLookFinder, settingsStorage);
	}
	
	private SutSetupResult setupSut(SightView sightView, AudioPlayerView playerView, AudioPlayer player, 
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
		
		SightPresenter sut = new SightPresenter(sightView, playerView, player);
		sut.setNewSightLookGotInRangeRaiser(sightLookFinder);
		sut.setAssetStreamProvider(mock(AssetStreamProvider.class));
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
