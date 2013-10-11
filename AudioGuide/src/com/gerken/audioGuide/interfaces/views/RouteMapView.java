package com.gerken.audioGuide.interfaces.views;

import java.io.InputStream;

import com.gerken.audioGuide.interfaces.OnEventListener;

import android.content.Intent;

public interface RouteMapView {
	Intent getIntent();
	
	int getMapWidth();
	int getMapHeight();
	void setLocationPointerPosition(int x, int y);
	
	void displayMap(InputStream mapStream) throws Exception;
	void displayError(int messageResourceId);
	
	void addViewInitializedListener(OnEventListener listener);
	void addViewStartedListener(OnEventListener listener);
	void addViewStoppedListener(OnEventListener listener);
}
