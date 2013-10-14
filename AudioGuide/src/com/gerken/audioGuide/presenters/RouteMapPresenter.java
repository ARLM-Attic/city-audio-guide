package com.gerken.audioGuide.presenters;

import java.io.InputStream;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.interfaces.LocationTracker;
import com.gerken.audioGuide.interfaces.MediaAssetManager;
import com.gerken.audioGuide.interfaces.Logger;
import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.OnLocationChangedListener;
import com.gerken.audioGuide.interfaces.views.RouteMapView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.objectModel.MapBounds;
import com.gerken.audioGuide.objectModel.Route;

public class RouteMapPresenter {
	private City _city; 
	private RouteMapView _view;
	private MediaAssetManager _assetStreamProvider;
	private LocationTracker _locationTracker;
	private Logger _logger;
	
	private Route _currentRoute;
	
	private OnEventListener _viewInitializedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			handleViewInitialized();
		}
	};
	private OnEventListener _viewStartedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			handleViewStarted();
		}
	};
	private OnEventListener _viewStoppedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			handleViewStopped();
		}
	};
	
	private OnLocationChangedListener _locationChangedListener = new OnLocationChangedListener() {		
		@Override
		public void onLocationChanged(double latitude, double longitude) {
			handleLocationChanged(latitude, longitude);			
		}
	};
	
	public RouteMapPresenter(City city, RouteMapView view, 
			MediaAssetManager assetStreamProvider) {
		_city = city;
		_view = view;
		_assetStreamProvider = assetStreamProvider;
		
		_view.addViewInitializedListener(_viewInitializedListener);
		_view.addViewStartedListener(_viewStartedListener);
		_view.addViewStoppedListener(_viewStoppedListener);
	}

	public void setLocationTracker(LocationTracker tracker) {
		_locationTracker = tracker;
		_locationTracker.addLocationChangedListener(_locationChangedListener);
	}
	
	public void setLogger(Logger logger) {
		_logger = logger;
	}
	
	private void handleViewInitialized() {
		int routeId = getCurrentRoute().getId();
		String assetName = String.format("rt_%d.png", routeId);		
		
		InputStream str;
		try {
			str = _assetStreamProvider.getImageAssetStream(assetName);
			_view.displayMap(str);
		} 
		catch (Exception e) {
			String emsg = String.format("Cannot display map for the route %d", routeId);
			logError(emsg, e);
			_view.displayError(R.string.route_map_cannot_read);
		}	
	}

	protected void handleViewStarted() {
		_locationTracker.startTracking();		
	}

	protected void handleViewStopped() {
		_locationTracker.stopTracking();		
	}

	private void handleLocationChanged(double latitude, double longitude) {
		Route currentRoute = getCurrentRoute();
		MapBounds bounds = currentRoute.getMapBounds();
		if(bounds == null)
			return;
		if(bounds.getEast() <= bounds.getWest())
			logWarning("East <= West bound for the route " + currentRoute.getName());
		if(bounds.getNorth() <= bounds.getSouth())
			logWarning("North <= South bound for the route " + currentRoute.getName());
		
		int dx = (int)( (double)_view.getMapWidth() * 
			(longitude - bounds.getWest()) / (bounds.getEast() - bounds.getWest()) 
		);
		int dy = (int)( (double)_view.getMapHeight() *
			(bounds.getNorth() - latitude) / (bounds.getNorth() - bounds.getSouth())
		);
		
		_view.setLocationPointerPosition(dx, dy);
	}
	
	private Route getCurrentRoute() {
		if(_currentRoute != null)
			return _currentRoute;
		
		int routeId = _view.getRouteId();
		for(Route r : _city.getRoutes()) {
			if(r.getId() == routeId)
				return r;
		}
		return null;
	}

	private void logError(String message, Throwable ex) {
		if(_logger != null)
			_logger.logError(message, ex);
	}
	
	private void logWarning(String message) {
		if(_logger != null)
			_logger.logWarning(message);
	}

}
