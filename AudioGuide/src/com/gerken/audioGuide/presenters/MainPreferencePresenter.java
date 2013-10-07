package com.gerken.audioGuide.presenters;

import java.util.ArrayList;

import com.gerken.audioGuide.interfaces.*;
import com.gerken.audioGuide.interfaces.views.MainPreferenceView;
import com.gerken.audioGuide.objectModel.*;

public class MainPreferencePresenter extends AuxiliaryPresenter {
	
	private City _city;
	private MainPreferenceView _view;
	private ApplicationSettingsStorage _prefStorage;
	
	private OnEventListener _viewInitializedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			handleViewInitialized();
		}
	};
	private OnEventListener _okButtonPressedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			handleOk();
		}
	};
	
	public MainPreferencePresenter(City city, MainPreferenceView prefView, 
			ApplicationSettingsStorage prefStorage) {
		super(prefView, city);
		_city = city;
		_view = prefView;
		_prefStorage = prefStorage;
		
		_view.addViewInitializedListener(_viewInitializedListener);
		_view.addOkButtonPressedListener(_okButtonPressedListener);
	}
	
	public void handleViewInitialized() {
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
	
	public void handleOk() {
		String routeIdString = _view.getSelectedRoute();
		if(routeIdString != null) {
			int routeId = Integer.valueOf(routeIdString);
			_prefStorage.setCurrentRouteId(routeId);
		}
		else
			_prefStorage.resetCurrentRoute();
		
		_view.finish();
	}
}
