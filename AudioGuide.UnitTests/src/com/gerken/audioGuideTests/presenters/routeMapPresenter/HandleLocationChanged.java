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
		final double MAP_NORTH = 22.0;
		final double MAP_WEST = 14.0;
		final double MAP_SOUTH = 28.0;
		final double MAP_EAST = 16.0;
		
		final double NEW_LATITUDE = 15.0;
		final double NEW_LONGITUDE = 26.0;
		
		final int MAP_WIDTH = 300;
		final int MAP_HEIGHT = 200;
		
		final int EXP_MAP_POINTER_X = 150;
		final int EXP_MAP_POINTER_Y = 100;
		
		MapBounds bounds = new MapBounds(MAP_NORTH, MAP_WEST, MAP_SOUTH, MAP_EAST);
		City city = createSingleRouteCity(bounds);
		
		RouteMapView view = mock(RouteMapView.class);
		when(view.getMapHeight()).thenReturn(MAP_HEIGHT);
		when(view.getMapWidth()).thenReturn(MAP_WIDTH);
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
		
		result.sut = sut;
		result.locationChangedListener = locationChangedListenerCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		public RouteMapPresenter sut;
		public OnLocationChangedListener locationChangedListener;
	}
	
	private City createSingleRouteCity(MapBounds routeMapBounds) {
		City city = new City();
		Route route = new Route();
		route.setMapBounds(routeMapBounds);
		city.getRoutes().add(route);
		
		return city;
	}
}
