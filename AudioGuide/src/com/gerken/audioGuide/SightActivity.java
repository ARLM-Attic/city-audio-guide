package com.gerken.audioGuide;

import java.util.ArrayList;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.controls.AudioPlayerControl;
import com.gerken.audioGuide.controls.ControlUpdater;
import com.gerken.audioGuide.controls.TextViewUpdater;
import com.gerken.audioGuide.graphics.*;
import com.gerken.audioGuide.interfaces.BitmapContainer;
import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.util.IntentExtraManager;
import com.gerken.audioGuide.util.SightIntentExtraWrapper;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.*;

public class SightActivity extends BasicGuideActivity implements SightView {
	private final int ROUTE_ID_UNDEFINED = Integer.MIN_VALUE;
	private int _currentRouteId = ROUTE_ID_UNDEFINED;
	
	private View _rootView;
	private View _playerInfoPanel;
	private AudioPlayerControl _audioPlayerControl;
	private TextView _captionTextView;
	
	private RouteArrowsView _nextSightPointerArrow;
	private Bitmap _backgroundBitmap = null;
	private Bitmap _oldBackgroundBitmap = null;
	
	private Handler _handler;
	private SightIntentExtraWrapper _sightIntentExtraWrapper;
	
	private float _playerPanelHeight = 0.0f;
	private boolean _isInDemoMode;
	
	private ArrayList<OnEventListener> _viewTouchedListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _viewDestroyedListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _viewRestartedListeners = new ArrayList<OnEventListener>();
	
	private ControlUpdater<String> _captionSetter;
	private ControlUpdater<Drawable> _backgroundImageSetter;
	
	private final Object _backgroundBitmapSettingLock = new Object();
	private final Object _backgroundBitmapRecyclingLock = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sight);
        
        _handler = new Handler();    
        
        findControls();
        initControlUpdaters();
        
        _sightIntentExtraWrapper = new SightIntentExtraWrapper(getIntent());
        _isInDemoMode = _sightIntentExtraWrapper.getIsDemoMode();
        
        GuideApplication app = (GuideApplication)getApplication();
        app.getPresenterContainer().initSightPresenter(
        		this, _audioPlayerControl, _isInDemoMode);
        app.getPresenterContainer().initAudioPlayerPresenter(
        		_audioPlayerControl, _isInDemoMode); 
        
        _playerPanelHeight = calculatePlayerPanelHeight();
        playPlayerPanelHidingAnimation(1);
        setPlayerButtonsClickable(false);   
        initExitDemoButton(_isInDemoMode);
        
        onInitialized();
    }
    
    private void findControls() {
    	_rootView = findViewById(R.id.rootLayout);
        _rootView.setOnClickListener(_rootViewClickListener);
       
        _nextSightPointerArrow = findControl(R.id.nextSightPointerArrow);
        _playerInfoPanel = findControl(R.id.playerInfoPanel);
        _audioPlayerControl = findControl(R.id.playerPanel);
        _captionTextView = findControl(R.id.sightCaption);;
    }
    
    private void initControlUpdaters() {
    	_captionSetter = new ControlUpdater<String>(
			new TextViewUpdater(_captionTextView), "");
    	
    	_backgroundImageSetter = new ControlUpdater<Drawable>(
    			new ControlUpdater.Updater<Drawable>(){
    				@Override
    				public void Update(Drawable param) {
    					synchronized(_backgroundBitmapRecyclingLock) {   					
	    					_rootView.setBackgroundDrawable(param);
	    					
	    					if(_oldBackgroundBitmap != null) {
	    						_oldBackgroundBitmap.recycle();
	    						_oldBackgroundBitmap = null;
	    					}
    					}
    				}
    			}, 
    			null
    		);
    }
    
    private void initExitDemoButton(boolean isDemoMode) {
    	Button exitDemoButton = findControl(R.id.sightExitDemoButton);
    	exitDemoButton.setVisibility(isDemoMode ? View.VISIBLE : View.INVISIBLE);
    	exitDemoButton.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				finish();				
			}
		});
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
    	if(!_isInDemoMode) {
	        getMenuInflater().inflate(R.menu.main, menu);
	        return true;
    	}
    	return false;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.action_settings:
    		Intent intent = new Intent(this, MainPreferenceActivity.class);
    		startActivity(intent);
    		break;
    	case R.id.action_help:
    		showHelp();
    		break;
    	case R.id.action_route_map:
			Intent mapIntent = new Intent(this, RouteMapActivity.class);
			new IntentExtraManager(mapIntent).setRouteId(_currentRouteId);
    		startActivity(mapIntent);
    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	MenuItem mi = menu.findItem(R.id.action_route_map);
    	mi.setEnabled(_currentRouteId != ROUTE_ID_UNDEFINED); 
    	return super.onPrepareOptionsMenu(menu);
    }    

	@Override
	public void resetInfoPanelCaptionText() {
		setInfoPanelCaptionText(getString(R.string.sight_info_none));
	}

	@Override
	public void setBackgroundImage(BitmapContainer bitmapContainer) {
		synchronized(_backgroundBitmapSettingLock) {
			Bitmap newBackgroundBitmap = bitmapContainer.getBitmap();
			_oldBackgroundBitmap = _backgroundBitmap;
			_backgroundBitmap = newBackgroundBitmap;
		}
		
		_backgroundImageSetter.setStatus(new BitmapDrawable(_backgroundBitmap));
		_handler.post(_backgroundImageSetter);
	}
	
	@Override
	public void setInfoPanelCaptionText(String text) {
		boolean isCalledFromUiThread = 
				(_handler.getLooper().getThread().getId() == Thread.currentThread().getId());
		
		if(isCalledFromUiThread)
			_captionTextView.setText(text);
		else {
			_captionSetter.setStatus(text);
			_handler.post(_captionSetter);
		}
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
	public void enableRouteMapMenuItem(int routeId) {
		_currentRouteId = routeId;		
	}

	@Override
	public void disableRouteMapMenuItem() {
		_currentRouteId = ROUTE_ID_UNDEFINED;		
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
	public void addViewTouchedListener(OnEventListener listener) {
		_viewTouchedListeners.add(listener);		
	}
	
	@Override
	public void addViewDestroyedListener(OnEventListener listener) {
		_viewDestroyedListeners.add(listener);		
	}
	
	@Override
	public void addViewRestartedListener(OnEventListener listener) {
		_viewRestartedListeners.add(listener);		
	}
	
	@Override
	protected View getRootView() {
		return _rootView;
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
