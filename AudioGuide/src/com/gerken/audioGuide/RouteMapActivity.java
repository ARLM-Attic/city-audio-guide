package com.gerken.audioGuide;

import java.io.InputStream;

import com.gerken.audioGuide.interfaces.views.RouteMapView;
import com.gerken.audioGuide.presenters.RouteMapPresenter;
import com.gerken.audioGuide.services.AndroidMediaAssetManager;
import com.gerken.audioGuide.services.DefaultLoggingAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class RouteMapActivity extends Activity implements RouteMapView {
	
	private RouteMapPresenter _presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_map);
		
		_presenter = new RouteMapPresenter(this, 
				new AndroidMediaAssetManager(getApplicationContext()), 
				new DefaultLoggingAdapter("RouteMapPresenter"));
		
		_presenter.init();	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_map, menu);
		return true;
	}

	@Override
	public void displayMap(InputStream mapStream) throws Exception {
		ImageView mapImage = (ImageView)findViewById(R.id.mapImage);

		mapImage.setImageDrawable(Drawable.createFromStream(mapStream, ""));
		mapStream.close();
		findViewById(R.id.routeMapErrorMessage).setVisibility(View.INVISIBLE);
		
		Log.d("RouteMapActivity", String.format("img: %d %d", mapImage.getWidth(), mapImage.getHeight()));
		Log.d("RouteMapActivity", String.format("drw: %d %d", 
				mapImage.getDrawable().getMinimumWidth(), mapImage.getDrawable().getMinimumHeight()));
	}

	@Override
	public void displayError(int messageResourceId) {
		findViewById(R.id.routeMapMainView).setVisibility(View.INVISIBLE);
		
		((TextView)findViewById(R.id.routeMapErrorMessage)).setText(
				getString(messageResourceId));		
	}

}
