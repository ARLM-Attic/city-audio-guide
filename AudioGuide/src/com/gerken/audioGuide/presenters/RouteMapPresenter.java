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
import com.gerken.audioGuide.interfaces.ViewStateContainer;
import com.gerken.audioGuide.interfaces.listeners.OnViewStateRestoreListener;
import com.gerken.audioGuide.interfaces.listeners.OnViewStateSaveListener;
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
	private boolean _isMapPointerVisible = false;
	
	private ViewStateContainer _viewInstanceRestoringData = null;
	
	private float _multiTouchDownDistance;
	private float _originalScale = 1f;
	private float _currentScale = 1f;
	private Point<Integer> _originalScreenCenterAbsolutePosition = new Point<Integer>(0, 0);
	private Point<Float> _mapScalingCenter = new Point<Float>(0f, 0f);
	private Point<Integer> _mapPointerPosition = new Point<Integer>(0, 0);
	
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
	
	private OnViewStateSaveListener _viewInstanceStateSavedListener = new OnViewStateSaveListener() {		
		@Override
		public void onStateSave(ViewStateContainer stateContainer) {
			handleViewStateSave(stateContainer);
		}
	};
	private OnViewStateRestoreListener _viewInstanceStateRestoredListener = new OnViewStateRestoreListener() {		
		@Override
		public void onStateRestore(ViewStateContainer stateContainer) {
			_viewInstanceRestoringData = stateContainer;
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
		_view.addViewInstanceStateSavedListener(_viewInstanceStateSavedListener);
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
			String emsg = "Cannot display map for the route " + String.valueOf(getCurrentRoute().getId());
			logError(emsg, e);
			_view.displayError(R.string.route_map_cannot_read);
		}	
	}
	
	private void handleViewLayoutComplete() {
		if(_viewInstanceRestoringData != null) {
			RouteMapViewStateContainer rmc = new RouteMapViewStateContainer(_viewInstanceRestoringData);
			_view.scrollTo(rmc.getViewScrollX(), rmc.getViewScrollY());
			_currentScale = _originalScale = rmc.getScale();			
			_view.setMapScale(_originalScale);
			if(rmc.isMapPointerVisible()) {
				_view.setMapPointerScale(_originalScale);
				showLocationPointerAt(rmc.getMapPointerX(), rmc.getMapPointerY(), _originalScale);
			}
			else
				_view.hideLocationPointer();
			_viewInstanceRestoringData = null;
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
		_mapPointerPosition = new Point<Integer>(dx, dy);
		
		if(dx >= 0 && dx < _view.getMapWidth() && dy >=0 && dy < _view.getMapHeight()){
			_mapPointerPosition = new Point<Integer>(dx, dy);
			showLocationPointerAt(dx, dy, _currentScale);
			_isMapPointerVisible = true;
			if(!_isScrollingToCurrentLocationDone) {
				scrollToShowScreenCenterAt(dx, dy, _currentScale);
				_isScrollingToCurrentLocationDone = true;
			}
		}
		else {
			_view.hideLocationPointer();
			_isMapPointerVisible = false;
		}
	}
	
	private void handleMultiTouchDown(Point<Float>[] touchPoints) {
		if(touchPoints.length < 2)
			return;
		_originalScreenCenterAbsolutePosition = getScreenCenterAbsolutePosition();
		logDebug(String.format("oscroll: %d,%d - %d,%d", _view.getScrollX(), _view.getScrollY(),
				_originalScreenCenterAbsolutePosition.getX(), _originalScreenCenterAbsolutePosition.getY()));
		_multiTouchDownDistance = getDistance(touchPoints[0], touchPoints[1]);
		_mapScalingCenter = new Point<Float>(
			0.5f*(touchPoints[0].getX()+touchPoints[1].getX()), 
			0.5f*(touchPoints[0].getY()+touchPoints[1].getY())
		);
		logDebug(String.format("MultiTouchDown: center=%.1f,%.1f; dist=%.1f", 
				_mapScalingCenter.getX(), _mapScalingCenter.getY(), _multiTouchDownDistance));
	}
	
	private Point<Integer> getScreenCenterAbsolutePosition() {
		return new Point<Integer>(
			(int)(_view.getScrollX()/_currentScale)+getViewHalfWidth(), 
			(int)(_view.getScrollY()/_currentScale)+getViewHalfHeight()
		);
	}
	
	private void handleMultiTouchMove(Point<Float>[] touchPoints) {
		if(touchPoints.length < 2)
			return;
		float newDistance = getDistance(touchPoints[0], touchPoints[1]);
		float newScaleRatio = newDistance / _multiTouchDownDistance;
		float newScale = _originalScale * newScaleRatio;
		logDebug(String.format("MultiTouchMove: newScaleRatio=%.4f newScale=%.4f", newScaleRatio, newScale));
		
		int newMapWidth = (int)(((float)_view.getOriginalMapWidth())*newScale);
		int newMapHeight = (int)(((float)_view.getOriginalMapHeight())*newScale);
		boolean mapExceedsScreen = (newMapWidth > _view.getWidth() || newMapHeight > _view.getHeight());
		
		if(newScale < 1f && mapExceedsScreen) {
			_currentScale = newScale;
			_view.setMapScale(newScale);
			_view.setMapPointerScale(newScale);			
			
			_view.setMapSize(newMapWidth, newMapHeight);
			_view.setMapPointerContainerSize(newMapWidth, newMapHeight);
			
			scrollToShowScreenCenterAt(_originalScreenCenterAbsolutePosition.getX(), 
					_originalScreenCenterAbsolutePosition.getY(), newScale);			
			
			if(_isMapPointerVisible)
				showLocationPointerAt(_mapPointerPosition.getX(), _mapPointerPosition.getY(), _currentScale);
		}
	}
	
	private void handleMultiTouchUp() {
		_originalScale = _currentScale;
	}
	
	private void handleViewStateSave(ViewStateContainer stateContainer) {
		RouteMapViewStateContainer rmc = new RouteMapViewStateContainer(stateContainer);
		rmc.setViewScroll(_view.getScrollX(), _view.getScrollY());
		rmc.setScale(_currentScale);
		rmc.setMapPointerVisible(_isMapPointerVisible);
		rmc.setMapPointerPosition(_mapPointerPosition.getX(), _mapPointerPosition.getY());
	}
	
	private float getDistance(Point<Float> p0, Point<Float> p1) {
		float dx = p1.getX()-p0.getX();
		float dy = p1.getY()-p0.getY();
		return (float)Math.sqrt(dx*dx + dx*dy);
	}
	
	private void scrollToShowScreenCenterAt(int scx, int scy, float scale) {
		int sx = Math.max(0, (int)((scx- getViewHalfWidth())*scale) );
		int sy = Math.max(0, (int)((scy- getViewHalfHeight())*scale) );		
		_view.scrollTo(sx, sy);
		logDebug(String.format("nscroll: %d,%d", sx, sy));
	}
	
	private void showLocationPointerAt(int x, int y, float scale) {
		float ratio = scale/2f;
		int px = (int)(scale*x - ratio*_view.getOriginalMapPointerWidth());
		int py = (int)(scale*y - ratio*_view.getOriginalMapPointerHeight());
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
	
	private int getViewHalfWidth() {
		return _view.getWidth()/2;
	}
	private int getViewHalfHeight() {
		return _view.getHeight()/2;
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

	private class RouteMapViewStateContainer {
		private static final String KEY_SCROLL_X = "ScrollX";
		private static final String KEY_SCROLL_Y = "ScrollY";
		private static final String KEY_MAP_POINTER_X = "PointerX";
		private static final String KEY_MAP_POINTER_Y = "PointerY";
		private static final String KEY_MAP_POINTER_VISIBLE = "PointerVisible";
		private static final String KEY_SCALE = "Scale";
		
		private ViewStateContainer _container;
		
		public RouteMapViewStateContainer(ViewStateContainer genericContainer) {
			_container = genericContainer;
		}
		
		public int getViewScrollX() {
			return _container.getInt(KEY_SCROLL_X);
		}
		public int getViewScrollY() {
			return _container.getInt(KEY_SCROLL_Y);
		}
		
		public boolean isMapPointerVisible() {
			return _container.getBoolean(KEY_MAP_POINTER_VISIBLE);
		}
		public int getMapPointerX() {
			return _container.getInt(KEY_MAP_POINTER_X);
		}
		public int getMapPointerY() {
			return _container.getInt(KEY_MAP_POINTER_Y);
		}
		
		public float getScale() {
			return _container.getFloat(KEY_SCALE);
		}
		
		public void setViewScroll(int x, int y) {
			_container.putInt(KEY_SCROLL_X, x);
			_container.putInt(KEY_SCROLL_Y, y);
		}
		
		public void setMapPointerVisible(boolean isVisible) {
			_container.putBoolean(KEY_MAP_POINTER_VISIBLE, isVisible);
		}
		public void setMapPointerPosition(int x, int y) {
			_container.putInt(KEY_MAP_POINTER_X, x);
			_container.putInt(KEY_MAP_POINTER_Y, y);
		}
		
		public void setScale(float scale){
			_container.putFloat(KEY_SCALE, scale);
		}
	}
}
