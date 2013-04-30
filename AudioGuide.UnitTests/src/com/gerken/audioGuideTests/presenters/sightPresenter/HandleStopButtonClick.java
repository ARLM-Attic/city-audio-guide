package com.gerken.audioGuideTests.presenters.sightPresenter;

import static org.mockito.Mockito.*;

import java.util.Random;

import org.junit.Test;

import com.gerken.audioGuide.interfaces.*;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.objectModel.*;
import com.gerken.audioGuide.presenters.SightPresenter;;

public class HandleStopButtonClick {
	private Random _random = new Random(System.currentTimeMillis());
	
	@Test
	public void Given_PlayerHasBeenPlaying__Then_PlayerGetsStopped() throws Exception {
		final String WHATEVER_STRING = "whatever";		
		
		City city = new City(1, "Default", WHATEVER_STRING);
		SightView view = mock(SightView.class);
		AudioPlayer player = mock(AudioPlayer.class);
		when(player.isPlaying()).thenReturn(true);
		
		SightPresenter sut = CreateSut(city, view, player);
		
		// --- Act
		sut.handleStopButtonClick();
		
		// --- Assert
		verify(player).stop();
	}
	
	@Test
	public void Given_RouteHasBeenChosen__Then_NextRoutePointNameAndDirectionShown() throws Exception {
		final double EXPECTED_LOCATION_LATITUDE = 12.345;
		final double EXPECTED_LOCATION_LONGITUDE = 24.567;
		final String WHATEVER_STRING = "whatever";
		int expectedRouteId = _random.nextInt();
		short expectedHeadingDeg = (short)_random.nextInt(Short.MAX_VALUE);
		byte expectedHorizonPerc = (byte)_random.nextInt(Byte.MAX_VALUE);
		final String EXPECTED_NEXT_ROUTE_POINT_NAME = "Eiffel Tower";
		final boolean ROUTE_IS_CHOSEN = true;
		
		NextRoutePoint expectedRoutePoint = new NextRoutePoint(expectedRouteId, 
				expectedHeadingDeg, expectedHorizonPerc, EXPECTED_NEXT_ROUTE_POINT_NAME);		
		SightLook expectedSightLook = new SightLook(
				EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE, WHATEVER_STRING);
		expectedSightLook.getNextRoutePoints().add(expectedRoutePoint);
		Sight expectedSight = new Sight(_random.nextInt(), WHATEVER_STRING, WHATEVER_STRING);
		expectedSight.addLook(expectedSightLook);
		City city = new City(_random.nextInt(), WHATEVER_STRING, WHATEVER_STRING);
		city.getSights().add(expectedSight);
		
		SightView view = mock(SightView.class);
		AudioPlayer player = mock(AudioPlayer.class);
		when(player.isPlaying()).thenReturn(true);
		ApplicationSettingsStorage prefStorage = mock(ApplicationSettingsStorage.class);
		when(prefStorage.isRouteChosen()).thenReturn(ROUTE_IS_CHOSEN);
		when(prefStorage.getCurrentRouteId()).thenReturn(expectedRouteId);
		
		SightPresenter sut = CreateSut(city, view, player, prefStorage);
		sut.handleLocationChange(EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE);
		
		// --- Act
		sut.handleStopButtonClick();
		
		// --- Assert
		float expectedHeadingRad = (float)(Math.PI*expectedHeadingDeg/180.0);
		verify(view).displayNextSightDirection(eq(expectedHeadingRad), anyFloat());
		verify(view).setInfoPanelCaptionText(EXPECTED_NEXT_ROUTE_POINT_NAME);
	}

	private SightPresenter CreateSut(City city, SightView view, AudioPlayer player) {
		
		return CreateSut(city, view, player, 
				mock(ApplicationSettingsStorage.class));
	}
	
	private SightPresenter CreateSut(City city, 
			SightView view, AudioPlayer player, ApplicationSettingsStorage prefStorage) {
		AssetStreamProvider assetStreamProvider = mock(AssetStreamProvider.class);
		DownscalableBitmapCreator bmpCreator = mock(DownscalableBitmapCreator.class);
		Logger logger = mock(Logger.class);
		
		return new SightPresenter(city, view, assetStreamProvider,
				player, prefStorage, bmpCreator, logger);
	}
}
