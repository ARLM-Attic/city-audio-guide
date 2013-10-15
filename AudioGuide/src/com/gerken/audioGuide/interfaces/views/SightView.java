package com.gerken.audioGuide.interfaces.views;

import com.gerken.audioGuide.interfaces.BitmapContainer;
import com.gerken.audioGuide.interfaces.OnEventListener;

public interface SightView extends Measurable<Integer> {
	
	void resetInfoPanelCaptionText();
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
	void addViewStartedListener(OnEventListener listener);
	void addViewLayoutCompleteListener(OnEventListener listener);
	void addViewTouchedListener(OnEventListener listener);
	void addViewStoppedListener(OnEventListener listener);
	void addViewDestroyedListener(OnEventListener listener);
	void addViewRestartedListener(OnEventListener listener);
}
