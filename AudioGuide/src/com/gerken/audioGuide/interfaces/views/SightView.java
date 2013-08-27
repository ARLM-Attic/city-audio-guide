package com.gerken.audioGuide.interfaces.views;

import com.gerken.audioGuide.interfaces.BitmapContainer;
import com.gerken.audioGuide.interfaces.OnEventListener;

public interface SightView {
	int getWidth();
	int getHeight();
	
	void acceptNoSightInRange();
	void setInfoPanelCaptionText(String text);
	void setBackgroundImage(BitmapContainer bitmap);
	
	void acceptNewRouteSelected(String sightName, String routeName);

	void displayNextSightDirection(float heading, float horizon);
	void hideNextSightDirection();
	
	void hidePlayerPanel();
	void showPlayerPanel();
	
	void showHelp();
	
	void displayError(String message);
	void displayError(int messageResourceId);
	
	void addViewInitializedListener(OnEventListener listener);
	void addViewTouchedListener(OnEventListener listener);
	void addViewStoppedListener(OnEventListener listener);
	void addViewDestroyedListener(OnEventListener listener);
	void addViewRestartedListener(OnEventListener listener);
}
