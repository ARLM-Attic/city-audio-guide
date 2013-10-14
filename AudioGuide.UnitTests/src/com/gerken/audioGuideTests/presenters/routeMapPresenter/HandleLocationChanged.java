package com.gerken.audioGuideTests.presenters.routeMapPresenter;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.interfaces.LocationTracker;
import com.gerken.audioGuide.interfaces.MediaAssetManager;
import com.gerken.audioGuide.interfaces.OnLocationChangedListener;
import com.gerken.audioGuide.interfaces.views.RouteMapView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.objectModel.MapBounds;
import com.gerken.audioGuide.objectModel.Route;
import com.gerken.audioGuide.presenters.RouteMapPresenter;

public class HandleLocationChanged {
	
	@Test
	public void Given_NewLocationIsOnMap__Then_MapPointerUpdated() {
		final double MAP_NORTH = 26.0;
		final double MAP_WEST = 12.0;
		final double MAP_SOUTH = 22.0;
		final double MAP_EAST = 16.0;
		
		final double NEW_LATITUDE = 25.0;
		final double NEW_LONGITUDE = 13.0;
		
		final int MAP_WIDTH = 400;
		final int MAP_HEIGHT = 200;
		
		final int EXP_MAP_POINTER_X = 100;
		final int EXP_MAP_POINTER_Y = 50;
		
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
		verify(view).setLocationPointerPosition(EXP_MAP_POINTER_X, EXP_MAP_POINTER_Y);
	}
	
	private SutSetupResult setupSut(City city, RouteMapView view, LocationTracker tracker) {
		SutSetupResult result = new SutSetupResult();

		ArgumentCaptor<OnLocationChangedListener> locationChangedListenerCaptor = 
				ArgumentCaptor.forClass(OnLocationChangedListener.class);
		doNothing().when(tracker).addLocationChangedListener(locationChangedListenerCaptor.capture());
		
		
		RouteMapPresenter sut = new RouteMapPresenter(city, view, mock(MediaAssetManager.class));
		sut.setLocationTracker(tracker);
		
		result.sut = sut;
		result.locationChangedListener = locationChangedListenerCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		public RouteMapPresenter sut;
		public OnLocationChangedListener locationChangedListener;
	}
	
	private City createSingleRouteCity(int routeId, MapBounds routeMapBounds) {
		City city = new City();
		Route route = new Route(routeId, "whatever");
		route.setMapBounds(routeMapBounds);
		city.getRoutes().add(route);
		
		return city;
	}
}
