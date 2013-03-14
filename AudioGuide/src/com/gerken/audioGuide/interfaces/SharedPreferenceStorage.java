package com.gerken.audioGuide.interfaces;

public interface SharedPreferenceStorage {
	boolean isRouteChosen();
	int getCurrentRouteId();
	void setCurrentRouteId(int id);	
	void resetCurrentRoute();
	
	int getLastSightId();
	void setLastSightId(int id);
	
	void setOnCurrentRouteChangedListener(OnEventListener listener);
}
