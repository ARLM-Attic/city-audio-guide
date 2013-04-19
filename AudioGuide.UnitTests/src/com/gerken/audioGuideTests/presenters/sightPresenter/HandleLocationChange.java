package com.gerken.audioGuideTests.presenters.sightPresenter;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import com.gerken.audioGuide.interfaces.*;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.objectModel.*;
import com.gerken.audioGuide.presenters.SightPresenter;;

public class HandleLocationChange {

	@Test
	public void Given_NewSightGotInRange__Then_ViewNotifiedAboutNewSight() throws Exception {
		final double EXPECTED_LOCATION_LATITUDE = 12.345;
		final double EXPECTED_LOCATION_LONGITUDE = 24.567;
		final String EXPECTED_SIGHT_LOOK_IMAGE_NAME = "colosseum.jpg";
		final String EXPECTED_SIGHT_NAME = "Colosseum";
		final ByteArrayInputStream EXPECTED_SIGHT_LOOK_IMAGE_STREAM = 
				new ByteArrayInputStream(new byte[]{1,2,3});
		final double WHATEVER_DOUBLE = 111.222;
		final String WHATEVER_STRING = "whatever";		
		
		SightLook expectedSightLook = new SightLook(
				EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE, EXPECTED_SIGHT_LOOK_IMAGE_NAME);
		SightLook unexpectedSightLook = new SightLook(
				WHATEVER_DOUBLE, WHATEVER_DOUBLE, WHATEVER_STRING);
		Sight expectedSight = new Sight(1, EXPECTED_SIGHT_NAME, "audio.mp3");
		expectedSight.addLook(expectedSightLook);
		expectedSight.addLook(unexpectedSightLook);
		Sight unexpectedSight = new Sight(2, "", "audio.mp3");
		City city = new City(1, "Default", WHATEVER_STRING);
		city.getSights().add(expectedSight);
		city.getSights().add(unexpectedSight);	
		
		
		SightView view = mock(SightView.class);
		AssetStreamProvider assetStreamProvider = mock(AssetStreamProvider.class);
		when(assetStreamProvider.getImageAssetStream(EXPECTED_SIGHT_LOOK_IMAGE_NAME))
			.thenReturn(EXPECTED_SIGHT_LOOK_IMAGE_STREAM);
		
		SightPresenter sut = CreateSut(city, view, assetStreamProvider);
		
		// --- Act
		sut.handleLocationChange(EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE);
		
		// --- Assert
		verify(view).acceptNewSightGotInRange(EXPECTED_SIGHT_NAME, EXPECTED_SIGHT_LOOK_IMAGE_STREAM);
	}
	
	@Test
	public void Given_NewSightGotInRange__Then_AudioNotificationPalyed() throws Exception {
		final double EXPECTED_LOCATION_LATITUDE = 12.345;
		final double EXPECTED_LOCATION_LONGITUDE = 24.567;
		final String WHATEVER_STRING = "whatever";		
		
		Sight expectedSight = new Sight(1, WHATEVER_STRING, "audio.mp3");
		expectedSight.addLook(new SightLook(
				EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE, WHATEVER_STRING));
		City city = new City(1, "Default", WHATEVER_STRING);
		city.getSights().add(expectedSight);
		
		SightView view = mock(SightView.class);
		AssetStreamProvider assetStreamProvider = mock(AssetStreamProvider.class);
		AudioPlayer player = mock(AudioPlayer.class);
		when(assetStreamProvider.getImageAssetStream(anyString()))
			.thenReturn(new ByteArrayInputStream(new byte[]{1,2,3}));
		
		SightPresenter sut = CreateSut(city, view, assetStreamProvider, player);
		
		// --- Act
		sut.handleLocationChange(EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE);
		
		// --- Assert
		verify(player).signalSightInRange();
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
		AssetStreamProvider assetStreamProvider = mock(AssetStreamProvider.class);
		when(assetStreamProvider.getImageAssetStream(FIRST_SIGHT_LOOK_IMAGE_NAME))
			.thenReturn(FIRST_SIGHT_LOOK_IMAGE_STREAM);
		when(assetStreamProvider.getImageAssetStream(SECOND_SIGHT_LOOK_IMAGE_NAME))
			.thenReturn(SECOND_SIGHT_LOOK_IMAGE_STREAM);
		
		SightPresenter sut = CreateSut(city, view, assetStreamProvider);
		sut.handleLocationChange(FIRST_SIGHTLOOK_LATITUDE, FIRST_SIGHTLOOK_LONGITUDE);
		
		// --- Act
		sut.handleLocationChange(SECOND_SIGHTLOOK_LATITUDE, SECOND_SIGHTLOOK_LONGITUDE);
		
		// --- Assert
		verify(view).acceptNewSightLookGotInRange(SECOND_SIGHT_LOOK_IMAGE_STREAM);
	}
	
	private SightPresenter CreateSut(City city, SightView view, AssetStreamProvider assetStreamProvider) {
		return CreateSut(city, view, assetStreamProvider, mock(AudioPlayer.class));
	}
	
	private SightPresenter CreateSut(City city, SightView view, AssetStreamProvider assetStreamProvider, AudioPlayer player) {		
		SharedPreferenceStorage prefStorage = mock(SharedPreferenceStorage.class);
		Logger logger = mock(Logger.class);
		
		return new SightPresenter(city, view, assetStreamProvider,
				player, prefStorage, logger);
	}

}
