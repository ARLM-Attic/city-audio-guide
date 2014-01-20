package com.gerken.audioGuideTests.presenters.routeMapPresenter;

import static org.mockito.Mockito.*;

import java.util.Random;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.containers.Point;
import com.gerken.audioGuide.interfaces.LocationTracker;
import com.gerken.audioGuide.interfaces.MediaAssetManager;
import com.gerken.audioGuide.interfaces.listeners.OnEventListener;
import com.gerken.audioGuide.interfaces.listeners.OnLocationChangedListener;
import com.gerken.audioGuide.interfaces.listeners.OnMultiTouchListener;
import com.gerken.audioGuide.interfaces.listeners.OnViewStateRestoreListener;
import com.gerken.audioGuide.interfaces.views.RouteMapView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.objectModel.MapBounds;
import com.gerken.audioGuide.objectModel.Route;
import com.gerken.audioGuide.presenters.RouteMapPresenter;
import com.gerken.audioGuideTests.SimpleViewStateContainer;

public class HandleLocationChanged {
	private Random _random = new Random(System.currentTimeMillis());
	
	@Test
	public void Given_NewLocationIsOnMap__Then_MapPointerShown_MapScrolled() {
		final double MAP_NORTH = 26.0;
		final double MAP_WEST = 12.0;
		final double MAP_SOUTH = 22.0;
		final double MAP_EAST = 16.0;
		
		final double NEW_LATITUDE = 25.0;
		final double NEW_LONGITUDE = 13.0;
		
		final int MAP_WIDTH = 400;
		final int MAP_HEIGHT = 200;
		final int SCR_WIDTH = 100;
		final int SCR_HEIGHT = 200;
		
		final int EXP_MAP_POINTER_X = 100;
		final int EXP_MAP_POINTER_Y = 50;
		final int EXP_SCROLL_TO_X = 50;
		final int EXP_SCROLL_TO_Y = 0;
		
		final int ROUTE_ID = 42;
		
		MapBounds bounds = new MapBounds(MAP_NORTH, MAP_WEST, MAP_SOUTH, MAP_EAST);
		City city = createSingleRouteCity(ROUTE_ID, bounds);
		
		RouteMapView view = mock(RouteMapView.class);
		when(view.getHeight()).thenReturn(SCR_HEIGHT);
		when(view.getWidth()).thenReturn(SCR_WIDTH);
		when(view.getMapWidth()).thenReturn(MAP_WIDTH);
		when(view.getMapHeight()).thenReturn(MAP_HEIGHT);
		when(view.getOriginalMapWidth()).thenReturn(MAP_WIDTH);
		when(view.getOriginalMapHeight()).thenReturn(MAP_HEIGHT);		
		when(view.getRouteId()).thenReturn(ROUTE_ID);
		LocationTracker tracker = mock(LocationTracker.class);
		
		SutSetupResult sutSetupResult = setupSut(city, view, tracker);
		
		// --- Act
		sutSetupResult.locationChangedListener.onLocationChanged(NEW_LATITUDE, NEW_LONGITUDE);
		
		// --- Assert
		verify(view).showLocationPointerAt(EXP_MAP_POINTER_X, EXP_MAP_POINTER_Y);
		verify(view).scrollTo(EXP_SCROLL_TO_X, EXP_SCROLL_TO_Y);
	}
	
	@Test
	public void Given_NewLocationIsOutsideMap__Then_MapPointerHidden_MapNotScrolled() {
		final double MAP_NORTH = 26.0;
		final double MAP_WEST = 12.0;
		final double MAP_SOUTH = 22.0;
		final double MAP_EAST = 16.0;
		
		final double NEW_LATITUDE = 31.0;
		final double NEW_LONGITUDE = 13.0;
		
		final int MAP_WIDTH = 400;
		final int MAP_HEIGHT = 200;

		final int ROUTE_ID = 42;
		
		MapBounds bounds = new MapBounds(MAP_NORTH, MAP_WEST, MAP_SOUTH, MAP_EAST);
		City city = createSingleRouteCity(ROUTE_ID, bounds);
		
		RouteMapView view = mock(RouteMapView.class);
		when(view.getMapHeight()).thenReturn(MAP_HEIGHT);
		when(view.getMapWidth()).thenReturn(MAP_WIDTH);
		when(view.getRouteId()).thenReturn(ROUTE_ID);
		LocationTracker tracker = mock(LocationTracker.class);
		
		SutSetupResult sutSetupResult = setupSut(city, view, tracker);
		
		// --- Act
		sutSetupResult.locationChangedListener.onLocationChanged(NEW_LATITUDE, NEW_LONGITUDE);
		
		// --- Assert
		verify(view).hideLocationPointer();
		verify(view, never()).scrollTo(anyInt(), anyInt());
	}
	
