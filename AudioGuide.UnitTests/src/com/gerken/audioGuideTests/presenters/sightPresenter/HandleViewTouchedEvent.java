package com.gerken.audioGuideTests.presenters.sightPresenter;

import static org.mockito.Mockito.*;

import java.util.Random;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.gerken.audioGuide.interfaces.ApplicationSettingsStorage;
import com.gerken.audioGuide.interfaces.AssetStreamProvider;
import com.gerken.audioGuide.interfaces.AudioPlayer;
import com.gerken.audioGuide.interfaces.NewSightLookGotInRangeRaiser;
import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.OnSightLookGotInRangeListener;
import com.gerken.audioGuide.interfaces.views.AudioPlayerView;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.objectModel.NextRoutePoint;
import com.gerken.audioGuide.objectModel.Sight;
import com.gerken.audioGuide.objectModel.SightLook;
import com.gerken.audioGuide.presenters.SightPresenter;

public class HandleViewTouchedEvent {
	
	private Random _random = new Random(System.currentTimeMillis());
	
	
	private class ArgumentRetrievingAnswer<TArg> implements Answer {		
		private TArg _arg;

		@Override
		public Object answer(InvocationOnMock invocation) {
			Object[] args = invocation.getArguments();
			_arg = (TArg)args[0];
			return null;
		}
		
		public TArg getArg() {
			return _arg;
		}
		
	}

	@Test
	public void Given_NoSightInRange__Then_PlayerPanelIsNotDisplayed() throws Exception {
		
		SightView sightView = mock(SightView.class);
		ArgumentRetrievingAnswer<OnEventListener> sightViewTouchedListenerInterceptor = 
				new ArgumentRetrievingAnswer<OnEventListener>();
		doAnswer(sightViewTouchedListenerInterceptor)
			.when(sightView).addViewTouchedListener(any(OnEventListener.class));
		
		
		NewSightLookGotInRangeRaiser sightLookFinder = mock(NewSightLookGotInRangeRaiser.class);
		ArgumentRetrievingAnswer<OnSightLookGotInRangeListener> sightLookGotInRangeListenerInterceptor = 
				new ArgumentRetrievingAnswer<OnSightLookGotInRangeListener>();
		doAnswer(sightLookGotInRangeListenerInterceptor)
			.when(sightLookFinder).addSightLookGotInRangeListener(any(OnSightLookGotInRangeListener.class));
		

		
		SightPresenter sut = new SightPresenter(sightView, 
				mock(AudioPlayerView.class), mock(AudioPlayer.class));
		sut.setNewSightLookGotInRangeRaiser(sightLookFinder);
		
		OnEventListener sightViewTouchListener = sightViewTouchedListenerInterceptor.getArg();
		OnSightLookGotInRangeListener sightLookGotInRangeListener = 
				sightLookGotInRangeListenerInterceptor.getArg();
		
		sightLookGotInRangeListener.onSightLookGotInRange(null);
		
		// --- Act
		sightViewTouchListener.onEvent();
		
		// --- Assert
		verify(sightView, never()).showPlayerPanel();
	}
	
	@Test
	public void Given_SightIsInRange__Then_PlayerPanelIsDisplayed() throws Exception {
		final double EXPECTED_LOCATION_LATITUDE = 12.345;
		final double EXPECTED_LOCATION_LONGITUDE = 24.567;
		
		City city = CreateSingleSightLookModel(EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE);
		
		SightView sightView = mock(SightView.class);
		ArgumentRetrievingAnswer<OnEventListener> sightViewTouchedListenerInterceptor = 
				new ArgumentRetrievingAnswer<OnEventListener>();
		doAnswer(sightViewTouchedListenerInterceptor)
			.when(sightView).addViewTouchedListener(any(OnEventListener.class));
		
		NewSightLookGotInRangeRaiser sightLookFinder = mock(NewSightLookGotInRangeRaiser.class);
		ArgumentRetrievingAnswer<OnSightLookGotInRangeListener> sightLookGotInRangeListenerInterceptor = 
				new ArgumentRetrievingAnswer<OnSightLookGotInRangeListener>();
		doAnswer(sightLookGotInRangeListenerInterceptor)
			.when(sightLookFinder).addSightLookGotInRangeListener(any(OnSightLookGotInRangeListener.class));
		
		SightPresenter sut = new SightPresenter(sightView, 
				mock(AudioPlayerView.class), mock(AudioPlayer.class));
		sut.setNewSightLookGotInRangeRaiser(sightLookFinder);
		sut.setAssetStreamProvider(mock(AssetStreamProvider.class));
		
		OnEventListener sightViewTouchListener = sightViewTouchedListenerInterceptor.getArg();
		OnSightLookGotInRangeListener sightLookGotInRangeListener = 
				sightLookGotInRangeListenerInterceptor.getArg();
		
		sightLookGotInRangeListener.onSightLookGotInRange(city.getSights().get(0).getSightLooks().get(0));
		
		// --- Act
		sightViewTouchListener.onEvent();
		
		// --- Assert
		verify(sightView).showPlayerPanel();
	}

