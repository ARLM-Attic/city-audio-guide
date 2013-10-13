package com.gerken.audioGuide;

import java.io.InputStream;
import java.util.ArrayList;

import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.views.RouteMapView;
import com.gerken.audioGuide.util.IntentExtraManager;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class RouteMapActivity extends Activity implements RouteMapView {
	private ImageView _mapImage;
	private IntentExtraManager _intentExtraManager;
	
	private ArrayList<OnEventListener> _viewInitializedListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _viewStartedListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _viewStoppedListeners = new ArrayList<OnEventListener>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_map);
		
		_mapImage = (ImageView)findViewById(R.id.mapImage);
		_intentExtraManager = new IntentExtraManager(getIntent());
		
		((GuideApplication)getApplication()).getPresenterContainer().initRouteMapPresenter(this);
		
		for(OnEventListener l : _viewInitializedListeners)
        	l.onEvent();
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	for(OnEventListener l : _viewStartedListeners)
        	l.onEvent();
    }
	
	@Override
    protected void onStop() {
    	super.onStop();
    	for(OnEventListener l : _viewStoppedListeners)
        	l.onEvent();
    }

	@Override
	public void displayMap(InputStream mapStream) throws Exception {	

		_mapImage.setImageDrawable(Drawable.createFromStream(mapStream, ""));
		mapStream.close();
		findViewById(R.id.routeMapErrorMessage).setVisibility(View.INVISIBLE);
		
		View mapContainer = findViewById(R.id.mapContainer);
		mapContainer.setMinimumWidth(_mapImage.getWidth());
		mapContainer.setMinimumHeight(_mapImage.getHeight());
		
		View mapPointerContainer = findViewById(R.id.mapPointerContainer);
		mapPointerContainer.setMinimumWidth(_mapImage.getWidth());
		mapPointerContainer.setMinimumHeight(_mapImage.getHeight());
		
		setLocationPointerPosition(100, 200);
	}	

	@Override
	public void displayError(int messageResourceId) {
		findViewById(R.id.routeMapMainView).setVisibility(View.INVISIBLE);
		
		((TextView)findViewById(R.id.routeMapErrorMessage)).setText(
				getString(messageResourceId));		
	}
	
	@Override
	public void addViewInitializedListener(OnEventListener listener) {
		_viewInitializedListeners.add(listener);
	}

	@Override
	public void addViewStartedListener(OnEventListener listener) {
		_viewStartedListeners.add(listener);
	}

	@Override
	public void addViewStoppedListener(OnEventListener listener) {
		_viewStoppedListeners.add(listener);
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
	public void setLocationPointerPosition(int x, int y) {
		View mapPointer = findViewById(R.id.mapPointerImage);
		AbsoluteLayout.LayoutParams pointerLp = (AbsoluteLayout.LayoutParams)mapPointer.getLayoutParams();
		pointerLp.x = x;
		pointerLp.y = y;
		//pointerLp.setMargins(100, 200, pointerLp.rightMargin, pointerLp.bottomMargin);
		mapPointer.setLayoutParams(pointerLp);		
	}

	@Override
	public int getRouteId() {
		return _intentExtraManager.getRouteId();
	}

}
