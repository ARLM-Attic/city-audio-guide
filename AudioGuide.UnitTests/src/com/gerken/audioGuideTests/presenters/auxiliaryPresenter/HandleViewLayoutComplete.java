package com.gerken.audioGuideTests.presenters.auxiliaryPresenter;

import static org.mockito.Mockito.*;

import java.util.Random;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.interfaces.DownscalingBitmapLoader;
import com.gerken.audioGuide.interfaces.listeners.OnEventListener;
import com.gerken.audioGuide.interfaces.views.AuxiliaryView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.objectModel.CityConfiguration;
import com.gerken.audioGuide.presenters.AuxiliaryPresenter;

public class HandleViewLayoutComplete {
	private Random _random = new Random(System.currentTimeMillis());
	
	@Test
	public void Given_ViewIsLandscape__Then_LandscapeBackgroundIsLoaded() throws Exception {
		final int WIDTH = 320;
		final int HEIGHT = 240;
		final String EXPECTED_LANDSCAPE_BACKGROUND_IMAGE_NAME = "view_land.jpg";
		
		AuxiliaryView auxView = mock(AuxiliaryView.class);
		when(auxView.getWidth()).thenReturn(WIDTH);
		when(auxView.getHeight()).thenReturn(HEIGHT);
		DownscalingBitmapLoader bitmapLoader = mock(DownscalingBitmapLoader.class);

		CityConfiguration config = new CityConfiguration(createRandomString(), 
				createRandomString(), EXPECTED_LANDSCAPE_BACKGROUND_IMAGE_NAME);
		City city = new City(_random.nextInt(), createRandomString(), config);
		
		SutSetupResult sutSetupResult = setupSut(city, auxView, bitmapLoader);
		
		// --- Act
		sutSetupResult.viewLayoutCompleteListener.onEvent();
		
		// --- Assert
		verify(bitmapLoader).load(EXPECTED_LANDSCAPE_BACKGROUND_IMAGE_NAME, WIDTH, HEIGHT);
	}
	
	@Test
	public void Given_ViewIsPortrait__Then_LandscapeBackgroundIsLoaded() throws Exception {
		final int WIDTH = 320;
		final int HEIGHT = 640;
		final String EXPECTED_PORTRAIT_BACKGROUND_IMAGE_NAME = "view_port.jpg";
		
		AuxiliaryView auxView = mock(AuxiliaryView.class);
		when(auxView.getWidth()).thenReturn(WIDTH);
		when(auxView.getHeight()).thenReturn(HEIGHT);
		DownscalingBitmapLoader bitmapLoader = mock(DownscalingBitmapLoader.class);

		CityConfiguration config = new CityConfiguration(createRandomString(), 
				EXPECTED_PORTRAIT_BACKGROUND_IMAGE_NAME, createRandomString());
		City city = new City(_random.nextInt(), createRandomString(), config);
		
		SutSetupResult sutSetupResult = setupSut(city, auxView, bitmapLoader);
		
		// --- Act
		sutSetupResult.viewLayoutCompleteListener.onEvent();
		
		// --- Assert
		verify(bitmapLoader).load(EXPECTED_PORTRAIT_BACKGROUND_IMAGE_NAME, WIDTH, HEIGHT);
	}
	
	private String createRandomString() {
		return String.valueOf(_random.nextLong());
	}
	
	private SutSetupResult setupSut(City city, AuxiliaryView view, 
			DownscalingBitmapLoader bitmapLoader) {
		SutSetupResult result = new SutSetupResult();

		ArgumentCaptor<OnEventListener> viewLayoutCompleteListenerCaptor = 
				ArgumentCaptor.forClass(OnEventListener.class);
		doNothing().when(view).addViewLayoutCompleteListener(
				viewLayoutCompleteListenerCaptor.capture());		
		
		AuxiliaryPresenter sut = new AuxiliaryPresenter(view, city);
		sut.setBitmapLoader(bitmapLoader);
		
		result.sut = sut;
		result.viewLayoutCompleteListener = viewLayoutCompleteListenerCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		public AuxiliaryPresenter sut;
		public OnEventListener viewLayoutCompleteListener;
	}
}
