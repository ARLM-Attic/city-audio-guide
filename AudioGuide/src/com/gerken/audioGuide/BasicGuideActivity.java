package com.gerken.audioGuide;

import java.util.ArrayList;

import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.PresenterLifetimeManager;
import com.gerken.audioGuide.interfaces.views.Measurable;

import android.app.Activity;
import android.view.View;
import android.view.ViewTreeObserver;

public abstract class BasicGuideActivity extends Activity implements Measurable<Integer>, PresenterLifetimeManager {
	private ArrayList<OnEventListener> _viewLayoutCompleteListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _viewInitializedListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _viewStartedListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _viewStoppedListeners = new ArrayList<OnEventListener>();
	
	private Object _presenter;
	
	protected abstract View getRootView();	

	protected void onInitialized() {
		getRootView().getViewTreeObserver().addOnGlobalLayoutListener(
			    new ViewTreeObserver.OnGlobalLayoutListener() {
			    	public void onGlobalLayout() {
			    		for(OnEventListener l : _viewLayoutCompleteListeners)
			            	l.onEvent();
			    	}
			    }
		    );
		
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
	
	protected <T> T findControl(int controlId) {
    	T control = (T)findViewById(controlId);
    	return control;
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
	
	public void addViewLayoutCompleteListener(OnEventListener listener) {
		_viewLayoutCompleteListeners.add(listener);
	}

	public void addViewStartedListener(OnEventListener listener) {
		_viewStartedListeners.add(listener);
	}

	public void addViewStoppedListener(OnEventListener listener) {
		_viewStoppedListeners.add(listener);
	}	
	
	public void setPresenter(Object presenter){
		_presenter = presenter;
	}
}