	@Test
	public void Given_ZoomOutMade_NewLocationIsOnMap__Then_MapPointerPositionScaled() {
		final double MAP_NORTH = 26.0;
		final double MAP_WEST = 12.0;
		final double MAP_SOUTH = 22.0;
		final double MAP_EAST = 16.0;
		
		final double NEW_LATITUDE = 25.0;
		final double NEW_LONGITUDE = 13.0;
		
		final int MAP_WIDTH = 400;
		final int MAP_HEIGHT = 200;
		final int SCR_WIDTH = 80;
		final int SCR_HEIGHT = 120;
		final int MAP_POINTER_WIDTH = 20;
		final int MAP_POINTER_HEIGHT = 20;
		
		final int EXP_MAP_POINTER_X = 45;
		final int EXP_MAP_POINTER_Y = 20;
		final int EXP_SCROLL_TO_X = 10;
		final int EXP_SCROLL_TO_Y = 0;
		
		final int ROUTE_ID = _random.nextInt();
		
		MapBounds bounds = new MapBounds(MAP_NORTH, MAP_WEST, MAP_SOUTH, MAP_EAST);
		City city = createSingleRouteCity(ROUTE_ID, bounds);
		
		RouteMapView view = mock(RouteMapView.class);
		when(view.getHeight()).thenReturn(SCR_HEIGHT);
		when(view.getWidth()).thenReturn(SCR_WIDTH);
		when(view.getOriginalMapHeight()).thenReturn(MAP_HEIGHT);
		when(view.getOriginalMapWidth()).thenReturn(MAP_WIDTH);
		when(view.getRouteId()).thenReturn(ROUTE_ID);
		when(view.getOriginalMapPointerWidth()).thenReturn(MAP_POINTER_WIDTH);
		when(view.getOriginalMapPointerHeight()).thenReturn(MAP_POINTER_HEIGHT);
		LocationTracker tracker = mock(LocationTracker.class);
		
		SutSetupResult sutSetupResult = setupSut(city, view, tracker);
		
		doZoomOutScaleOneHalf(sutSetupResult.multiTouchListener);		
		
		// --- Act
		sutSetupResult.locationChangedListener.onLocationChanged(NEW_LATITUDE, NEW_LONGITUDE);
		
		// --- Assert
		verify(view).showLocationPointerAt(EXP_MAP_POINTER_X, EXP_MAP_POINTER_Y);
		verify(view).scrollTo(EXP_SCROLL_TO_X, EXP_SCROLL_TO_Y);
	}
	
	@Test
	public void Given_TwiceNewLocationIsOnMap__Then_MapScrolledOnlyOnce() throws InterruptedException {
		final double MAP_NORTH = 26.0;
		final double MAP_WEST = 12.0;
		final double MAP_SOUTH = 22.0;
		final double MAP_EAST = 16.0;
		
		final double NEW_LATITUDE = 25.0;
		final double NEW_LONGITUDE = 13.0;
		
		final int MAP_WIDTH = 400;
		final int MAP_HEIGHT = 200;
		
		final int ROUTE_ID = 42;
		
		MapBounds bounds = new MapBounds(MAP_NORTH, MAP_WEST, MAP_SOUTH, MAP_EAST);
		City city = createSingleRouteCity(ROUTE_ID, bounds);
		
		RouteMapView view = mock(RouteMapView.class);
		when(view.getHeight()).thenReturn(42);
		when(view.getWidth()).thenReturn(42);
		when(view.getMapHeight()).thenReturn(MAP_HEIGHT);
		when(view.getMapWidth()).thenReturn(MAP_WIDTH);
		when(view.getOriginalMapHeight()).thenReturn(MAP_HEIGHT);
		when(view.getOriginalMapWidth()).thenReturn(MAP_WIDTH);
		when(view.getRouteId()).thenReturn(ROUTE_ID);
		LocationTracker tracker = mock(LocationTracker.class);
		
		SutSetupResult sutSetupResult = setupSut(city, view, tracker);
		
		// --- Act
		sutSetupResult.locationChangedListener.onLocationChanged(NEW_LATITUDE, NEW_LONGITUDE);
		Thread.sleep(100);
		sutSetupResult.locationChangedListener.onLocationChanged(NEW_LATITUDE+0.1, NEW_LONGITUDE+0.1);
		
		// --- Assert
		verify(view, times(1)).scrollTo(anyInt(), anyInt());
	}
	
