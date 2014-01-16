package com.gerken.audioGuideTests.presenters.routeMapPresenter;

import static org.mockito.Mockito.*;

import java.util.Random;

import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.containers.Point;
import com.gerken.audioGuide.interfaces.LocationTracker;
import com.gerken.audioGuide.interfaces.MediaAssetManager;
import com.gerken.audioGuide.interfaces.listeners.OnEventListener;
import com.gerken.audioGuide.interfaces.listeners.OnLocationChangedListener;
import com.gerken.audioGuide.interfaces.listeners.OnMultiTouchListener;
import com.gerken.audioGuide.interfaces.listeners.OnViewStateRestoreListener;
import com.gerken.audioGuide.interfaces.listeners.OnViewStateSaveListener;
import com.gerken.audioGuide.interfaces.views.RouteMapView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.presenters.RouteMapPresenter;
import com.gerken.audioGuideTests.SimpleViewStateContainer;

public class HandleViewLayoutComplete {
	private Random _random = new Random(System.currentTimeMillis());
	
	@Test
	public void Given_ScaleStoredToInstanceState__Then_MapScaled() {
		final float EXPECTED_SCALE = 0.5f;
		SimpleViewStateContainer container = new SimpleViewStateContainer();
		
		RouteMapView view = mock(RouteMapView.class);
		when(view.getHeight()).thenReturn(_random.nextInt());
		when(view.getWidth()).thenReturn(_random.nextInt());
		
		RouteMapPresenter.RouteMapViewStateContainer containerWrapper = 
				new RouteMapPresenter.RouteMapViewStateContainer(container);
		containerWrapper.setScale(EXPECTED_SCALE);
		
		SutSetupResult sutSetupResult = setupSut(view);
		sutSetupResult.viewStateRestoreListener.onStateRestore(container);
		
		// --- Act
		sutSetupResult.viewLayoutCompleteListener.onEvent();
		
		// --- Assert
		verify(view).setMapScale(EXPECTED_SCALE);
	}
	
	private SutSetupResult setupSut(RouteMapView view) {
		SutSetupResult result = new SutSetupResult();
		
		ArgumentCaptor<OnViewStateRestoreListener> viewStateRestoreListenerCaptor = 
				ArgumentCaptor.forClass(OnViewStateRestoreListener.class);
		doNothing().when(view).addViewInstanceStateRestoredListener(viewStateRestoreListenerCaptor.capture());
		
		ArgumentCaptor<OnEventListener> viewLayoutCompleteListenerCaptor = 
				ArgumentCaptor.forClass(OnEventListener.class);
		doNothing().when(view).addViewLayoutCompleteListener(
				viewLayoutCompleteListenerCaptor.capture());
		
		result.sut = new RouteMapPresenter(new City(), view, mock(MediaAssetManager.class));
		result.viewStateRestoreListener = viewStateRestoreListenerCaptor.getValue();
		result.viewLayoutCompleteListener = viewLayoutCompleteListenerCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		public RouteMapPresenter sut;
		public OnViewStateRestoreListener viewStateRestoreListener;
		public OnEventListener viewLayoutCompleteListener;
	}
}
