package com.gerken.audioGuideTests.presenters.sightPresenter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import com.gerken.audioGuide.interfaces.*;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.objectModel.*;
import com.gerken.audioGuide.presenters.SightPresenter;;

public class HandleLocationChange {

	@Test
	public void test() throws Exception {
		final double EXPECTED_LOCATION_LATITUDE = 12.345;
		final double EXPECTED_LOCATION_LONGITUDE = 24.567;
		final String EXPECTED_SIGHT_LOOK_IMAGE_NAME = "colosseum.jpg";
		final String EXPECTED_SIGHT_NAME = "Colosseum";
		final byte[] EXPECTED_SIGHT_LOOK_IMAGE_DATA = new byte[]{1,2,3};
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
		
		ByteArrayInputStream str = new ByteArrayInputStream(EXPECTED_SIGHT_LOOK_IMAGE_DATA);
		
		SightView view = mock(SightView.class);
		AssetStreamProvider assetStreamProvider = mock(AssetStreamProvider.class);
		when(assetStreamProvider.getImageAssetStream(EXPECTED_SIGHT_LOOK_IMAGE_NAME))
			.thenReturn(str);
		AudioPlayer player = mock(AudioPlayer.class);
		SharedPreferenceStorage prefStorage = mock(SharedPreferenceStorage.class);
		Logger logger = mock(Logger.class);
		
		SightPresenter sut = new SightPresenter(city, view, assetStreamProvider,
				player, prefStorage, logger);
		
		// --- Act
		sut.handleLocationChange(EXPECTED_LOCATION_LATITUDE, EXPECTED_LOCATION_LONGITUDE);
		
		// --- Assert
		verify(view).acceptNewSightGotInRange(EXPECTED_SIGHT_NAME, str);
	}

}
