package com.gerken.audioGuide.interfaces.views;

import java.io.InputStream;

import com.gerken.audioGuide.interfaces.OnEventListener;

import android.content.Intent;

public interface RouteMapView {
	Intent getIntent();
	void displayMap(InputStream mapStream) throws Exception;
	void displayError(int messageResourceId);
	
	void addViewInitializedListener(OnEventListener listener);
}
