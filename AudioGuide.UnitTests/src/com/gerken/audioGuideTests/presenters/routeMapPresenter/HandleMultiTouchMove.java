package com.gerken.audioGuideTests.presenters.routeMapPresenter;

import static org.mockito.Mockito.*;

import java.util.Random;

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
import com.gerken.audioGuide.objectModel.MapBounds;
import com.gerken.audioGuide.objectModel.Route;
import com.gerken.audioGuide.presenters.RouteMapPresenter;

public class HandleMultiTouchMove {
	private static final float DEFAULT_DELTA = 0.001f; 
	
	private Random _random = new Random(System.currentTimeMillis());

	@Test
	public void Given_ZoomOutMoveDone__Then_MapScaleSet_MapPointerScaleSet() {
		final int ORIGINAL_MAP_WIDTH = 200;
		final int ORIGINAL_MAP_HEIGHT = 160;
		
		final float EXPECTED_SCALE = 0.5f;
		final int EXPECTED_SCALED_MAP_WIDTH = 100;
		final int EXPECTED_SCALED_MAP_HEIGHT = 80;
		
		RouteMapView view = mock(RouteMapView.class);
		when(view.getHeight()).thenReturn(40);
		when(view.getWidth()).thenReturn(40);
		when(view.getOriginalMapWidth()).thenReturn(ORIGINAL_MAP_WIDTH);
		when(view.getOriginalMapHeight()).thenReturn(ORIGINAL_MAP_HEIGHT);
		
		SutSetupResult sutSetupResult = setupSut(view);
		
		// --- Act
		doZoomOutScaleOneHalf(sutSetupResult);
		
		// --- Assert
		verify(view).setMapScale(AdditionalMatchers.eq(EXPECTED_SCALE, DEFAULT_DELTA));
		verify(view).setMapPointerScale(AdditionalMatchers.eq(EXPECTED_SCALE, DEFAULT_DELTA));
		verify(view).setMapSize(EXPECTED_SCALED_MAP_WIDTH, EXPECTED_SCALED_MAP_HEIGHT);
		verify(view).setMapPointerContainerSize(EXPECTED_SCALED_MAP_WIDTH, EXPECTED_SCALED_MAP_HEIGHT);
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
		
		// --- Act
		doZoomOutScaleOneHalf(sutSetupResult);
		
		// --- Assert
		verify(view).scrollTo(EXPECTED_VIEW_SCROLL_X, EXPECTED_VIEW_SCROLL_Y);
	}
	
	@Test
	public void Given_ScaledMapSizeIsLessThanScreenSize__Then_ZoomingOutStopped() {
		final int VIEW_WIDTH = 120;
		final int VIEW_HEIGHT = 160;
		final int ORIGINAL_MAP_WIDTH = 200;
		final int ORIGINAL_MAP_HEIGHT = 200;
		
		RouteMapView view = mock(RouteMapView.class);
		when(view.getWidth()).thenReturn(VIEW_WIDTH);
		when(view.getHeight()).thenReturn(VIEW_HEIGHT);		
		when(view.getOriginalMapWidth()).thenReturn(ORIGINAL_MAP_WIDTH);
		when(view.getOriginalMapHeight()).thenReturn(ORIGINAL_MAP_HEIGHT);
		
		SutSetupResult sutSetupResult = setupSut(view);
		
		// --- Act
		doZoomOutScaleOneHalf(sutSetupResult);
		
		// --- Assert
		verify(view, never()).setMapScale(anyFloat());
		verify(view, never()).setMapPointerScale(anyFloat());
	}
	
