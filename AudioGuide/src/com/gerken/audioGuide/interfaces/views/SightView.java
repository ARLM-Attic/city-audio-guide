package com.gerken.audioGuide.interfaces.views;

import java.io.InputStream;

public interface SightView extends AudioPlayerView {
	void acceptNewSightGotInRange(String sightName, InputStream imageStream) throws Exception;
	void acceptNewSightLookGotInRange(InputStream imageStream) throws Exception;
	void acceptNoSightInRange();
	void setInfoPanelCaptionText(String text);
	
	void acceptNewRouteSelected(String sightName, String routeName);
	
	void displayPlayerPlaying();
	void displayPlayerStopped();
	
	void displayNextSightDirection(float heading, float horizon);
	void hideNextSightDirection();
	
	void hidePlayerPanel();
	void showPlayerPanel();
	
	void showHelp();
	
	void displayError(String message);
	void displayError(int messageResourceId);
}
