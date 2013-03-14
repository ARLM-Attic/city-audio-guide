package com.gerken.audioGuide.interfaces.views;

public interface MainPreferenceView {
	void setRouteChoices(CharSequence[] entries, CharSequence[] entryValues);
	void setSelectedRoute(CharSequence value);
	void finish();
}
