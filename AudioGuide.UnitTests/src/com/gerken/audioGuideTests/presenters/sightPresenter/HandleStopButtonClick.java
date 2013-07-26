package com.gerken.audioGuideTests.presenters.sightPresenter;

import static org.mockito.Mockito.*;

import java.util.Random;
import java.util.UUID;

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

		final boolean ROUTE_IS_NOT_CHOSEN = false;
		
		NextRoutePoint expectedRoutePoint = createNextRoutePoint();		
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
		final boolean ROUTE_IS_CHOSEN = true;
		
		NextRoutePoint expectedRoutePoint = createNextRoutePoint(expectedRouteId);		
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
		float expectedHeadingRad = (float)(Math.PI*expectedRoutePoint.getHeading()/180.0);
		verify(sightView).displayNextSightDirection(eq(expectedHeadingRad), anyFloat());
		verify(sightView).setInfoPanelCaptionText(expectedRoutePoint.getName());
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
	
	private NextRoutePoint createNextRoutePoint() {
		return createNextRoutePoint(_random.nextInt());
	}
	
	private NextRoutePoint createNextRoutePoint(int routeId) {
		short heading = (short)_random.nextInt(Short.MAX_VALUE);
		byte horizon = (byte)_random.nextInt(Byte.MAX_VALUE);
		String name = UUID.randomUUID().toString();
		
		return new NextRoutePoint(routeId, heading, horizon, name);
	}
	
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
		
		SightPresenter sut = new SightPresenter(sightView, playerView);
		sut.setAudioPlayer(player);
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