	@Test
	public void Given_SightIsInRange_AndNextRoutePointIsShown__Then_SightNameIsShown_AndNextRoutePointIsHidden() throws Exception {

		final double EXPECTED_LOCATION_LATITUDE = 12.345;
		final double EXPECTED_LOCATION_LONGITUDE = 24.567;
		int expectedRouteId = _random.nextInt();
		short expectedHeadingDeg = (short)_random.nextInt(Short.MAX_VALUE);
		byte expectedHorizonPerc = (byte)_random.nextInt(Byte.MAX_VALUE);
		final String EXPECTED_SIGHT_NAME = "Colosseum";
		final String EXPECTED_NEXT_ROUTE_POINT_NAME = "Eiffel Tower";		
		
		NextRoutePoint expectedRoutePoint = new NextRoutePoint(expectedRouteId, 
				expectedHeadingDeg, expectedHorizonPerc, EXPECTED_NEXT_ROUTE_POINT_NAME);		
		City city = CreateSingleSightLookModel(
				EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE, EXPECTED_SIGHT_NAME);
		city.getSights().get(0).getSightLooks().get(0).getNextRoutePoints().add(expectedRoutePoint);
		
		SightView sightView = mock(SightView.class);
		ArgumentRetrievingAnswer<OnEventListener> sightViewTouchedListenerInterceptor = 
				new ArgumentRetrievingAnswer<OnEventListener>();
		doAnswer(sightViewTouchedListenerInterceptor)
			.when(sightView).addViewTouchedListener(any(OnEventListener.class));
		
		AudioPlayerView playerView = mock(AudioPlayerView.class);
		ArgumentRetrievingAnswer<OnEventListener> stopButtonPressedListenerInterceptor = 
				new ArgumentRetrievingAnswer<OnEventListener>();
		doAnswer(stopButtonPressedListenerInterceptor)
			.when(playerView).addStopPressedListener(any(OnEventListener.class));
		
		NewSightLookGotInRangeRaiser sightLookFinder = mock(NewSightLookGotInRangeRaiser.class);
		ArgumentRetrievingAnswer<OnSightLookGotInRangeListener> sightLookGotInRangeListenerInterceptor = 
				new ArgumentRetrievingAnswer<OnSightLookGotInRangeListener>();
		doAnswer(sightLookGotInRangeListenerInterceptor)
			.when(sightLookFinder).addSightLookGotInRangeListener(any(OnSightLookGotInRangeListener.class));

		ApplicationSettingsStorage settingsStorage = 
				CreateApplicationSettingsStorageWithRouteChosen(expectedRouteId);
		
		// --- SUT
		SightPresenter sut = new SightPresenter(sightView, playerView, mock(AudioPlayer.class));
		sut.setNewSightLookGotInRangeRaiser(sightLookFinder);
		sut.setAssetStreamProvider(mock(AssetStreamProvider.class));
		sut.setApplicationSettingsStorage(settingsStorage);
		
		OnEventListener sightViewTouchListener = sightViewTouchedListenerInterceptor.getArg();
		OnEventListener stopButtonPressedListener = stopButtonPressedListenerInterceptor.getArg();
		OnSightLookGotInRangeListener sightLookGotInRangeListener = 
				sightLookGotInRangeListenerInterceptor.getArg();
		
		sightLookGotInRangeListener.onSightLookGotInRange(city.getSights().get(0).getSightLooks().get(0));
		stopButtonPressedListener.onEvent();

		// --- Act
		reset(sightView);
		sightViewTouchListener.onEvent();
		
		// --- Assert
		verify(sightView).hideNextSightDirection();
		verify(sightView).setInfoPanelCaptionText(EXPECTED_SIGHT_NAME);
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
	
	private ApplicationSettingsStorage CreateApplicationSettingsStorageWithRouteChosen(int routeId) {
		final boolean ROUTE_IS_CHOSEN = true;
		ApplicationSettingsStorage settingsStorage = mock(ApplicationSettingsStorage.class);
		when(settingsStorage.isRouteChosen()).thenReturn(ROUTE_IS_CHOSEN);
		when(settingsStorage.getCurrentRouteId()).thenReturn(routeId);
		return settingsStorage;
	}
}
