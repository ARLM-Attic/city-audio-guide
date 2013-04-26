package com.gerken.audioGuide;

import java.io.InputStream;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.controls.ControlUpdater;
import com.gerken.audioGuide.graphics.*;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.presenters.SightPresenter;
import com.gerken.audioGuide.services.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.TranslateAnimation;
import android.widget.*;

public class MainActivity extends Activity implements SightView {
	
	private View _rootView;
	private View _playerInfoPanel;
	private View _playerPanel;
	private ImageButton _playButton;
	private ImageButton _stopButton;
	private ImageButton _rewindButton;
	private ProgressBar _audioProgressBar;
	private TextView _audioDuration;
	private TextView _audioPlayed;
	
	private RouteArrowsView _nextSightPointerArrow;
	
	private SightPresenter _presenter;
	private LocationManagerFacade _locationManager;
	
	private PlayerButtonDrawableFactory _buttonDrawableFactory = 
			new PlayerButtonDrawableFactory();
	
	private Drawable _playButtonDefaultDrawable;
	private Drawable _playButtonPressedDrawable;
	private Drawable _stopButtonDefaultDrawable;
	private Drawable _rewindButtonDefaultDrawable;
	
	private Handler _handler;
	
	private float _playerPanelHeight = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        _handler = new Handler();
        
        Context ctx = getApplicationContext();
        _presenter = new SightPresenter(
        		((GuideApplication)getApplication()).getCity(), 
        		this, new GuideAssetManager(ctx),
        		new AndroidMediaPlayerFacade(ctx),
        		new SharedPreferenceManager(ctx),
        		new DefaultLoggingAdapter("SightPresenter"));
        
        _locationManager = new LocationManagerFacade(ctx, _locationListener);       
        
        _rootView = findViewById(R.id.rootLayout);
        _rootView.setOnClickListener(_rootViewClickListener);
                
        initPlayButton();
        initStopButton();
        initRewindButton();        
        
        _audioProgressBar = findControl(R.id.audioProgressBar);
        _audioDuration = findControl(R.id.audioDuration);
        _audioPlayed = findControl(R.id.audioPlayed);
        
        _nextSightPointerArrow = findControl(R.id.nextSightPointerArrow);
        _playerPanel = findControl(R.id.playerPanel);
        _playerInfoPanel = findControl(R.id.playerInfoPanel);
        
        _playerPanelHeight = calculatePlayerPanelHeight();
        playPlayerPanelHidingAnimation(1);
        setPlayerButtonsClickable(false);
        
