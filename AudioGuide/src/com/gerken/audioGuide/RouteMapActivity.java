package com.gerken.audioGuide;

import java.io.InputStream;
import java.util.ArrayList;

import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.views.RouteMapView;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class RouteMapActivity extends Activity implements RouteMapView {
	
	private ArrayList<OnEventListener> _viewInitializedListeners = new ArrayList<OnEventListener>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_map);
		
		((GuideApplication)getApplication()).getPresenterContainer().initRouteMapPresenter(this);
		
		for(OnEventListener l : _viewInitializedListeners)
        	l.onEvent();
	}

	@Override
	public void displayMap(InputStream mapStream) throws Exception {
		ImageView mapImage = (ImageView)findViewById(R.id.mapImage);

		mapImage.setImageDrawable(Drawable.createFromStream(mapStream, ""));
		mapStream.close();
		findViewById(R.id.routeMapErrorMessage).setVisibility(View.INVISIBLE);
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
}
