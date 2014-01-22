package com.gerken.audioGuideTests.presenters.routeMapPresenter;

import static org.mockito.Mockito.*;

import java.util.Random;

import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.interfaces.MediaAssetManager;
import com.gerken.audioGuide.interfaces.listeners.OnEventListener;
import com.gerken.audioGuide.interfaces.listeners.OnViewStateRestoreListener;
import com.gerken.audioGuide.interfaces.views.RouteMapView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.presenters.RouteMapPresenter;
import com.gerken.audioGuideTests.SimpleViewStateContainer;

public class HandleViewLayoutComplete {
	private Random _random = new Random(System.currentTimeMillis());
	
	@Test
	public void Given_ScaleStoredToInstanceState__Then_MapScaled() {
		final int VIEW_WIDTH = 40;
		final int VIEW_HEIGHT = 60;
		final int ORIGINAL_MAP_WIDTH = 200;
		final int ORIGINAL_MAP_HEIGHT = 160;
		
		final float EXPECTED_SCALE = 0.5f;
		
		SimpleViewStateContainer container = new SimpleViewStateContainer();
		
		RouteMapView view = mock(RouteMapView.class);
		when(view.getWidth()).thenReturn(VIEW_WIDTH);
		when(view.getHeight()).thenReturn(VIEW_HEIGHT);
		when(view.getOriginalMapWidth()).thenReturn(ORIGINAL_MAP_WIDTH);
		when(view.getOriginalMapHeight()).thenReturn(ORIGINAL_MAP_HEIGHT);		
		
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
	
	@Test
	public void Given_ScaledMapDoesNotCoverAtLeastOneDimensionAfterRestoringInstanceState__Then_MapScaleAdjusted() {
		final int VIEW_WIDTH = 60;
		final int VIEW_HEIGHT = 40;
		final int ORIGINAL_MAP_WIDTH = 200;
		final int ORIGINAL_MAP_HEIGHT = 160;
		
		final float ORIGINAL_SCALE = 0.2f;
		final float EXPECTED_ADJUSTED_SCALE = 0.25f;
		SimpleViewStateContainer container = new SimpleViewStateContainer();
		
		RouteMapView view = mock(RouteMapView.class);
		when(view.getWidth()).thenReturn(VIEW_WIDTH);
		when(view.getHeight()).thenReturn(VIEW_HEIGHT);
		when(view.getOriginalMapWidth()).thenReturn(ORIGINAL_MAP_WIDTH);
		when(view.getOriginalMapHeight()).thenReturn(ORIGINAL_MAP_HEIGHT);
		
		
		RouteMapPresenter.RouteMapViewStateContainer containerWrapper = 
				new RouteMapPresenter.RouteMapViewStateContainer(container);
		containerWrapper.setScale(ORIGINAL_SCALE);
		
		SutSetupResult sutSetupResult = setupSut(view);
		sutSetupResult.viewStateRestoreListener.onStateRestore(container);
		
		// --- Act
		sutSetupResult.viewLayoutCompleteListener.onEvent();
		
		// --- Assert
		verify(view).setMapScale(EXPECTED_ADJUSTED_SCALE);
	}
	
	@Test
	public void Given_MapPointerIsVisible__Then_MapPointerPositionAndScaleSet() {
		final float EXPECTED_SCALE = 0.5f;
		final boolean MAP_POINTER_IS_VISIBLE = true;
		final int MAP_POINTER_X = 80;
		final int MAP_POINTER_Y = 120;
		final int MAP_POINTER_WIDTH = 20;
		final int MAP_POINTER_HEIGHT = 20;
		
		final int EXPECTED_MAP_POINTER_VIEW_LEFT = 35;
		final int EXPECTED_MAP_POINTER_VIEW_TOP = 55;
		
		SimpleViewStateContainer container = new SimpleViewStateContainer();
		
		RouteMapView view = mock(RouteMapView.class);
		when(view.getHeight()).thenReturn(_random.nextInt());
		when(view.getWidth()).thenReturn(_random.nextInt());
		when(view.getOriginalMapPointerWidth()).thenReturn(MAP_POINTER_WIDTH);
		when(view.getOriginalMapPointerHeight()).thenReturn(MAP_POINTER_HEIGHT);
		
		RouteMapPresenter.RouteMapViewStateContainer containerWrapper = 
				new RouteMapPresenter.RouteMapViewStateContainer(container);
		containerWrapper.setScale(EXPECTED_SCALE);
		containerWrapper.setMapPointerVisible(MAP_POINTER_IS_VISIBLE);
		containerWrapper.setMapPointerPosition(MAP_POINTER_X, MAP_POINTER_Y);
		
		SutSetupResult sutSetupResult = setupSut(view);
		sutSetupResult.viewStateRestoreListener.onStateRestore(container);
		
		// --- Act
		sutSetupResult.viewLayoutCompleteListener.onEvent();
		
		// --- Assert
		verify(view).setMapPointerScale(EXPECTED_SCALE);
		verify(view).showLocationPointerAt(EXPECTED_MAP_POINTER_VIEW_LEFT, EXPECTED_MAP_POINTER_VIEW_TOP);
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
