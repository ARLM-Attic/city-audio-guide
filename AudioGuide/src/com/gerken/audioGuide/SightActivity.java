package com.gerken.audioGuide;

import java.util.ArrayList;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.controls.AudioPlayerControl;
import com.gerken.audioGuide.controls.ControlUpdater;
import com.gerken.audioGuide.graphics.*;
import com.gerken.audioGuide.interfaces.BitmapContainer;
import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.services.*;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.*;

public class SightActivity extends Activity implements SightView {
	
	private View _rootView;
	private View _playerInfoPanel;
	private AudioPlayerControl _audioPlayerControl;
	
	private RouteArrowsView _nextSightPointerArrow;
	private Bitmap _backgroundBitmap;
	
	private Handler _handler;
	
	private float _playerPanelHeight = 0.0f;
	
	private ArrayList<OnEventListener> _viewInitializedListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _viewLayoutCompleteListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _viewStartedListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _viewTouchedListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _viewStoppedListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _viewDestroyedListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _viewRestartedListeners = new ArrayList<OnEventListener>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sight);
        
        _handler = new Handler();    
        
        _rootView = findViewById(R.id.rootLayout);
        _rootView.setOnClickListener(_rootViewClickListener);
       
        _nextSightPointerArrow = findControl(R.id.nextSightPointerArrow);
        _playerInfoPanel = findControl(R.id.playerInfoPanel);
        _audioPlayerControl = findControl(R.id.playerPanel);
        
        GuideApplication app = (GuideApplication)getApplication();
        app.getPresenterContainer().initSightPresenter(this, _audioPlayerControl);
        app.getPresenterContainer().initAudioPlayerPresenter(_audioPlayerControl); 
        
        _playerPanelHeight = calculatePlayerPanelHeight();
        playPlayerPanelHidingAnimation(1);
        setPlayerButtonsClickable(false);
        
        _rootView.getViewTreeObserver().addOnGlobalLayoutListener(
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

    private float calculatePlayerPanelHeight() {
        //Display display = getWindowManager().getDefaultDisplay(); 
        //int height = display.getHeight();

    	ViewGroup sightCaptionFrame = findControl(R.id.sightCaptionFrame);
        ViewGroup.LayoutParams sightCaptionFrameLp = sightCaptionFrame.getLayoutParams();
        
        ViewGroup.LayoutParams playerInfoPanelLp = (ViewGroup.LayoutParams)_playerInfoPanel.getLayoutParams();
        return playerInfoPanelLp.height-sightCaptionFrameLp.height;
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
    protected void onDestroy() {
    	super.onDestroy();
    	for(OnEventListener l : _viewDestroyedListeners)
        	l.onEvent();
    }
    
    @Override
    protected void onRestart() {
    	super.onRestart();
    	for(OnEventListener l : _viewRestartedListeners)
        	l.onEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.action_settings:
    		Intent intent = new Intent(this, MainPreferenceActivity.class);
    		//Intent intent = new Intent(this, RouteMapActivity.class);
    		startActivity(intent);
    		break;
    	case R.id.action_help:
    		showHelp();
    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    private <T> T findControl(int controlId) {
    	T control = (T)findViewById(controlId);
    	return control;
    }

	/*
	public void acceptNewSightGotInRange(String sightName, InputStream imageStream) throws Exception {
		setInfoPanelCaptionText(sightName);
        setNewBackgroundImage(imageStream);     
        _nextSightPointerArrow.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void acceptNewSightLookGotInRange(InputStream imageStream) throws Exception {
		setNewBackgroundImage(imageStream);
		_nextSightPointerArrow.setVisibility(View.INVISIBLE);
	}
	*/

	@Override
	public int getWidth() {
		return _rootView.getWidth();
	}

	@Override
	public int getHeight() {
		return _rootView.getHeight();
	}
	
	@Override
	public void resetInfoPanelCaptionText() {
		setInfoPanelCaptionText(getString(R.string.sight_info_none));
	}

	@Override
	public void acceptNewRouteSelected(String sightName, String routeName) {
		setInfoPanelCaptionText(String.format("%s: %s", sightName, routeName));		
	}
	
	@Override
	public void setBackgroundImage(BitmapContainer bitmapContainer) {
		if(_backgroundBitmap != null)
			_backgroundBitmap.recycle();
		
		_backgroundBitmap = bitmapContainer.getBitmap();
		_rootView.setBackgroundDrawable(new BitmapDrawable(_backgroundBitmap));
	}
	
	@Override
	public void setInfoPanelCaptionText(String text) {
		TextView caption = findControl(R.id.sightCaption);
        caption.setText(text);
	}	
    

	@Override
	public void displayNextSightDirection(float heading, float horizon) {
		_nextSightPointerArrow.setVector(heading, horizon);
		_nextSightPointerArrow.invalidate();
		_nextSightPointerArrow.setVisibility(View.VISIBLE);	
	}
	
	@Override
	public void hideNextSightDirection() {
		_nextSightPointerArrow.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void hidePlayerPanel() {
		_handler.post(_playerPanelHider);		
	}
	
	@Override
	public void showPlayerPanel() {
		TranslateAnimation ta = new TranslateAnimation(0, 0, _playerPanelHeight, 0);
        ta.setDuration(500);
        ta.setRepeatCount(0);
        ta.setFillAfter(true);
	    _playerInfoPanel.startAnimation(ta);	
	    setPlayerButtonsClickable(true);
	}
	
	@Override
	public void showHelp() {
		Intent helpIntent = new Intent(this, HelpActivity.class);
		startActivity(helpIntent);
	}

	@Override
	public void displayError(String message) {
		Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();	
	}
	
	@Override
	public void displayError(int messageResourceId) {
		Toast.makeText(getBaseContext(), messageResourceId, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void addViewInitializedListener(OnEventListener listener) {
		_viewInitializedListeners.add(listener);
	}
	
	@Override
	public void addViewLayoutCompleteListener(OnEventListener listener) {
		_viewLayoutCompleteListeners.add(listener);
	}

	@Override
	public void addViewStartedListener(OnEventListener listener) {
		_viewStartedListeners.add(listener);		
	}

	@Override
	public void addViewTouchedListener(OnEventListener listener) {
		_viewTouchedListeners.add(listener);		
	}
	
	@Override
	public void addViewStoppedListener(OnEventListener listener) {
		_viewStoppedListeners.add(listener);		
	}
	
	@Override
	public void addViewDestroyedListener(OnEventListener listener) {
		_viewDestroyedListeners.add(listener);		
	}
	
	@Override
	public void addViewRestartedListener(OnEventListener listener) {
		_viewRestartedListeners.add(listener);		
	}

	private void playPlayerPanelHidingAnimation(long duration) {
		TranslateAnimation ta = new TranslateAnimation(0, 0, 0, _playerPanelHeight);
        ta.setDuration(duration);
        ta.setRepeatCount(0);
        ta.setFillAfter(true);
        _playerInfoPanel.clearAnimation();
	    _playerInfoPanel.startAnimation(ta);  
	}
	
	private void setPlayerButtonsClickable(boolean clickable) {
		_audioPlayerControl.setClickable(clickable);
	}

	private OnClickListener _rootViewClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			for(OnEventListener l : _viewTouchedListeners)
	        	l.onEvent();
		}
	};
	
	private ControlUpdater<Long> _playerPanelHider = new ControlUpdater<Long>(
			new ControlUpdater.Updater<Long>() {
				@Override
				public void Update(Long param) {
					playPlayerPanelHidingAnimation(param);		
					setPlayerButtonsClickable(false);
				}
			}, 
			500L
		);
}
