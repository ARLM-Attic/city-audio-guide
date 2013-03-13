package com.gerken.audioGuide.interfaces;

public interface SharedPreferenceStorage {
	boolean isRouteChosen();
	int getCurrentRouteId();
	void setCurrentRouteId(int id);	
	
	int getLastSightId();
	void setLastSightId(int id);
}
