package com.gerken.audioGuide;

import java.io.InputStream;
import java.util.ArrayList;

import com.gerken.audioGuide.containers.Point;
import com.gerken.audioGuide.interfaces.listeners.*;
import com.gerken.audioGuide.interfaces.views.RouteMapView;
import com.gerken.audioGuide.util.BundleViewStateContainer;
import com.gerken.audioGuide.util.IntentExtraManager;

import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsoluteLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class RouteMapActivity extends BasicGuideActivity implements RouteMapView {
	private View _rootView;	
	private ImageView _mapImage;
	private IntentExtraManager _intentExtraManager;	
	private Animation _mapPointerAnimation;
	private HorizontalScrollView _horizontalScrollView;
	private ScrollView _verticalScrollView;
	private ImageView _mapPointer;
	private View _mapContainer;
	private View _mapPointerContainer;
	
	private int _originalMapWidth = 0;
	private int _originalMapHeight = 0;
	private int _originalMapPointerWidth = 0;
	private int _originalMapPointerHeight = 0;
	
	private boolean _isMapZoomStarted = false;
	
	private ArrayList<OnViewStateSaveListener> _viewInstanceStateSavedListeners = new ArrayList<OnViewStateSaveListener>();
	private ArrayList<OnViewStateRestoreListener> _viewInstanceStateRestoredListeners = new ArrayList<OnViewStateRestoreListener>();
	private ArrayList<OnMultiTouchListener> _viewMultiTouchListeners = new ArrayList<OnMultiTouchListener>();
	
	private OnTouchListener _mapTouchListener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			return handleMapImageTouch(v, event);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_map);
		
		_rootView = findViewById(R.id.rootLayout);
		_mapImage = (ImageView)findViewById(R.id.mapImage);
		_horizontalScrollView = (HorizontalScrollView)findViewById(R.id.routeMapHorizontalScroller);
		_verticalScrollView = (ScrollView)findViewById(R.id.routeMapMainView);
		_mapPointer = (ImageView)findViewById(R.id.mapPointerImage);
		_mapContainer = findViewById(R.id.mapContainer);
		_mapPointerContainer = findViewById(R.id.mapPointerContainer);
		
		_intentExtraManager = new IntentExtraManager(getIntent());
		_mapPointerAnimation = createMapPointerAnimation();
		
		_mapImage.setOnTouchListener(_mapTouchListener);
		
		_originalMapPointerWidth = _mapPointer.getWidth();
		_originalMapPointerHeight = _mapPointer.getHeight();
		
		((GuideApplication)getApplication()).getPresenterContainer().initRouteMapPresenter(this);
		
		onInitialized();
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  super.onSaveInstanceState(savedInstanceState);
	  for(OnViewStateSaveListener l : _viewInstanceStateSavedListeners)
	  		l.onStateSave(new BundleViewStateContainer(savedInstanceState));
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	  	for(OnViewStateRestoreListener l : _viewInstanceStateRestoredListeners)
	  		l.onStateRestore(new BundleViewStateContainer(savedInstanceState));
	}

	@Override
	public void displayMap(InputStream mapStream) throws Exception {
		Bitmap bmp = BitmapFactory.decodeStream(mapStream);		
		mapStream.close();
		_originalMapWidth = bmp.getWidth();
		_originalMapHeight = bmp.getHeight();
		
		_mapImage.setImageDrawable(
				new BitmapDrawable(getApplicationContext().getResources(), bmp));

		findViewById(R.id.routeMapErrorMessage).setVisibility(View.INVISIBLE);
		
		_mapContainer.setMinimumWidth(_originalMapWidth);
		_mapContainer.setMinimumHeight(_originalMapHeight);
		
		_mapPointerContainer.setMinimumWidth(_originalMapWidth);
		_mapPointerContainer.setMinimumHeight(_originalMapHeight);
	}	

	@Override
	public void displayError(int messageResourceId) {
		findViewById(R.id.routeMapMainView).setVisibility(View.INVISIBLE);
		
		((TextView)findViewById(R.id.routeMapErrorMessage)).setText(
				getString(messageResourceId));		
	}
	
	@Override
	public int getOriginalMapWidth() {
		return _originalMapWidth;
	}
	@Override
	public int getOriginalMapHeight() {
		return _originalMapHeight;
	}
	
	@Override
	public int getMapWidth() {
		return _mapImage.getWidth();
	}
	@Override
	public int getMapHeight() {
		return _mapImage.getHeight();
	}
	

	@Override
	public int getOriginalMapPointerWidth() {
		return _originalMapPointerWidth;
	}
	@Override
	public int getOriginalMapPointerHeight() {
		return _originalMapPointerHeight;
	}

	@Override
	public int getScrollX() {
		return _horizontalScrollView.getScrollX();
	}
	@Override
	public int getScrollY() {
		return _verticalScrollView.getScrollY();
	}

	@Override
	public void showLocationPointerAt(int x, int y) {
		AbsoluteLayout.LayoutParams pointerLp = (AbsoluteLayout.LayoutParams)_mapPointer.getLayoutParams();
		pointerLp.x = x;
		pointerLp.y = y;
		_mapPointer.setLayoutParams(pointerLp);
		_mapPointer.setVisibility(View.VISIBLE);
		_mapPointer.startAnimation(_mapPointerAnimation);
	}

	@Override
	public void hideLocationPointer() {
		findViewById(R.id.mapPointerImage).setVisibility(View.INVISIBLE);		
	}

	@Override
	public int getRouteId() {
		return _intentExtraManager.getRouteId();
	}

	@Override
	public void scrollTo(int x, int y) {
		_horizontalScrollView.scrollTo(x, y);
		_verticalScrollView.scrollTo(x, y);
	}

	@Override
	protected View getRootView() {
		return _rootView;
	}
	
	@Override
	public void  addViewInstanceStateSavedListener(OnViewStateSaveListener listener) {
		_viewInstanceStateSavedListeners.add(listener);
	}
	@Override
	public void addViewInstanceStateRestoredListener(OnViewStateRestoreListener listener) {
		_viewInstanceStateRestoredListeners.add(listener);
	}

	@Override
	public void addViewMultiTouchListener(OnMultiTouchListener listener) {
		_viewMultiTouchListeners.add(listener);		
	}

	@Override
	public void setMapScale(float scale) {
		Matrix scaleMatrix = new Matrix();
		scaleMatrix.postScale(scale, scale, 0, 0);
		_mapImage.setImageMatrix(scaleMatrix);
	}
	
	@Override
	public void setMapSize(int width, int height) {
		setViewLayoutSize(_mapImage, width, height);
	}
	
	@Override
	public void setMapPointerContainerSize(int width, int height) {
		setViewLayoutSize(_mapPointerContainer, width, height);
		_mapContainer.setMinimumWidth(width);
		_mapContainer.setMinimumHeight(height);
	}
	
	@Override
	public void setMapPointerScale(float scale) {
		Matrix scaleMatrix = new Matrix();
		scaleMatrix.postScale(scale, scale, 0, 0);
		_mapPointer.setImageMatrix(scaleMatrix);
	}
	
	private void setViewLayoutSize(View view, int width, int height) {
		ViewGroup.LayoutParams view_lp = view.getLayoutParams();
		view_lp.width = width;
		view_lp.height = height;
		view.setLayoutParams(view_lp);
	}	

	private Animation createMapPointerAnimation() {
		Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        //anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        return anim;
	}

	private boolean handleMapImageTouch(View v, MotionEvent event) {
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return true;
		case MotionEvent.ACTION_POINTER_DOWN:
			_isMapZoomStarted = true;
			for(OnMultiTouchListener l: _viewMultiTouchListeners)
				l.onMultiTouchDown(getTouchEventPoints(event));
			return true;
		case MotionEvent.ACTION_MOVE:
			if(_isMapZoomStarted) {
				for(OnMultiTouchListener l: _viewMultiTouchListeners)
					l.onMultiTouchMove(getTouchEventPoints(event));
				return true;
			}
			else
				v.getParent().requestDisallowInterceptTouchEvent(false);
			break;
		case MotionEvent.ACTION_UP:
	    case MotionEvent.ACTION_POINTER_UP:
	    	v.getParent().requestDisallowInterceptTouchEvent(false);
	    	if(_isMapZoomStarted) {
	    		_isMapZoomStarted = false;
	    		for(OnMultiTouchListener l: _viewMultiTouchListeners)
					l.onMultiTouchUp();
	    		return true;
	    	}
	    	break;
		}
		
		return true;
	}
	
	private Point<Float>[] getTouchEventPoints(MotionEvent event) {
		Point<Float>[] res = (Point<Float>[])new Point[event.getPointerCount()];
		for(int i=0; i<event.getPointerCount(); i++)
			res[i] = new Point<Float>(event.getX(i), event.getY(i));
		return res;
	}
}
