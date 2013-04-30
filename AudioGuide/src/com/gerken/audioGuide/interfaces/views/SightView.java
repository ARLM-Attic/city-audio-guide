package com.gerken.audioGuide.interfaces.views;

import com.gerken.audioGuide.graphics.DownscalableBitmap;

public interface SightView extends AudioPlayerView {
	int getWidth();
	int getHeight();
	
	void acceptNoSightInRange();
	void setInfoPanelCaptionText(String text);
	void setBackgroundImage(DownscalableBitmap bitmap) throws java.io.IOException;
	
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
