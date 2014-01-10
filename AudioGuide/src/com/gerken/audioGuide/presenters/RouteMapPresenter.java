package com.gerken.audioGuide.presenters;

import java.io.InputStream;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.containers.Point;
import com.gerken.audioGuide.interfaces.LocationTracker;
import com.gerken.audioGuide.interfaces.MediaAssetManager;
import com.gerken.audioGuide.interfaces.Logger;
import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.OnLocationChangedListener;
import com.gerken.audioGuide.interfaces.OnMultiTouchListener;
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
	private boolean _isScrollingToCurrentLocationDone = false;
	private boolean _shouldHandleRestoringInstance = false;
	
	private float _multiTouchDownDistance;
	private float _originalScale = 1f;
	private float _currentScale = 1f;
	private Point<Integer> _originalScrollPosition = new Point<Integer>(0, 0);
	private Point<Float> _mapScalingCenter = new Point<Float>(0f, 0f);
	
	private OnEventListener _viewInitializedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			handleViewInitialized();
		}
	};
	private OnEventListener _viewLayoutCompleteListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			handleViewLayoutComplete();
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
	private OnEventListener _viewInstanceStateRestoredListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			_shouldHandleRestoringInstance = true;
		}
	};
	
	private OnLocationChangedListener _locationChangedListener = new OnLocationChangedListener() {		
		@Override
		public void onLocationChanged(double latitude, double longitude) {
			handleLocationChanged(latitude, longitude);			
		}
	};
	
	private OnMultiTouchListener _multiTouchListener = new OnMultiTouchListener() {
		public void onMultiTouchDown(Point<Float>[] touchPoints) {
			handleMultiTouchDown(touchPoints);			
		}
		public void onMultiTouchMove(Point<Float>[] touchPoints) {
			handleMultiTouchMove(touchPoints);
		}
		public void onMultiTouchUp() {
			handleMultiTouchUp();
		}
	};
	
	public RouteMapPresenter(City city, RouteMapView view, 
			MediaAssetManager assetStreamProvider) {
		_city = city;
		_view = view;
		_assetStreamProvider = assetStreamProvider;
		
		_view.addViewInitializedListener(_viewInitializedListener);
		_view.addViewLayoutCompleteListener(_viewLayoutCompleteListener);
		_view.addViewStartedListener(_viewStartedListener);
		_view.addViewStoppedListener(_viewStoppedListener);
		_view.addViewInstanceStateRestoredListener(_viewInstanceStateRestoredListener);
		_view.addViewMultiTouchListener(_multiTouchListener);
	}

	public void setLocationTracker(LocationTracker tracker) {
		_locationTracker = tracker;
		_locationTracker.addLocationChangedListener(_locationChangedListener);
	}
	
	public void setLogger(Logger logger) {
		_logger = logger;
	}
	
	private void handleViewInitialized() {
		InputStream str;
		try {
			str = _assetStreamProvider.getImageAssetStream(getCurrentRoute().getImageName());
			_view.displayMap(str);
		} 
		catch (Exception e) {
			String emsg = String.format("Cannot display map for the route %d", getCurrentRoute().getId());
			logError(emsg, e);
			_view.displayError(R.string.route_map_cannot_read);
		}	
	}
	
	private void handleViewLayoutComplete() {
		if(_shouldHandleRestoringInstance) {
			_view.scrollTo(_view.getRestoredScrollX(), _view.getRestoredScrollY());
			if(_view.isRestoredPointerVisible())
				_view.showLocationPointerAt(_view.getRestoredPointerX(), _view.getRestoredPointerY());
			else
				_view.hideLocationPointer();
			_shouldHandleRestoringInstance = false;
		}
	}

	protected void handleViewStarted() {
		_locationTracker.startTracking();
		_isScrollingToCurrentLocationDone = false;
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
		
		if(dx >= 0 && dx < _view.getMapWidth() && dy >=0 && dy < _view.getMapHeight()){
			showLocationPointerAt(dx, dy);
			if(!_isScrollingToCurrentLocationDone) {
				scrollTo(dx, dy);
				_isScrollingToCurrentLocationDone = true;
			}
		}
		else
			_view.hideLocationPointer();
	}
	
	private void handleMultiTouchDown(Point<Float>[] touchPoints) {
		if(touchPoints.length < 2)
			return;
		_originalScrollPosition = new Point<Integer>(_view.getScrollX(), _view.getScrollY());
		logDebug(String.format("oscroll: %d,%d", _view.getScrollX(), _view.getScrollY()));
		_multiTouchDownDistance = getDistance(touchPoints[0], touchPoints[1]);
		_mapScalingCenter = new Point<Float>(
			0.5f*(touchPoints[0].getX()+touchPoints[1].getX()), 
			0.5f*(touchPoints[0].getY()+touchPoints[1].getY())
		);
		logDebug(String.format("MultiTouchDown: center=%.1f,%.1f; dist=%.1f", 
				_mapScalingCenter.getX(), _mapScalingCenter.getY(), _multiTouchDownDistance));
	}
	
	private void handleMultiTouchMove(Point<Float>[] touchPoints) {
		if(touchPoints.length < 2)
			return;
		float newDistance = getDistance(touchPoints[0], touchPoints[1]);
		float newScaleRatio = newDistance / _multiTouchDownDistance;
		float newScale = _originalScale * newScaleRatio;
		logDebug(String.format("MultiTouchMove: newScaleRatio=%.4f newScale=%.4f", newScaleRatio, newScale));
		if(newScale < 1f) {
			_currentScale = newScale;
			_view.setMapScale(newScale);
			
			int newMapWidth = (int)(((float)_view.getOriginalMapWidth())*newScale);
			int newMapHeight = (int)(((float)_view.getOriginalMapHeight())*newScale);
			_view.setMapSize(newMapWidth, newMapHeight);
			_view.setMapPointerContainerSize(newMapWidth, newMapHeight);
			
			float hw = _view.getWidth()/2f;
			float hh = _view.getHeight()/2f;
			int sx = (int)(((float)_originalScrollPosition.getX()+hw)*newScale - hw);
			int sy = (int)(((float)_originalScrollPosition.getY()+hh)*newScale - hh);
			_view.scrollTo(sx, sy);
			logDebug(String.format("nscroll: %d,%d", sx, sy));
		}
	}
	
	private void handleMultiTouchUp() {
		_originalScale = _currentScale;
	}
	
	private float getDistance(Point<Float> p0, Point<Float> p1) {
		float dx = p1.getX()-p0.getX();
		float dy = p1.getY()-p0.getY();
		return (float)Math.sqrt(dx*dx + dx*dy);
	}
	
	private void scrollTo(int dx, int dy) {
		int sx = Math.max(0, dx - _view.getWidth()/2);
		int sy = Math.max(0, dy - _view.getHeight()/2);
		_view.scrollTo(sx, sy);
	}
	
	private void showLocationPointerAt(int x, int y) {
		int px = x - (int)(_view.getPointerWidth()/2);
		int py = y - (int)(_view.getPointerHeight()/2);
		_view.showLocationPointerAt(px, py);
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
	private void logDebug(String message) {
		if(_logger != null)
			_logger.logDebug(message);
	}

}
