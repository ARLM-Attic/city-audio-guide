package com.gerken.audioGuide.presenters;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.interfaces.MediaAssetManager;
import com.gerken.audioGuide.interfaces.Logger;
import com.gerken.audioGuide.interfaces.views.RouteMapView;
import com.gerken.audioGuide.util.IntentExtraManager;

public class RouteMapPresenter {
	
	private RouteMapView _view;
	private MediaAssetManager _assetStreamProvider;
	private Logger _logger;
	
	public RouteMapPresenter(RouteMapView view, 
			MediaAssetManager assetStreamProvider, Logger logger) {
		_view = view;
		_assetStreamProvider = assetStreamProvider;
		_logger = logger;
	}
	
	public void init() {
		int routeId = new IntentExtraManager(_view.getIntent()).getRouteId();
		String assetName = String.format("rt_%d.png", routeId);		
		
		InputStream str;
		try {
			str = _assetStreamProvider.getImageAssetStream(assetName);
			_view.displayMap(str);
		} 
		catch (Exception e) {
			String emsg = String.format("Cannot display map for the route %d", routeId);
			_logger.logError(emsg, e);
			_view.displayError(R.string.route_map_cannot_read);
		}	
	}

}