        _presenter.handleViewInit();
    }
    
    private void initPlayButton() {
    	_playButton = findControl(R.id.playButton);
        ViewGroup.LayoutParams lp = _playButton.getLayoutParams();      
		
		_playButtonDefaultDrawable = 
				_buttonDrawableFactory.createPlayButtonDefaultDrawable(lp.width, lp.height);
		_playButtonPressedDrawable = 
				_buttonDrawableFactory.createPlayButtonPressedDrawable(lp.width, lp.height);

        _playButton.setImageDrawable(_playButtonDefaultDrawable);
        _playButton.setOnClickListener(_playButtonClickListener);
    }
    
    private void initStopButton() {
    	_stopButton = findControl(R.id.stopButton);
    	ViewGroup.LayoutParams lp = _stopButton.getLayoutParams();   
        _stopButtonDefaultDrawable = 
        		_buttonDrawableFactory.createStopButtonDrawable(lp.width, lp.height);
        _stopButton.setImageDrawable(_stopButtonDefaultDrawable);
        
        _stopButton.setOnClickListener(_stopButtonClickListener);
    }
    
    private void initRewindButton() {
    	_rewindButton = findControl(R.id.rewindButton);
    	ViewGroup.LayoutParams lp = _stopButton.getLayoutParams();   
    	_rewindButtonDefaultDrawable = 
        		_buttonDrawableFactory.createRewindButtonDrawable(lp.width, lp.height);
    	_rewindButton.setImageDrawable(_rewindButtonDefaultDrawable);

        _rewindButton.setOnTouchListener(_rewindButtonTouchListener);
    }
    
    private float calculatePlayerPanelHeight() {
        //Display display = getWindowManager().getDefaultDisplay(); 
        //int height = display.getHeight();

    	ViewGroup sightCaptionFrame = findControl(R.id.sightCaptionFrame);
        ViewGroup.LayoutParams sightCaptionFrameLp = sightCaptionFrame.getLayoutParams();
        
        ViewGroup.LayoutParams playerInfoPanelLp = (ViewGroup.LayoutParams)_playerInfoPanel.getLayoutParams();
        return playerInfoPanelLp.height-sightCaptionFrameLp.height;
    }
    
    //private View.OnLayoutChangeListener lcl = 
    
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

	@Override
	public void acceptNewSightGotInRange(String sightName, InputStream imageStream) throws Exception {
		setInfoPanelCaptionText(sightName);
        setNewBackgroundImage(imageStream);     
        _nextSightPointerArrow.setVisibility(View.INVISIBLE);
        _playerInfoPanel.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void acceptNewSightLookGotInRange(InputStream imageStream) throws Exception {
		setNewBackgroundImage(imageStream);
		_nextSightPointerArrow.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void acceptNoSightInRange() {
		setInfoPanelCaptionText(getString(R.string.sight_info_none));
		_rootView.setBackgroundResource(R.drawable.prague_silhouette);
	}

	@Override
	public void acceptNewRouteSelected(String sightName, String routeName) {
		setInfoPanelCaptionText(String.format("%s: %s", sightName, routeName));		
	}
	
	@Override
	public void setInfoPanelCaptionText(String text) {
		TextView caption = findControl(R.id.sightCaption);
        caption.setText(text);
	}

	private void setNewBackgroundImage(InputStream imageStream) throws Exception {
        if(imageStream != null) {
        	
        	BitmapFactory.Options boundsOpt = new BitmapFactory.Options();
        	boundsOpt.inJustDecodeBounds = true;
        	BitmapFactory.decodeStream(imageStream, null, boundsOpt);
        	float imgWidth = boundsOpt.outWidth;
        	float imgHeight = boundsOpt.outHeight;
        	
        	float scrWidth = _rootView.getWidth();
        	float scrHeight = _rootView.getHeight();

        	int sample = (int)Math.floor(Math.min(imgWidth/scrWidth, imgHeight/scrHeight));
        	
        	BitmapFactory.Options loadOpt = new BitmapFactory.Options();
        	loadOpt.inSampleSize = sample;
        	Bitmap sampledBitmap = BitmapFactory.decodeStream(imageStream, null, loadOpt);
        	
        	float bmpWidth = sampledBitmap.getWidth();
        	float bmpHeight = sampledBitmap.getHeight();
        	
        	float scrAspect = scrWidth/scrHeight;
        	float bmpAspect = bmpWidth/bmpHeight;
        	
        	Bitmap finalBitmap = sampledBitmap;
        	if(scrAspect > bmpAspect) {
        		float newHeight = scrHeight*(bmpWidth/scrWidth);
        		int y0 = Math.round((bmpHeight-newHeight)/2.0f);
        		finalBitmap = Bitmap.createBitmap(sampledBitmap, 0, y0, (int)bmpWidth, (int)newHeight);
        		sampledBitmap.recycle();
        	}
        	else if(scrAspect < bmpAspect) {
        		float newWidth = scrWidth*(bmpHeight/scrHeight);
        		int x0 = Math.round((bmpWidth-newWidth)/2.0f);
        		finalBitmap = Bitmap.createBitmap(sampledBitmap, x0, 0, (int)newWidth, (int)bmpHeight);
        		sampledBitmap.recycle();
        	}
        	
        	_rootView.setBackgroundDrawable(new BitmapDrawable(finalBitmap));
        	//img.recycle();

    		imageStream.close();        	
        }
	}
	
    
	@Override
	public void displayPlayerPlaying() {
		_playButton.setSelected(true);
		_playButton.setImageDrawable(_playButtonPressedDrawable);
	}

	@Override
	public void displayPlayerStopped() {
		_playButton.setSelected(false);
		_playButton.setImageDrawable(_playButtonDefaultDrawable);
	}

	@Override
	public void setAudioProgressMaximum(int ms) {
		_audioProgressBar.setMax(ms);		
	}

	@Override
	public void setAudioProgressPosition(int ms) {
		_audioProgressBarUpdater.setStatus(ms);
		_handler.post(_audioProgressBarUpdater);
	}

	@Override
	public void setAudioDuration(String formattedDuration) {
		_audioDuration.setText(formattedDuration);		
	}

	@Override
	public void setAudioPosition(String formattedPosition) {
		_audioPlayedUpdater.setStatus(formattedPosition);
		_handler.post(_audioPlayedUpdater);
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
	
	private void playPlayerPanelHidingAnimation(long duration) {
		TranslateAnimation ta = new TranslateAnimation(0, 0, 0, _playerPanelHeight);
        ta.setDuration(duration);
        ta.setRepeatCount(0);
        ta.setFillAfter(true);
        _playerInfoPanel.clearAnimation();
	    _playerInfoPanel.startAnimation(ta);  
	}
	
	private void setPlayerButtonsClickable(boolean clickable) {
		_playerPanel.setClickable(clickable);
		_playButton.setClickable(clickable);
		_stopButton.setClickable(clickable);
	}

	private OnClickListener _rootViewClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			_presenter.handleWindowClick();
		}
	};
	
    private OnClickListener _playButtonClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			_presenter.handlePlayButtonClick();
		}
	};
	
    private OnClickListener _stopButtonClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			_presenter.handleStopButtonClick();
		}
	};
	

	private OnTouchListener _rewindButtonTouchListener = new OnTouchListener() {		
		@Override
		public boolean onTouch(View v, MotionEvent e) {
			if(e.getAction() == MotionEvent.ACTION_UP) {
				_presenter.handleRewindButtonRelease();
				return true;
			}
			else if(e.getAction() == MotionEvent.ACTION_DOWN) {
				_presenter.handleRewindButtonPress();
				return true;
			}
			return false;
		}
	};
	
	private LocationListener _locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			if(location != null) {
				_presenter.handleLocationChange(location.getLatitude(), location.getLongitude());
			}			
		}
	};
	
	private ControlUpdater<String> _audioPlayedUpdater = new ControlUpdater<String>(
			new ControlUpdater.Updater<String>() {
				@Override
				public void Update(String param) {
					_audioPlayed.setText(param);
				}
			}, 
			"0:00"//MainActivity.this.getString(R.string.audio_formatted_position_default)
		);
	
	private ControlUpdater<Integer> _audioProgressBarUpdater = new ControlUpdater<Integer>(
			new ControlUpdater.Updater<Integer>() {
				@Override
				public void Update(Integer param) {
					 _audioProgressBar.setProgress(param);
				}
			}, 
			0
		);
	
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
