package com.gerken.audioGuideTests.presenters.sightPresenter;

import static org.mockito.Mockito.*;

import java.util.Random;

import org.junit.Test;

import com.gerken.audioGuide.interfaces.ApplicationSettingsStorage;
import com.gerken.audioGuide.interfaces.AssetStreamProvider;
import com.gerken.audioGuide.interfaces.AudioPlayer;
import com.gerken.audioGuide.interfaces.Logger;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.objectModel.NextRoutePoint;
import com.gerken.audioGuide.objectModel.Sight;
import com.gerken.audioGuide.objectModel.SightLook;
import com.gerken.audioGuide.presenters.SightPresenter;

public class HandleWindowClick {
	private Random random = new Random(System.currentTimeMillis());
	
	@Test
	public void Given_SightIsInRange__Then_PlayerPanelIsDisplayed() throws Exception {
		final double EXPECTED_LOCATION_LATITUDE = 12.345;
		final double EXPECTED_LOCATION_LONGITUDE = 24.567;
		
		City city = CreateSingleSightLookModel(EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE);
		
		SightView view = mock(SightView.class);
		
		SightPresenter sut = CreateSut(city, view);
		sut.handleLocationChange(EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE);
		
		// --- Act
		sut.handleWindowClick();
		
		// --- Assert
		verify(view).showPlayerPanel();
	}
	
	@Test
	public void Given_SightIsInRange_AndNextRoutePointIsShown__Then_SightNameIsShown_AndNextRoutePointIsHidden() throws Exception {
		final String WHATEVER_STRING = "whatever";		
		
		final double EXPECTED_LOCATION_LATITUDE = 12.345;
		final double EXPECTED_LOCATION_LONGITUDE = 24.567;
		int expectedRouteId = random.nextInt();
		short expectedHeadingDeg = (short)random.nextInt(Short.MAX_VALUE);
		final String EXPECTED_SIGHT_NAME = "Colosseum";
		final String EXPECTED_NEXT_ROUTE_POINT_NAME = "Eiffel Tower";
		final boolean ROUTE_IS_CHOSEN = true;
		
		NextRoutePoint expectedRoutePoint = 
				new NextRoutePoint(expectedRouteId, expectedHeadingDeg, EXPECTED_NEXT_ROUTE_POINT_NAME);		
		City city = CreateSingleSightLookModel(
				EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE, EXPECTED_SIGHT_NAME);
		city.getSights().get(0).getSightLooks().get(0).getNextRoutePoints().add(expectedRoutePoint);
		
		SightView view = mock(SightView.class);
		ApplicationSettingsStorage settingsStorage = mock(ApplicationSettingsStorage.class);
		when(settingsStorage.isRouteChosen()).thenReturn(ROUTE_IS_CHOSEN);
		when(settingsStorage.getCurrentRouteId()).thenReturn(expectedRouteId);
		
		SightPresenter sut = CreateSut(city, view, settingsStorage);
		sut.handleLocationChange(EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE);
		sut.handleStopButtonClick();
		
		// --- Act
		sut.handleWindowClick();
		
		// --- Assert
		verify(view).hideNextSightDirection();
		verify(view).setInfoPanelCaptionText(EXPECTED_SIGHT_NAME);
	}
	
	private City CreateSingleSightLookModel(double latitude, double longitude) {
		final String WHATEVER_STRING = "whatever";	
		return CreateSingleSightLookModel(latitude, longitude, WHATEVER_STRING);
	}
	
	private City CreateSingleSightLookModel(double latitude, double longitude, String sightName) {
		final String WHATEVER_STRING = "whatever";	
		
		SightLook expectedSightLook = new SightLook(
				latitude, longitude, WHATEVER_STRING);
		Sight expectedSight = new Sight(random.nextInt(), sightName, WHATEVER_STRING);
		expectedSight.addLook(expectedSightLook);
		City city = new City(random.nextInt(), WHATEVER_STRING, WHATEVER_STRING);
		city.getSights().add(expectedSight);
		
		return city;
	}
	
	private SightPresenter CreateSut(City city, SightView view) {		
		return CreateSut(city, view, 
				mock(AudioPlayer.class), mock(ApplicationSettingsStorage.class));
	}
	
	private SightPresenter CreateSut(City city, SightView view, ApplicationSettingsStorage settingsStorage) {		
		return CreateSut(city, view, mock(AudioPlayer.class), settingsStorage);
	}
	
	private SightPresenter CreateSut(City city, 
			SightView view, AudioPlayer player, ApplicationSettingsStorage settingsStorage) {
		AssetStreamProvider assetStreamProvider = mock(AssetStreamProvider.class);
		Logger logger = mock(Logger.class);
		
		return new SightPresenter(city, view, assetStreamProvider,
				player, settingsStorage, logger);
	}

}