	@Test
	public void Given_MapPointerVisible__Then_MapPointerShownAtScaledPosition() {
		final int VIEW_WIDTH = 120;
		final int VIEW_HEIGHT = 160;
		final int ORIGINAL_MAP_WIDTH = 400;
		final int ORIGINAL_MAP_HEIGHT = 200;
		final int MAP_POINTER_WIDTH = 20;
		final int MAP_POINTER_HEIGHT = 20;
		
		final double MAP_NORTH = 26.0;
		final double MAP_WEST = 12.0;
		final double MAP_SOUTH = 22.0;
		final double MAP_EAST = 16.0;
		final int ROUTE_ID = _random.nextInt();
		
		final int EXPECTED_SCALED_MAP_POINTER_X = 95;
		final int EXPECTED_SCALED_MAP_POINTER_Y = 45;
		
		MapBounds bounds = new MapBounds(MAP_NORTH, MAP_WEST, MAP_SOUTH, MAP_EAST);
		City city = createSingleRouteCity(ROUTE_ID, bounds);
		
		RouteMapView view = mock(RouteMapView.class);
		when(view.getWidth()).thenReturn(VIEW_WIDTH);
		when(view.getHeight()).thenReturn(VIEW_HEIGHT);		
		when(view.getOriginalMapWidth()).thenReturn(ORIGINAL_MAP_WIDTH);
		when(view.getOriginalMapHeight()).thenReturn(ORIGINAL_MAP_HEIGHT);
		when(view.getOriginalMapPointerWidth()).thenReturn(MAP_POINTER_WIDTH);
		when(view.getOriginalMapPointerHeight()).thenReturn(MAP_POINTER_HEIGHT);
		when(view.getRouteId()).thenReturn(ROUTE_ID);
		
		SutSetupResult sutSetupResult = setupSut(city, view, mock(LocationTracker.class));
		sutSetupResult.locationChangedListener.onLocationChanged(
			0.5*(MAP_NORTH+MAP_SOUTH), 0.5*(MAP_EAST+MAP_WEST));
		
		// --- Act
		doZoomOutScaleOneHalf(sutSetupResult);
		
		// --- Assert
		verify(view).showLocationPointerAt(EXPECTED_SCALED_MAP_POINTER_X, EXPECTED_SCALED_MAP_POINTER_Y);
	}
	
	@SuppressWarnings("unchecked")
	private void doZoomOutScaleOneHalf(SutSetupResult sutSetupResult) {
		sutSetupResult.multiTouchListener.onMultiTouchDown(
			(Point<Float>[])new Point[]{ new Point<Float>(20f, 0f), new Point<Float>(40f, 0f) }
		);
		sutSetupResult.multiTouchListener.onMultiTouchMove(
			(Point<Float>[])new Point[]{ new Point<Float>(25f, 0f), new Point<Float>(35f, 0f) }
		);
	}
	
	private City createSingleRouteCity(int routeId, MapBounds routeMapBounds) {
		City city = new City();
		Route route = new Route(routeId, "whatever");
		route.setMapBounds(routeMapBounds);
		city.getRoutes().add(route);
		
		return city;
	}
	
	private SutSetupResult setupSut(RouteMapView view) {
		return setupSut(new City(), view, mock(LocationTracker.class));
	}
	
	private SutSetupResult setupSut(City city, RouteMapView view, LocationTracker tracker) {
		SutSetupResult result = new SutSetupResult();
		
		ArgumentCaptor<OnMultiTouchListener> multiTouchListenerCaptor = 
				ArgumentCaptor.forClass(OnMultiTouchListener.class);
		doNothing().when(view).addViewMultiTouchListener(multiTouchListenerCaptor.capture());
		
		ArgumentCaptor<OnLocationChangedListener> locationChangedListenerCaptor = 
				ArgumentCaptor.forClass(OnLocationChangedListener.class);
		doNothing().when(tracker).addLocationChangedListener(locationChangedListenerCaptor.capture());
		
		result.sut = new RouteMapPresenter(city, view, mock(MediaAssetManager.class));
		result.sut.setLocationTracker(tracker);
		result.multiTouchListener = multiTouchListenerCaptor.getValue();
		result.locationChangedListener = locationChangedListenerCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		public RouteMapPresenter sut;
		public OnLocationChangedListener locationChangedListener;
		public OnMultiTouchListener multiTouchListener;
	}
}
