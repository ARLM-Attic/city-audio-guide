package com.gerken.audioGuide.interfaces.views;

import java.io.InputStream;

import com.gerken.audioGuide.containers.Point;
import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.OnMultiTouchListener;
import com.gerken.audioGuide.interfaces.PresenterLifetimeManager;

public interface RouteMapView extends Measurable<Integer>, PresenterLifetimeManager {
	int getRouteId();
	
	int getOriginalMapWidth();
	int getOriginalMapHeight();
	int getMapWidth();
	int getMapHeight();
	int getOriginalMapPointerWidth();
	int getOriginalMapPointerHeight();
	
	int getRestoredScrollX();
	int getRestoredScrollY();
	int getRestoredPointerX();
	int getRestoredPointerY();
	boolean isRestoredPointerVisible();
	
	int getScrollX();
	int getScrollY();	
	
	void showLocationPointerAt(int x, int y);
	void hideLocationPointer();
	void scrollTo(int x, int y);
	void setMapScale(float scale);
	void setMapPointerScale(float scale);
	void setMapSize(int width, int height);
	void setMapPointerContainerSize(int width, int height);
	void setMapPointerSize(int width, int height);
	
	void displayMap(InputStream mapStream) throws Exception;
	void displayError(int messageResourceId);
	
	void addViewInitializedListener(OnEventListener listener);
	void addViewLayoutCompleteListener(OnEventListener listener);
	void addViewStartedListener(OnEventListener listener);
	void addViewStoppedListener(OnEventListener listener);
	void addViewInstanceStateRestoredListener(OnEventListener listener);
	void addViewMultiTouchListener(OnMultiTouchListener listener);
}
