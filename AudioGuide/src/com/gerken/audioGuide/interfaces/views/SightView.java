package com.gerken.audioGuide.interfaces.views;

import java.io.InputStream;

public interface SightView {
	void acceptNewSightGotInRange(String sightName, InputStream imageStream);
	void acceptNewSightLookGotInRange(InputStream imageStream);
	
	void acceptNewRouteSelected(String sightName, String routeName);
	
	void displayPlayerPlaying();
	void displayPlayerStopped();
	
	void setAudioProgressMaximum(int ms);
	void setAudioProgressPosition(int ms);
	void setAudioDuration(String formattedDuration);
	void setAudioPosition(String formattedPosition);
	
	void displayNextSightDirection(float heading);
	
	void hidePlayerPanel();
	void showPlayerPanel();
	
	void displayError(String message);
	void displayError(int messageResourceId);
}
