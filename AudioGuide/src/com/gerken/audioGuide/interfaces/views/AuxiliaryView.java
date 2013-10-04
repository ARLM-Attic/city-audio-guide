package com.gerken.audioGuide.interfaces.views;

import com.gerken.audioGuide.interfaces.BitmapContainer;
import com.gerken.audioGuide.interfaces.OnEventListener;

public interface AuxiliaryView extends Measurable<Integer> {
	void setBackgroundImage(BitmapContainer bitmap);
	
	void addViewLayoutCompleteListener(OnEventListener listener);
}
