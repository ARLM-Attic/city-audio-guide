package com.gerken.audioGuide.interfaces.views;

import com.gerken.audioGuide.interfaces.BitmapContainer;
import com.gerken.audioGuide.interfaces.listeners.OnEventListener;
import com.gerken.audioGuide.interfaces.PresenterLifetimeManager;

public interface AuxiliaryView extends Measurable<Integer>, PresenterLifetimeManager {
	void setBackgroundImage(BitmapContainer bitmap);
	
	void addViewLayoutCompleteListener(OnEventListener listener);
}
