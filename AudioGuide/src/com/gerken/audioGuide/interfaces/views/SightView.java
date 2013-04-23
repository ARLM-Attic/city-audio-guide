package com.gerken.audioGuide.interfaces.views;

import java.io.InputStream;

public interface SightView extends AudioPlayerView {
	void acceptNewSightGotInRange(String sightName, InputStream imageStream);
	void acceptNewSightLookGotInRange(InputStream imageStream);
	void acceptNoSightInRange();
	
	void acceptNewRouteSelected(String sightName, String routeName);
	
	void displayPlayerPlaying();
	void displayPlayerStopped();
	
	void displayNextSightDirection(float heading);
	void hideNextSightDirection();
	
	void hidePlayerPanel();
	void showPlayerPanel();
	
	void showHelp();
	
	void displayError(String message);
	void displayError(int messageResourceId);
}
