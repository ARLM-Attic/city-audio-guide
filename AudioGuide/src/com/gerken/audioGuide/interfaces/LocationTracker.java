package com.gerken.audioGuide.interfaces;

import com.gerken.audioGuide.interfaces.listeners.OnLocationChangedListener;

public interface LocationTracker {
	void startTracking();
	void stopTracking();
	void addLocationChangedListener(OnLocationChangedListener listener);
}
