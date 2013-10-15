package com.gerken.audioGuide;

import java.util.ArrayList;

import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.views.Measurable;

import android.app.Activity;
import android.view.View;

public abstract class BasicGuideActivity extends Activity implements Measurable<Integer> {
	private ArrayList<OnEventListener> _viewInitializedListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _viewStartedListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _viewStoppedListeners = new ArrayList<OnEventListener>();
	
	protected abstract View getRootView();	

	protected void onInitialized() {
		for(OnEventListener l : _viewInitializedListeners)
        	l.onEvent();
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	for(OnEventListener l : _viewStartedListeners)
        	l.onEvent();
    }
	
	@Override
    protected void onStop() {
    	super.onStop();
    	for(OnEventListener l : _viewStoppedListeners)
        	l.onEvent();
    }
	
	@Override
	public Integer getWidth() {
		return getRootView().getWidth();
	}

	@Override
	public Integer getHeight() {
		return getRootView().getHeight();
	}
	
	public void addViewInitializedListener(OnEventListener listener) {
		_viewInitializedListeners.add(listener);
	}

	public void addViewStartedListener(OnEventListener listener) {
		_viewStartedListeners.add(listener);
	}

	public void addViewStoppedListener(OnEventListener listener) {
		_viewStoppedListeners.add(listener);
	}	
}
