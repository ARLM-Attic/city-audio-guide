package com.gerken.audioGuide.interfaces.views;

import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.PresenterLifetimeManager;

public interface MainPreferenceView extends AuxiliaryView, PresenterLifetimeManager {
	void setRouteChoices(CharSequence[] entries, CharSequence[] entryValues);	
	void setSelectedRoute(CharSequence value);
	
	String getSelectedRoute();
	
	void addViewInitializedListener(OnEventListener listener);
	void addOkButtonPressedListener(OnEventListener listener);

	void finish();
}
