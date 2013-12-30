package com.gerken.audioGuide.interfaces.views;

import java.io.InputStream;

import com.gerken.audioGuide.containers.Point;
import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.OnMultiTouchListener;
import com.gerken.audioGuide.interfaces.PresenterLifetimeManager;

public interface RouteMapView extends Measurable<Integer>, PresenterLifetimeManager {
	int getRouteId();
	
	int getMapWidth();
	int getMapHeight();
	int getPointerWidth();
	int getPointerHeight();
	
	int getRestoredScrollX();
	int getRestoredScrollY();
	int getRestoredPointerX();
	int getRestoredPointerY();
	boolean isRestoredPointerVisible();
	
	void showLocationPointerAt(int x, int y);
	void hideLocationPointer();
	void scrollTo(int x, int y);
	void setMapScale(float scale, Point<Float> scalingCenter);
	
	void displayMap(InputStream mapStream) throws Exception;
	void displayError(int messageResourceId);
	
	void addViewInitializedListener(OnEventListener listener);
	void addViewLayoutCompleteListener(OnEventListener listener);
	void addViewStartedListener(OnEventListener listener);
	void addViewStoppedListener(OnEventListener listener);
	void addViewInstanceStateRestoredListener(OnEventListener listener);
	void addViewMultiTouchListener(OnMultiTouchListener listener);
}
