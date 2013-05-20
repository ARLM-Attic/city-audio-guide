package com.gerken.audioGuideTests.services.sightLookFinderByLocation;

import static org.mockito.Mockito.*;

import java.util.Random;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.interfaces.LocationTracker;
import com.gerken.audioGuide.interfaces.OnLocationChangedListener;
import com.gerken.audioGuide.interfaces.OnSightLookGotInRangeListener;
import com.gerken.audioGuide.objectModel.*;
import com.gerken.audioGuide.services.SightLookFinderByLocation;

public class HandleLocationChangedEvent {
	private Random _random = new Random(System.currentTimeMillis());

	@Test
	public void Given_SightLookinRangeFound__Then_SightLookSentToListeners() {
		final double EXPECTED_LOCATION_LATITUDE = 12.345;
		final double EXPECTED_LOCATION_LONGITUDE = 24.567;	
		
		final double WHATEVER_DOUBLE = 111.222;
		City city = createSingleSightCity();
		SightLook expectedSightLook = addSightLook(city, 
				EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE);
		SightLook unexpectedSightLook = addSightLook(city, 
				WHATEVER_DOUBLE, WHATEVER_DOUBLE);
		
		OnSightLookGotInRangeListener sightLookInRangeListener = mock(OnSightLookGotInRangeListener.class);
		SutSetupResult setupResult = setupSut(city, sightLookInRangeListener);
		
		// --- Act
		setupResult.locationChangedListener.onLocationChanged(
				EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE);
		
		// --- Assert
		verify(sightLookInRangeListener).onSightLookGotInRange(expectedSightLook);
		verify(sightLookInRangeListener, never()).onSightLookGotInRange(unexpectedSightLook);
	}
	
	@Test
	public void Given_SightLookinRangeNotFound__Then_NullSentToListeners() {
		final double sightLatitude = 80.0*_random.nextDouble();
		final double sightLongitude = 170.0*_random.nextDouble();
		final double emptyLatitude = sightLatitude + 5.0;
		final double emptyLongitude = sightLongitude + 5.0;
		
		City city = createSingleSightCity();
		addSightLook(city,	sightLatitude, sightLongitude);
		OnSightLookGotInRangeListener sightLookInRangeListener = mock(OnSightLookGotInRangeListener.class);
		
		SutSetupResult setupResult = setupSut(city, sightLookInRangeListener);
		
		// --- Act
		setupResult.locationChangedListener.onLocationChanged(emptyLatitude, emptyLongitude);
		
		// --- Assert
		verify(sightLookInRangeListener).onSightLookGotInRange(null);
	}
	
	private City createSingleSightCity() {
		final String EXPECTED_SIGHT_NAME = "Colosseum";
		final String WHATEVER_STRING = "whatever";	
		
		Sight expectedSight = new Sight(1, EXPECTED_SIGHT_NAME, WHATEVER_STRING);
		City city = new City(1, "Default", WHATEVER_STRING);
		city.getSights().add(expectedSight);
		
		return city;
	}
	
	private SightLook addSightLook(City city, double latitude, double longitude) {
		final String WHATEVER_STRING = "whatever";	
		
		Sight expectedSight = city.getSights().get(0);
		SightLook sightLook = new SightLook(
				latitude, longitude, WHATEVER_STRING);
		expectedSight.addLook(sightLook);
		
		return sightLook;
	}
	
	private SutSetupResult setupSut(City city, OnSightLookGotInRangeListener sightLookInRangeListener) {
		SutSetupResult result = new SutSetupResult();
		LocationTracker locationTracker = mock(LocationTracker.class);
		ArgumentCaptor<OnLocationChangedListener> locationChangesListenerCaptor = 
				ArgumentCaptor.forClass(OnLocationChangedListener.class);
		doNothing().when(locationTracker).addLocationChangedListener(locationChangesListenerCaptor.capture());
		
		SightLookFinderByLocation sut = new SightLookFinderByLocation(city, locationTracker);
		sut.addSightLookGotInRangeListener(sightLookInRangeListener);
		
		result.sut = sut;
		result.locationChangedListener = locationChangesListenerCaptor.getValue();
		return result;
	}
	
	private class SutSetupResult {
		public SightLookFinderByLocation sut;
		public OnLocationChangedListener locationChangedListener;
	}
}
