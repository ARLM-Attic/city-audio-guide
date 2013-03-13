package com.gerken.audioGuide.interfaces.views;

import java.io.InputStream;

import android.content.Intent;

public interface RouteMapView {
	Intent getIntent();
	void displayMap(InputStream mapStream) throws Exception;
	void displayError(int messageResourceId);
}
