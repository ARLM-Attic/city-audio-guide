package com.gerken.audioGuide;

import java.io.InputStream;
import java.util.ArrayList;

import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.views.RouteMapView;
import com.gerken.audioGuide.util.IntentExtraManager;

import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsoluteLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class RouteMapActivity extends BasicGuideActivity implements RouteMapView {
	private static final String KEY_SCROLL_X = "ScrollX";
	private static final String KEY_SCROLL_Y = "ScrollY";
	private static final String KEY_POINTER_X = "PointerX";
	private static final String KEY_POINTER_Y = "PointerY";
	private static final String KEY_POINTER_VISIBLE = "PointerVisible";
	
	private View _rootView;	
	private ImageView _mapImage;
	private IntentExtraManager _intentExtraManager;	
	private Animation _mapPointerAnimation;
	private HorizontalScrollView _horizontalScrollView;
	private ScrollView _verticalScrollView;
	private View _mapPointer;
	
	private int _restoredScrollX = 0;
	private int _restoredScrollY = 0;
	private int _restoredPointerX = 0;
	private int _restoredPointerY = 0;
	private boolean _restoredPointerVisible = false;
	
	private ArrayList<OnEventListener> _viewInstanceStateRestoredListeners = new ArrayList<OnEventListener>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_map);
		
		_rootView = findViewById(R.id.rootLayout);
		_mapImage = (ImageView)findViewById(R.id.mapImage);
		_horizontalScrollView = (HorizontalScrollView)findViewById(R.id.routeMapHorizontalScroller);
		_verticalScrollView = (ScrollView)findViewById(R.id.routeMapMainView);
		_mapPointer = findViewById(R.id.mapPointerImage);;
		
		_intentExtraManager = new IntentExtraManager(getIntent());
		_mapPointerAnimation = createMapPointerAnimation();		
		
		((GuideApplication)getApplication()).getPresenterContainer().initRouteMapPresenter(this);
		
		onInitialized();
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  super.onSaveInstanceState(savedInstanceState);
	  savedInstanceState.putInt(KEY_SCROLL_X, _horizontalScrollView.getScrollX());
	  savedInstanceState.putInt(KEY_SCROLL_Y, _verticalScrollView.getScrollY());
	  
	  AbsoluteLayout.LayoutParams pointerLp = (AbsoluteLayout.LayoutParams)_mapPointer.getLayoutParams();
	  savedInstanceState.putInt(KEY_POINTER_X, pointerLp.x);
	  savedInstanceState.putInt(KEY_POINTER_Y, pointerLp.y);
	  savedInstanceState.putBoolean(KEY_POINTER_VISIBLE, 
			  (_mapPointer.getVisibility() == View.VISIBLE));
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  _restoredScrollX = savedInstanceState.getInt(KEY_SCROLL_X);
	  _restoredScrollY = savedInstanceState.getInt(KEY_SCROLL_Y);
	  _restoredPointerX = savedInstanceState.getInt(KEY_POINTER_X);
	  _restoredPointerY = savedInstanceState.getInt(KEY_POINTER_Y);
	  _restoredPointerVisible = savedInstanceState.getBoolean(KEY_POINTER_VISIBLE);
	  
	  for(OnEventListener l : _viewInstanceStateRestoredListeners)
      	l.onEvent();	  
	}

	@Override
	public void displayMap(InputStream mapStream) throws Exception {
		Bitmap bmp = BitmapFactory.decodeStream(mapStream);		
		mapStream.close();
		int mapWidth = bmp.getWidth();
		int mapHeight = bmp.getHeight();
		
		_mapImage.setImageDrawable(
				new BitmapDrawable(getApplicationContext().getResources(), bmp));

		findViewById(R.id.routeMapErrorMessage).setVisibility(View.INVISIBLE);
		
		View mapContainer = findViewById(R.id.mapContainer);
		mapContainer.setMinimumWidth(mapWidth);
		mapContainer.setMinimumHeight(mapHeight);
		
		View mapPointerContainer = findViewById(R.id.mapPointerContainer);
		mapPointerContainer.setMinimumWidth(mapWidth);
		mapPointerContainer.setMinimumHeight(mapHeight);
	}	

	@Override
	public void displayError(int messageResourceId) {
		findViewById(R.id.routeMapMainView).setVisibility(View.INVISIBLE);
		
		((TextView)findViewById(R.id.routeMapErrorMessage)).setText(
				getString(messageResourceId));		
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
	public int getPointerWidth() {
		return _mapPointer.getWidth();
	}
	@Override
	public int getPointerHeight() {
		return _mapPointer.getHeight();
	}

	@Override
	public int getRestoredPointerX() {
		return _restoredPointerX;
	}

	@Override
	public int getRestoredPointerY() {
		return _restoredPointerY;
	}
	@Override
	public boolean isRestoredPointerVisible() {
		return _restoredPointerVisible;
	}
	
	@Override
	public int getRestoredScrollX() {
		return _restoredScrollX;
	}
	@Override
	public int getRestoredScrollY() {
		return _restoredScrollY;
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
	public void addViewInstanceStateRestoredListener(OnEventListener listener) {
		_viewInstanceStateRestoredListeners.add(listener);
	}

	private Animation createMapPointerAnimation() {
		Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        //anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        return anim;
	}

}
