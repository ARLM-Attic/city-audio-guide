package com.gerken.audioGuide.interfaces;

import com.gerken.audioGuide.interfaces.listeners.OnEventListener;

public interface ApplicationSettingsStorage {
	boolean isRouteChosen();
	int getCurrentRouteId();
	void setCurrentRouteId(int id);	
	void resetCurrentRoute();
	
	int getLastSightId();
	void setLastSightId(int id);
	
	boolean showHelpAtStartup();
	void setShowHelpAtStartup(boolean show);
	
	void setOnCurrentRouteChangedListener(OnEventListener listener);
}
