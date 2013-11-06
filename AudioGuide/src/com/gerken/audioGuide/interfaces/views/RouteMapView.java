package com.gerken.audioGuide.interfaces.views;

import java.io.InputStream;

import com.gerken.audioGuide.interfaces.OnEventListener;

public interface RouteMapView extends Measurable<Integer> {
	int getRouteId();
	
	int getMapWidth();
	int getMapHeight();
	
	int getRestoredScrollX();
	int getRestoredScrollY();
	
	void showLocationPointerAt(int x, int y);
	void hideLocationPointer();
	void scrollTo(int x, int y);
	
	void displayMap(InputStream mapStream) throws Exception;
	void displayError(int messageResourceId);
	
	void addViewInitializedListener(OnEventListener listener);
	void addViewLayoutCompleteListener(OnEventListener listener);
	void addViewStartedListener(OnEventListener listener);
	void addViewStoppedListener(OnEventListener listener);
	void addViewInstanceStateRestoredListener(OnEventListener listener);
}
