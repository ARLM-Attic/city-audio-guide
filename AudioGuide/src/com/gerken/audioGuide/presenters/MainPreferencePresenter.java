package com.gerken.audioGuide.presenters;

import java.util.ArrayList;

import com.gerken.audioGuide.interfaces.*;
import com.gerken.audioGuide.interfaces.views.MainPreferenceView;
import com.gerken.audioGuide.objectModel.*;

public class MainPreferencePresenter {
	
	private City _city;
	private MainPreferenceView _view;
	private SharedPreferenceStorage _prefStorage;
	private Logger _logger;
	
	public MainPreferencePresenter(City city, MainPreferenceView prefView, 
			SharedPreferenceStorage prefStorage, Logger logger) {
		_city = city;
		_view = prefView;
		_prefStorage = prefStorage;
		_logger = logger;
	}
	
	public void init() {
		ArrayList<CharSequence> entries = new ArrayList<CharSequence>();
		ArrayList<CharSequence> entryValues = new ArrayList<CharSequence>();
		
		for(Route route : _city.getRoutes()) {
			entries.add(route.getName());
			entryValues.add(String.valueOf(route.getId()));
		}
		
		_view.setRouteChoices(entries.toArray(new CharSequence[entries.size()]), 
				entryValues.toArray(new CharSequence[entryValues.size()]));
		
		if(_prefStorage.isRouteChosen())
			_view.setSelectedRoute(String.valueOf(_prefStorage.getCurrentRouteId()));
	}
	
	public void handleOk(Object selectedRouteTag) {
		if(selectedRouteTag != null && selectedRouteTag instanceof String) {
			int routeId = Integer.valueOf((String)selectedRouteTag);
			_prefStorage.setCurrentRouteId(routeId);
		}
		else
			_prefStorage.resetCurrentRoute();
		
		_view.finish();
	}
}
