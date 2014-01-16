package com.gerken.audioGuideTests.services.sightLookFinderByLocation;

import static org.mockito.Mockito.*;

import java.util.Random;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.interfaces.LocationTracker;
import com.gerken.audioGuide.interfaces.listeners.OnLocationChangedListener;
import com.gerken.audioGuide.interfaces.listeners.OnSightLookGotInRangeListener;
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
	public void Given_NoSightLookWithinActivationRadius_AndPreviosListenerNotificationWasNull__Then_NoNewNotificationSentToListeners() {
		final int SIGHT_LOOK_MISSING_COUNT_THRESHOLD = 2;
		final double SIGHT_LOOK_LATITUDE = 50.08577;
		final double SIGHT_LOOK_LONGITUDE = 14.42304;	
		final double EMPTY1_LOCATION_LATITUDE = 50.087;
		final double EMPTY1_LOCATION_LONGITUDE = 14.424;
		final double EMPTY2_LOCATION_LATITUDE = 50.088;
		final double EMPTY2_LOCATION_LONGITUDE = 14.425;
		final double EMPTY3_LOCATION_LATITUDE = 50.089;
		final double EMPTY3_LOCATION_LONGITUDE = 14.426;
		final double EMPTY4_LOCATION_LATITUDE = 50.09;
		final double EMPTY4_LOCATION_LONGITUDE = 14.427;
		
		City city = createSingleSightCity();
		addSightLook(city, 	SIGHT_LOOK_LATITUDE, SIGHT_LOOK_LONGITUDE);
		
		OnSightLookGotInRangeListener sightLookInRangeListener = mock(OnSightLookGotInRangeListener.class);
		SutSetupResult setupResult = setupSut(city, sightLookInRangeListener);
		setupResult.sut.setSightActivationRadius(DEFAULT_ACTIVATION_RADIUS);
		setupResult.sut.setSightLookMissingCountThreshold(SIGHT_LOOK_MISSING_COUNT_THRESHOLD);
		
		setupResult.locationChangedListener.onLocationChanged(SIGHT_LOOK_LATITUDE, SIGHT_LOOK_LONGITUDE);
		setupResult.locationChangedListener.onLocationChanged(EMPTY1_LOCATION_LATITUDE, EMPTY1_LOCATION_LONGITUDE);
		setupResult.locationChangedListener.onLocationChanged(EMPTY2_LOCATION_LATITUDE, EMPTY2_LOCATION_LONGITUDE);
		
		// --- Act
		setupResult.locationChangedListener.onLocationChanged(EMPTY3_LOCATION_LATITUDE, EMPTY3_LOCATION_LONGITUDE);
		setupResult.locationChangedListener.onLocationChanged(EMPTY4_LOCATION_LATITUDE, EMPTY4_LOCATION_LONGITUDE);
		
		// --- Assert
		verify(sightLookInRangeListener, times(1)).onSightLookGotInRange(null);
	}	
	
	@Test
	public void Given_NoSightLookFoundWithinActivationRadiusAfterFindingAOne_MissingThresholdReached__Then_NullSightLookSentToListenersOnce() {
		final int SIGHT_LOOK_MISSING_COUNT_THRESHOLD = 3;
		final double SIGHT_LOOK_LATITUDE = 50.08577;
		final double SIGHT_LOOK_LONGITUDE = 14.42304;	
		final double EMPTY1_LOCATION_LATITUDE = 50.087;
		final double EMPTY1_LOCATION_LONGITUDE = 14.424;
		final double EMPTY2_LOCATION_LATITUDE = 50.088;
		final double EMPTY2_LOCATION_LONGITUDE = 14.425;
		final double EMPTY3_LOCATION_LATITUDE = 50.089;
		final double EMPTY3_LOCATION_LONGITUDE = 14.426;
		
		City city = createSingleSightCity();
		SightLook foundSightLook = addSightLook(city, SIGHT_LOOK_LATITUDE, SIGHT_LOOK_LONGITUDE);
		
		OnSightLookGotInRangeListener sightLookInRangeListener = mock(OnSightLookGotInRangeListener.class);
		SutSetupResult setupResult = setupSut(city, sightLookInRangeListener);
		setupResult.sut.setSightActivationRadius(DEFAULT_ACTIVATION_RADIUS);
		setupResult.sut.setSightLookMissingCountThreshold(SIGHT_LOOK_MISSING_COUNT_THRESHOLD);
		
		// --- Act
		setupResult.locationChangedListener.onLocationChanged(SIGHT_LOOK_LATITUDE, SIGHT_LOOK_LONGITUDE);
		setupResult.locationChangedListener.onLocationChanged(EMPTY1_LOCATION_LATITUDE, EMPTY1_LOCATION_LONGITUDE);
		setupResult.locationChangedListener.onLocationChanged(EMPTY2_LOCATION_LATITUDE, EMPTY2_LOCATION_LONGITUDE);
		setupResult.locationChangedListener.onLocationChanged(EMPTY3_LOCATION_LATITUDE, EMPTY3_LOCATION_LONGITUDE);
		
		// --- Assert
		verify(sightLookInRangeListener, times(1)).onSightLookGotInRange(foundSightLook);
		verify(sightLookInRangeListener, times(1)).onSightLookGotInRange(null);
	}
	
	@Test
	public void Given_NoSightLookFoundWithinActivationRadiusAfterFindingAOne_MissingThresholdNotReached__Then_NullSightLookNotSentToListeners() {
		final int SIGHT_LOOK_MISSING_COUNT_THRESHOLD = 3;
		final double SIGHT_LOOK_LATITUDE = 50.08577;
		final double SIGHT_LOOK_LONGITUDE = 14.42304;	
		final double EMPTY1_LOCATION_LATITUDE = 50.087;
		final double EMPTY1_LOCATION_LONGITUDE = 14.424;
		final double EMPTY2_LOCATION_LATITUDE = 50.088;
		final double EMPTY2_LOCATION_LONGITUDE = 14.425;
		
		City city = createSingleSightCity();
		SightLook foundSightLook = addSightLook(city, SIGHT_LOOK_LATITUDE, SIGHT_LOOK_LONGITUDE);
		
		OnSightLookGotInRangeListener sightLookInRangeListener = mock(OnSightLookGotInRangeListener.class);
		SutSetupResult setupResult = setupSut(city, sightLookInRangeListener);
		setupResult.sut.setSightActivationRadius(DEFAULT_ACTIVATION_RADIUS);
		setupResult.sut.setSightLookMissingCountThreshold(SIGHT_LOOK_MISSING_COUNT_THRESHOLD);
		
		// --- Act
		setupResult.locationChangedListener.onLocationChanged(SIGHT_LOOK_LATITUDE, SIGHT_LOOK_LONGITUDE);
		setupResult.locationChangedListener.onLocationChanged(EMPTY1_LOCATION_LATITUDE, EMPTY1_LOCATION_LONGITUDE);
		setupResult.locationChangedListener.onLocationChanged(EMPTY2_LOCATION_LATITUDE, EMPTY2_LOCATION_LONGITUDE);
		
		// --- Assert
		verify(sightLookInRangeListener, times(1)).onSightLookGotInRange(foundSightLook);
		verify(sightLookInRangeListener, never()).onSightLookGotInRange(null);
	}
	
	@Test
	public void Given_RandomEmptyLocationHappenedAroundSightLook_MissingThresholdNotReached__Then_NullSightLookNotSentToListeners() {
		final float ACTIVATION_RADIUS = 40;
		final int SIGHT_LOOK_MISSING_COUNT_THRESHOLD = 3;
		final double SIGHT_LOOK_LATITUDE = 50.08577;
		final double SIGHT_LOOK_LONGITUDE = 14.42304;	
		
		final double IN_RANGE1_LOCATION_LATITUDE = 50.08592;
		final double IN_RANGE1_LOCATION_LONGITUDE = 14.42317;
		final double IN_RANGE2_LOCATION_LATITUDE = 50.08571;
		final double IN_RANGE2_LOCATION_LONGITUDE = 14.42309;
		
		final double EMPTY1_LOCATION_LATITUDE = 50.08517;
		final double EMPTY1_LOCATION_LONGITUDE = 14.42459;
		
		City city = createSingleSightCity();
		SightLook foundSightLook = addSightLook(city, SIGHT_LOOK_LATITUDE, SIGHT_LOOK_LONGITUDE);
		
		OnSightLookGotInRangeListener sightLookInRangeListener = mock(OnSightLookGotInRangeListener.class);
		SutSetupResult setupResult = setupSut(city, sightLookInRangeListener);
		setupResult.sut.setSightActivationRadius(ACTIVATION_RADIUS);
		setupResult.sut.setSightLookMissingCountThreshold(SIGHT_LOOK_MISSING_COUNT_THRESHOLD);
		
		// --- Act
		setupResult.locationChangedListener.onLocationChanged(IN_RANGE1_LOCATION_LATITUDE, IN_RANGE1_LOCATION_LONGITUDE);
		setupResult.locationChangedListener.onLocationChanged(EMPTY1_LOCATION_LATITUDE, EMPTY1_LOCATION_LONGITUDE);
		setupResult.locationChangedListener.onLocationChanged(IN_RANGE2_LOCATION_LATITUDE, IN_RANGE2_LOCATION_LONGITUDE);
		
		// --- Assert
		verify(sightLookInRangeListener, times(2)).onSightLookGotInRange(foundSightLook);
		verify(sightLookInRangeListener, never()).onSightLookGotInRange(null);
	}
	
	@Test
	public void Given_EmptyLocationAfterSecondSightLook_MissingThresholdNotReached__Then_NullSightLookNotSentToListenersAfterThat() {
		final float ACTIVATION_RADIUS = 40;
		final int SIGHT_LOOK_MISSING_COUNT_THRESHOLD = 2;
		final double SIGHT_LOOK_LATITUDE = 50.08577;
		final double SIGHT_LOOK_LONGITUDE = 14.42304;	
		
		final double IN_RANGE1_LOCATION_LATITUDE = 50.08592;
		final double IN_RANGE1_LOCATION_LONGITUDE = 14.42317;
		
		final double EMPTY1_LOCATION_LATITUDE = 50.08541;
		final double EMPTY1_LOCATION_LONGITUDE = 14.42424;
		final double EMPTY2_LOCATION_LATITUDE = 50.08517;
		final double EMPTY2_LOCATION_LONGITUDE = 14.42459;
		
		City city = createSingleSightCity();
		addSightLook(city, SIGHT_LOOK_LATITUDE, SIGHT_LOOK_LONGITUDE);
		
		OnSightLookGotInRangeListener sightLookInRangeListener = mock(OnSightLookGotInRangeListener.class);
		SutSetupResult setupResult = setupSut(city, sightLookInRangeListener);
		setupResult.sut.setSightActivationRadius(ACTIVATION_RADIUS);
		setupResult.sut.setSightLookMissingCountThreshold(SIGHT_LOOK_MISSING_COUNT_THRESHOLD);
		
		setupResult.locationChangedListener.onLocationChanged(IN_RANGE1_LOCATION_LATITUDE, IN_RANGE1_LOCATION_LONGITUDE);
		setupResult.locationChangedListener.onLocationChanged(EMPTY1_LOCATION_LATITUDE, EMPTY1_LOCATION_LONGITUDE);
		setupResult.locationChangedListener.onLocationChanged(EMPTY2_LOCATION_LATITUDE, EMPTY2_LOCATION_LONGITUDE);
		setupResult.locationChangedListener.onLocationChanged(IN_RANGE1_LOCATION_LATITUDE, IN_RANGE1_LOCATION_LONGITUDE);
		
		// --- Act
		setupResult.locationChangedListener.onLocationChanged(EMPTY1_LOCATION_LATITUDE, EMPTY1_LOCATION_LONGITUDE);
		
		// --- Assert
		verify(sightLookInRangeListener, times(1)).onSightLookGotInRange(null);
	}
	
	@Test
	public void Given_ManyEmptyLocationsProcessed_AndOneLocationInRangeInTheMiddle__Then_NullSightLookSentToListenersOnce() {
		final float ACTIVATION_RADIUS = 40;
		final int SIGHT_LOOK_MISSING_COUNT_THRESHOLD = 3;
		final int EMPTY_LOCATION_HALF_COUNT = 50;
		
		final double SIGHT_LOOK_LATITUDE = 50.08577;
		final double SIGHT_LOOK_LONGITUDE = 14.42304;	
		
		final double IN_RANGE_LOCATION_LATITUDE = 50.08592;
		final double IN_RANGE_LOCATION_LONGITUDE = 14.42317;
		
		final double EMPTY_LOCATION_BASE_LATITUDE = 51;
		final double EMPTY_LOCATION_BASE_LONGITUDE = 14;
		final double EMPTY_LOCATION_RADIUS = 0.5;
		
		City city = createSingleSightCity();
		SightLook foundSightLook = addSightLook(city, SIGHT_LOOK_LATITUDE, SIGHT_LOOK_LONGITUDE);
		
		OnSightLookGotInRangeListener sightLookInRangeListener = mock(OnSightLookGotInRangeListener.class);
		SutSetupResult setupResult = setupSut(city, sightLookInRangeListener);
		setupResult.sut.setSightActivationRadius(ACTIVATION_RADIUS);
		setupResult.sut.setSightLookMissingCountThreshold(SIGHT_LOOK_MISSING_COUNT_THRESHOLD);
		
		// --- Act
		strollInEmptyArea(setupResult.locationChangedListener, EMPTY_LOCATION_HALF_COUNT,
				EMPTY_LOCATION_BASE_LATITUDE, EMPTY_LOCATION_BASE_LONGITUDE, EMPTY_LOCATION_RADIUS);
		setupResult.locationChangedListener.onLocationChanged(IN_RANGE_LOCATION_LATITUDE, IN_RANGE_LOCATION_LONGITUDE);
		strollInEmptyArea(setupResult.locationChangedListener, EMPTY_LOCATION_HALF_COUNT,
				EMPTY_LOCATION_BASE_LATITUDE, EMPTY_LOCATION_BASE_LONGITUDE, EMPTY_LOCATION_RADIUS);
		
		// --- Assert
		verify(sightLookInRangeListener).onSightLookGotInRange(foundSightLook);
		verify(sightLookInRangeListener, times(1)).onSightLookGotInRange(null);
	}
	
	private void strollInEmptyArea(OnLocationChangedListener listener,
			int times, double baseLatitude, double baseLongitude, double radius) {
		for(int i=0; i<times; i++)
			listener.onLocationChanged(
					baseLatitude + _random.nextDouble()*radius, 
					baseLongitude + _random.nextDouble()*radius);
	}
	
	private City createSingleSightCity() {
		Sight expectedSight = new Sight(_random.nextInt(), createRandomString(), createRandomString());
		City city = new City(_random.nextInt(), createRandomString());
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