	@Test
	public void Given_SecondLocationOnMapSignaledAfterStateRestoring__Then_MapNotScrolled() throws InterruptedException {
		final double MAP_NORTH = 26.0;
		final double MAP_WEST = 12.0;
		final double MAP_SOUTH = 22.0;
		final double MAP_EAST = 16.0;
		
		final double NEW_LATITUDE = 25.0;
		final double NEW_LONGITUDE = 13.0;
		
		final int MAP_WIDTH = 400;
		final int MAP_HEIGHT = 200;
		final float SCALE = 1f;
		final boolean MAP_POINTER_IS_VISIBLE = true;
		final boolean SCROLLING_TO_CURRENT_LOCATION_HAS_ALREADY_BEEN_DONE = true;
		
		final int ROUTE_ID = _random.nextInt();
		
		SimpleViewStateContainer container = new SimpleViewStateContainer();
		
		MapBounds bounds = new MapBounds(MAP_NORTH, MAP_WEST, MAP_SOUTH, MAP_EAST);
		City city = createSingleRouteCity(ROUTE_ID, bounds);
		
		RouteMapView view = mock(RouteMapView.class);
		when(view.getHeight()).thenReturn(_random.nextInt());
		when(view.getWidth()).thenReturn(_random.nextInt());
		when(view.getMapHeight()).thenReturn(MAP_HEIGHT);
		when(view.getMapWidth()).thenReturn(MAP_WIDTH);
		when(view.getOriginalMapHeight()).thenReturn(MAP_HEIGHT);
		when(view.getOriginalMapWidth()).thenReturn(MAP_WIDTH);
		when(view.getRouteId()).thenReturn(ROUTE_ID);
		LocationTracker tracker = mock(LocationTracker.class);
		
		RouteMapPresenter.RouteMapViewStateContainer containerWrapper = 
				new RouteMapPresenter.RouteMapViewStateContainer(container);
		containerWrapper.setScale(SCALE);
		containerWrapper.setMapPointerVisible(MAP_POINTER_IS_VISIBLE);
		containerWrapper.setMapPointerPosition(_random.nextInt(), _random.nextInt());
		containerWrapper.setScrollingToCurrentLocactionDone(SCROLLING_TO_CURRENT_LOCATION_HAS_ALREADY_BEEN_DONE);
		
		SutSetupResult sutSetupResult = setupSut(city, view, tracker);
		sutSetupResult.viewStateRestoreListener.onStateRestore(container);
		sutSetupResult.viewLayoutCompleteListener.onEvent();
		
		// --- Act
		sutSetupResult.locationChangedListener.onLocationChanged(NEW_LATITUDE, NEW_LONGITUDE);
		
		// --- Assert
		verify(view, times(1)).scrollTo(anyInt(), anyInt());
	}
	
	@SuppressWarnings("unchecked")
	private void doZoomOutScaleOneHalf(OnMultiTouchListener multiTouchListener) {
		multiTouchListener.onMultiTouchDown(
			(Point<Float>[])new Point[]{ new Point<Float>(20f, 0f), new Point<Float>(40f, 0f) }
		);
		multiTouchListener.onMultiTouchMove(
			(Point<Float>[])new Point[]{ new Point<Float>(25f, 0f), new Point<Float>(35f, 0f) }
		);
		multiTouchListener.onMultiTouchUp();
	}
	
	private SutSetupResult setupSut(City city, RouteMapView view, LocationTracker tracker) {
		SutSetupResult result = new SutSetupResult();

		ArgumentCaptor<OnLocationChangedListener> locationChangedListenerCaptor = 
				ArgumentCaptor.forClass(OnLocationChangedListener.class);
		doNothing().when(tracker).addLocationChangedListener(locationChangedListenerCaptor.capture());
		
		ArgumentCaptor<OnMultiTouchListener> multiTouchListenerCaptor = 
				ArgumentCaptor.forClass(OnMultiTouchListener.class);
		doNothing().when(view).addViewMultiTouchListener(multiTouchListenerCaptor.capture());
		
		ArgumentCaptor<OnViewStateRestoreListener> viewStateRestoreListenerCaptor = 
				ArgumentCaptor.forClass(OnViewStateRestoreListener.class);
		doNothing().when(view).addViewInstanceStateRestoredListener(viewStateRestoreListenerCaptor.capture());
		
		ArgumentCaptor<OnEventListener> viewLayoutCompleteListenerCaptor = 
				ArgumentCaptor.forClass(OnEventListener.class);
		doNothing().when(view).addViewLayoutCompleteListener(
				viewLayoutCompleteListenerCaptor.capture());
		
		RouteMapPresenter sut = new RouteMapPresenter(city, view, mock(MediaAssetManager.class));
		sut.setLocationTracker(tracker);
		
		result.sut = sut;
		result.locationChangedListener = locationChangedListenerCaptor.getValue();
		result.multiTouchListener = multiTouchListenerCaptor.getValue();
		result.viewStateRestoreListener = viewStateRestoreListenerCaptor.getValue();
		result.viewLayoutCompleteListener = viewLayoutCompleteListenerCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		public RouteMapPresenter sut;
		public OnLocationChangedListener locationChangedListener;
		public OnMultiTouchListener multiTouchListener;
		public OnViewStateRestoreListener viewStateRestoreListener;
		public OnEventListener viewLayoutCompleteListener;
	}
	
	private City createSingleRouteCity(int routeId, MapBounds routeMapBounds) {
		City city = new City();
		Route route = new Route(routeId, "whatever");
		route.setMapBounds(routeMapBounds);
		city.getRoutes().add(route);
		
		return city;
	}
}
