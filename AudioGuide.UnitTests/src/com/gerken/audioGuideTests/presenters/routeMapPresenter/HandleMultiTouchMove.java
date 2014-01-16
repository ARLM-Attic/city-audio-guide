package com.gerken.audioGuideTests.presenters.routeMapPresenter;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.containers.Point;
import com.gerken.audioGuide.interfaces.LocationTracker;
import com.gerken.audioGuide.interfaces.MediaAssetManager;
import com.gerken.audioGuide.interfaces.listeners.OnLocationChangedListener;
import com.gerken.audioGuide.interfaces.listeners.OnMultiTouchListener;
import com.gerken.audioGuide.interfaces.views.RouteMapView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.presenters.RouteMapPresenter;

public class HandleMultiTouchMove {
	private static final float DEFAULT_DELTA = 0.001f; 

	@Test
	public void Given_ZoomOutMoveDone__Then_MapScaleSet_MapPointerScaleSet() {
		final float EXPECTED_SCALE = 0.5f;
		
		RouteMapView view = mock(RouteMapView.class);
		when(view.getHeight()).thenReturn(40);
		when(view.getWidth()).thenReturn(40);
		when(view.getOriginalMapWidth()).thenReturn(160);
		when(view.getOriginalMapHeight()).thenReturn(160);
		
		SutSetupResult sutSetupResult = setupSut(view);
		sutSetupResult.multiTouchListener.onMultiTouchDown(
			(Point<Float>[])new Point[]{ new Point<Float>(20f, 0f), new Point<Float>(40f, 0f) }
		);
		
		// --- Act
		sutSetupResult.multiTouchListener.onMultiTouchMove(
			(Point<Float>[])new Point[]{ new Point<Float>(25f, 0f), new Point<Float>(35f, 0f) }
		);
		
		// --- Assert
		verify(view).setMapScale(AdditionalMatchers.eq(EXPECTED_SCALE, DEFAULT_DELTA));
		verify(view).setMapPointerScale(AdditionalMatchers.eq(EXPECTED_SCALE, DEFAULT_DELTA));
	}
	
	@Test
	public void Given_ZoomOutMoveDone__Then_MapScrolledToKeepScreenCenterAtTheSamePlace() {
		final int VIEW_WIDTH = 40;
		final int VIEW_HEIGHT = 60;
		final int ORIGINAL_SCROLL_X = 30;
		final int ORIGINAL_SCROLL_Y = 40;
		final int ORIGINAL_MAP_WIDTH = 200;
		final int ORIGINAL_MAP_HEIGHT = 200;
		
		final int EXPECTED_VIEW_SCROLL_X = 5;
		final int EXPECTED_VIEW_SCROLL_Y = 5;
		
		RouteMapView view = mock(RouteMapView.class);
		when(view.getWidth()).thenReturn(VIEW_WIDTH);
		when(view.getHeight()).thenReturn(VIEW_HEIGHT);		
		when(view.getOriginalMapWidth()).thenReturn(ORIGINAL_MAP_WIDTH);
		when(view.getOriginalMapHeight()).thenReturn(ORIGINAL_MAP_HEIGHT);
		when(view.getScrollX()).thenReturn(ORIGINAL_SCROLL_X);
		when(view.getScrollY()).thenReturn(ORIGINAL_SCROLL_Y);
		
		SutSetupResult sutSetupResult = setupSut(view);
		sutSetupResult.multiTouchListener.onMultiTouchDown(
			(Point<Float>[])new Point[]{ new Point<Float>(20f, 0f), new Point<Float>(40f, 0f) }
		);
		
		// --- Act
		sutSetupResult.multiTouchListener.onMultiTouchMove(
			(Point<Float>[])new Point[]{ new Point<Float>(25f, 0f), new Point<Float>(35f, 0f) }
		);
		
		// --- Assert
		verify(view).scrollTo(EXPECTED_VIEW_SCROLL_X, EXPECTED_VIEW_SCROLL_Y);
	}
	
	private SutSetupResult setupSut(RouteMapView view) {
		SutSetupResult result = new SutSetupResult();
		
		ArgumentCaptor<OnMultiTouchListener> multiTouchListenerCaptor = 
				ArgumentCaptor.forClass(OnMultiTouchListener.class);
		doNothing().when(view).addViewMultiTouchListener(multiTouchListenerCaptor.capture());
		
		City city = new City();
		result.sut = new RouteMapPresenter(city, view, mock(MediaAssetManager.class));
		result.multiTouchListener = multiTouchListenerCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		public RouteMapPresenter sut;
		public OnMultiTouchListener multiTouchListener;
	}
}
