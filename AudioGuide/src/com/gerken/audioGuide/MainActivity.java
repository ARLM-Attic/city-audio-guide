package com.gerken.audioGuide;

import java.io.IOException;
import java.util.ArrayList;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.controls.AudioPlayerControl;
import com.gerken.audioGuide.controls.ControlUpdater;
import com.gerken.audioGuide.graphics.*;
import com.gerken.audioGuide.interfaces.AudioPlayer;
import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.presenters.AudioPlayerPresenter;
import com.gerken.audioGuide.presenters.SightPresenter;
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

public class MainActivity extends Activity implements SightView {
	
	private View _rootView;
	private View _playerInfoPanel;
	private AudioPlayerControl _audioPlayerControl;
	
	private RouteArrowsView _nextSightPointerArrow;
	
	private SightPresenter _presenter;
	private AudioPlayerPresenter _audioPlayerPresenter;
	private AndroidLocationManagerFacade _locationManager;
	private SightLookFinderByLocation _sightLookFinderByLocation;
	
	
	private Bitmap _backgroundBitmap;
	
	private Handler _handler;
	
	private float _playerPanelHeight = 0.0f;
	
	private ArrayList<OnEventListener> _viewInitializedListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _viewTouchedListeners = new ArrayList<OnEventListener>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        _handler = new Handler();    
        
        _rootView = findViewById(R.id.rootLayout);
        _rootView.setOnClickListener(_rootViewClickListener);
       
        _nextSightPointerArrow = findControl(R.id.nextSightPointerArrow);
        _playerInfoPanel = findControl(R.id.playerInfoPanel);
        _audioPlayerControl = findControl(R.id.playerPanel);
        
        setupDependencies();
        
        _playerPanelHeight = calculatePlayerPanelHeight();
        playPlayerPanelHidingAnimation(1);
        setPlayerButtonsClickable(false);
        
        for(OnEventListener l : _viewInitializedListeners)
        	l.onEvent();
    }
    
    private void setupDependencies() {
    	Context ctx = getApplicationContext();
        AudioPlayer player = new AndroidMediaPlayerFacade(ctx);
        _locationManager = new AndroidLocationManagerFacade(ctx);  
        
        _sightLookFinderByLocation = new SightLookFinderByLocation(
        		((GuideApplication)getApplication()).getCity(), _locationManager);
        
        _presenter = new SightPresenter(this, _audioPlayerControl, player);
        _presenter.setAssetStreamProvider(new GuideAssetManager(ctx));
        _presenter.setApplicationSettingsStorage(new SharedPreferenceManager(ctx));
        _presenter.setNewSightLookGotInRangeRaiser(_sightLookFinderByLocation);
        _presenter.setDownscalableBitmapCreator(new DownscalableBitmapFactory());
        _presenter.setLogger(new DefaultLoggingAdapter("SightPresenter"));
        
        _audioPlayerPresenter = new AudioPlayerPresenter(
        		_audioPlayerControl, player);
        _audioPlayerPresenter.setLogger(new DefaultLoggingAdapter("AudioPlayerPresenter"));
        _audioPlayerPresenter.setAudioUpdateScheduler(new SchedulerService());
        _audioPlayerPresenter.setAudioRewindScheduler(new SchedulerService());
        _audioPlayerPresenter.setNewSightLookGotInRangeRaiser(_sightLookFinderByLocation);
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
    protected void onResume() {
    	super.onResume();
    	_locationManager.startTracking();
    	_presenter.handleActivityResume();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	_locationManager.stopTracking();
    	_presenter.handleActivityPause();
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
	public void acceptNoSightInRange() {
		setInfoPanelCaptionText(getString(R.string.sight_info_none));
		_rootView.setBackgroundResource(R.drawable.prague_silhouette);
		_nextSightPointerArrow.setVisibility(View.INVISIBLE);
	}

	@Override
	public void acceptNewRouteSelected(String sightName, String routeName) {
		setInfoPanelCaptionText(String.format("%s: %s", sightName, routeName));		
	}
	
	@Override
	public void setBackgroundImage(DownscalableBitmap bitmap) throws IOException {
		if(_backgroundBitmap != null)
			_backgroundBitmap.recycle();
		
		_backgroundBitmap = bitmap.getFinalBitmap();
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
	public void addViewTouchedListener(OnEventListener listener) {
		_viewTouchedListeners.add(listener);		
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
