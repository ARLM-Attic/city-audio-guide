package com.gerken.audioGuide.presenters;

import java.util.ArrayList;

import com.gerken.audioGuide.interfaces.*;
import com.gerken.audioGuide.objectModel.*;

public class MainPreferencePresenter {
	
	private City _city;
	private MainPreferenceView _prefView;
	private SharedPreferenceStorage _prefStorage;
	private Logger _logger;
	
	public MainPreferencePresenter(City city, MainPreferenceView prefView, 
			SharedPreferenceStorage prefStorage, Logger logger) {
		_city = city;
		_prefView = prefView;
		_prefStorage = prefStorage;
		_logger = logger;
	}
	
	public void init() {
		ArrayList<CharSequence> entries = new ArrayList<CharSequence>();
		ArrayList<CharSequence> entryValues = new ArrayList<CharSequence>();
		
		entries.add("None");
		entryValues.add("");
		for(Route route : _city.getRoutes()) {
			entries.add(route.getName());
			entryValues.add(String.valueOf(route.getId()));
		}
		
		_prefView.setRouteChoices(entries.toArray(new CharSequence[entries.size()]), 
				entryValues.toArray(new CharSequence[entryValues.size()]));
	}

}
