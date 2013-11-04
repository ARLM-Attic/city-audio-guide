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
	private final float DEFAULT_ACTIVATION_RADIUS = 20;

	@Test
	public void Given_SightLookIsWithinActivationRadius__Then_SightLookSentToListeners() {
		final double SIGHT_LOOK_LATITUDE = 50.08577;
		final double SIGHT_LOOK_LONGITUDE = 14.42304;	
		final double LOCATION_LATITUDE = 50.08572;
		final double LOCATION_LONGITUDE = 14.42305;	
		
		City city = createSingleSightCity();
		SightLook expectedSightLook = addSightLook(city, 
				SIGHT_LOOK_LATITUDE, SIGHT_LOOK_LONGITUDE);
		
		OnSightLookGotInRangeListener sightLookInRangeListener = mock(OnSightLookGotInRangeListener.class);
		SutSetupResult setupResult = setupSut(city, sightLookInRangeListener);
		setupResult.sut.setSightActivationRadius(DEFAULT_ACTIVATION_RADIUS);
		
		// --- Act
		setupResult.locationChangedListener.onLocationChanged(LOCATION_LATITUDE, LOCATION_LONGITUDE);
		
		// --- Assert
		verify(sightLookInRangeListener).onSightLookGotInRange(expectedSightLook);
	}
	
	@Test
	public void Given_MultipleSightLooksInRangeFound__Then_TheClosestSightLookSentToListeners() {
		final double EXPECTED_SIGHT_LOOK_LATITUDE = 50.08577;
		final double EXPECTED_SIGHT_LOOK_LONGITUDE = 14.42304;	
		final double UNEXPECTED_SIGHT_LOOK_LATITUDE = 50.08584;
		final double UNEXPECTED_SIGHT_LOOK_LONGITUDE = 14.42298;	
		final double LOCATION_LATITUDE = 50.08572;
		final double LOCATION_LONGITUDE = 14.42305;	
		
		City city = createSingleSightCity();
		SightLook expectedSightLook = addSightLook(city, 
				EXPECTED_SIGHT_LOOK_LATITUDE, EXPECTED_SIGHT_LOOK_LONGITUDE);
		SightLook unexpectedSightLook = addSightLook(city, 
				UNEXPECTED_SIGHT_LOOK_LATITUDE, UNEXPECTED_SIGHT_LOOK_LONGITUDE);
		
		OnSightLookGotInRangeListener sightLookInRangeListener = mock(OnSightLookGotInRangeListener.class);
		SutSetupResult setupResult = setupSut(city, sightLookInRangeListener);
		setupResult.sut.setSightActivationRadius(DEFAULT_ACTIVATION_RADIUS);
		
		// --- Act
		setupResult.locationChangedListener.onLocationChanged(LOCATION_LATITUDE, LOCATION_LONGITUDE);
		
		// --- Assert
		verify(sightLookInRangeListener).onSightLookGotInRange(expectedSightLook);
		verify(sightLookInRangeListener, never()).onSightLookGotInRange(unexpectedSightLook);
	}
	
	@Test
	public void Given_NoSightLookWithinActivationRadius__Then_NullSightLookSentToListeners() {
		final double SIGHT_LOOK_LATITUDE = 50.08577;
		final double SIGHT_LOOK_LONGITUDE = 14.42304;	
		final double LOCATION_LATITUDE = 50.08618;
		final double LOCATION_LONGITUDE = 14.42263;	
		
		City city = createSingleSightCity();
		SightLook sightLookOutsideRange = addSightLook(city, 
				SIGHT_LOOK_LATITUDE, SIGHT_LOOK_LONGITUDE);
		
		OnSightLookGotInRangeListener sightLookInRangeListener = mock(OnSightLookGotInRangeListener.class);
		SutSetupResult setupResult = setupSut(city, sightLookInRangeListener);
		setupResult.sut.setSightActivationRadius(DEFAULT_ACTIVATION_RADIUS);
		
		// --- Act
		setupResult.locationChangedListener.onLocationChanged(LOCATION_LATITUDE, LOCATION_LONGITUDE);
		
		// --- Assert
		verify(sightLookInRangeListener).onSightLookGotInRange(null);
		verify(sightLookInRangeListener, never()).onSightLookGotInRange(sightLookOutsideRange);
	}
	
	private City createSingleSightCity() {
		final String WHATEVER_STRING = createRandomString();
		
		Sight expectedSight = new Sight(_random.nextInt(), WHATEVER_STRING, WHATEVER_STRING);
		City city = new City(_random.nextInt(), WHATEVER_STRING, WHATEVER_STRING);
		city.getSights().add(expectedSight);
		
		return city;
	}
	
	private SightLook addSightLook(City city, double latitude, double longitude) {
		final String WHATEVER_STRING = createRandomString();	
		
		Sight expectedSight = city.getSights().get(0);
		SightLook sightLook = new SightLook(
				latitude, longitude, WHATEVER_STRING);
		expectedSight.addLook(sightLook);
		
		return sightLook;
	}
	
	private String createRandomString() {
		return String.valueOf(_random.nextLong());
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
