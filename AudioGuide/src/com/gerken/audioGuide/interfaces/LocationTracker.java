package com.gerken.audioGuide.interfaces;

public interface LocationTracker {
	void startTracking();
	void stopTracking();
	void addLocationChangedListener(OnLocationChangedListener listener);
}
