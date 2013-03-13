package com.gerken.audioGuide.interfaces.views;

import java.io.InputStream;

public interface SightView {
	void acceptNewSightGotInRange(String sightName, InputStream imageStream);
	void acceptNewSightLookGotInRange(InputStream imageStream);
	
	void displayPlayerPlaying();
	void displayPlayerStopped();
	
	void displayError(String message);
	void displayError(int messageResourceId);
}
