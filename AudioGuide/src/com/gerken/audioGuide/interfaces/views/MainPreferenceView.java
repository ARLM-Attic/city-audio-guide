package com.gerken.audioGuide.interfaces.views;

import com.gerken.audioGuide.interfaces.OnEventListener;

public interface MainPreferenceView extends AuxiliaryView {
	void setRouteChoices(CharSequence[] entries, CharSequence[] entryValues);	
	void setSelectedRoute(CharSequence value);
	
	String getSelectedRoute();
	
	void addViewInitializedListener(OnEventListener listener);
	void addOkButtonPressedListener(OnEventListener listener);

	void finish();
}
