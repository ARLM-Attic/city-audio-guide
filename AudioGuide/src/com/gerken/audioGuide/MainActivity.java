package com.gerken.audioGuide;

import java.io.InputStream;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.graphics.*;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.presenters.SightPresenter;
import com.gerken.audioGuide.services.*;

import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class MainActivity extends Activity implements SightView {
	private final String LOG_TAG = "MainActivity";
	private final int PLAY_BUTTON_SIGN_COLOR = 0xFF4CFF00;
	
	private View _playerPanel;
	private ImageButton _playButton;
	private ImageButton _stopButton;
	
	private RouteArrowsView _nextSightPointerArrow;
	
	private SightPresenter _presenter;
	private LocationManagerFacade _locationManager;
	
	private Drawable _playButtonDefaultDrawable;
	private Drawable _playButtonPressedDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Context ctx = getApplicationContext();
        _presenter = new SightPresenter(
        		((GuideApplication)getApplication()).getCity(), 
        		this, new GuideAssetManager(ctx),
        		new DefaultAudioPlayer(ctx),
        		new SharedPreferenceManager(ctx),
        		new DefaultLoggingAdapter("SightPresenter"));
        
        _locationManager = new LocationManagerFacade(ctx, _locationListener);       
        
        _playButton = findControl(R.id.playButton);
        ViewGroup.LayoutParams lp = _playButton.getLayoutParams();      
		
		_playButtonDefaultDrawable = createPlayButtonDefaultDrawable(lp.width, lp.height);
		_playButtonPressedDrawable = createPlayButtonPressedDrawable(lp.width, lp.height);

        _playButton.setImageDrawable(_playButtonDefaultDrawable);
        _playButton.setOnClickListener(_playButtonClickListener);
        
        _stopButton = findControl(R.id.stopButton);
        _stopButton.setOnClickListener(_stopButtonClickListener);
        
        _nextSightPointerArrow = findControl(R.id.nextSightPointerArrow);
        _playerPanel = findControl(R.id.playerPanel);
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
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    private <T> T findControl(int controlId) {
    	T control = (T)findViewById(controlId);
    	return control;
    }

	@Override
	public void acceptNewSightGotInRange(String sightName, InputStream imageStream) {
		setSightCaption(sightName);
        setNewBackgroundImage(imageStream);     
        _nextSightPointerArrow.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void acceptNewSightLookGotInRange(InputStream imageStream) {
		setNewBackgroundImage(imageStream);
		_nextSightPointerArrow.setVisibility(View.INVISIBLE);
	}

	@Override
	public void acceptNewRouteSelected(String sightName, String routeName) {
		setSightCaption(String.format("%s: %s", sightName, routeName));
		
	}
	
	private void setSightCaption(String text) {
		TextView caption = findControl(R.id.sightCaption);
        caption.setText(text);
	}

	private void setNewBackgroundImage(InputStream imageStream) {
		View root = findViewById(R.id.rootLayout);
        if(imageStream != null) {
        	root.setBackgroundDrawable(
            		Drawable.createFromStream(imageStream, ""));
        	try{
        		imageStream.close();
        	}
        	catch(Exception ex){
            	Log.e(LOG_TAG, "Oops", ex);
            }
        }
	}
	
	private Drawable createPlayButtonDefaultDrawable(int width, int height) {
		Drawable playSign = createPlayButtonDrawable(
				new RegularConvexShape(3, 0.4f*width, 0), width, height);
		
		ShapeDrawable shadow = new ShapeDrawable(new ShadowShape());
		shadow.setIntrinsicHeight(width);
		shadow.setIntrinsicWidth(height);
		Paint psPaint = shadow.getPaint();
		psPaint.setStyle(Style.FILL);
		psPaint.setColor(0x40666666);
		//psPaint.setColor(0xC00080FF);
		
		Drawable[] layers = new Drawable[]{ shadow, playSign };
		LayerDrawable ld = new LayerDrawable(layers);
		ld.setBounds(0, 0, width, height);
		
		return ld;
	}
	
	private Drawable createPlayButtonPressedDrawable(int width, int height) {
        Drawable pauseSign = createPlayButtonDrawable(
    		new PauseSignShape(0.4f), width, height);
		
		return pauseSign;
	}
	
	private Drawable createPlayButtonDrawable(Shape shape, int width, int height) {
		ShapeDrawable sign = new ShapeDrawable(shape);
		sign.setIntrinsicHeight(width);
		sign.setIntrinsicWidth(height);
		Paint psPaint = sign.getPaint();
		psPaint.setStyle(Style.FILL);
		psPaint.setColor(PLAY_BUTTON_SIGN_COLOR);		
	
		return sign;
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
	public void displayNextSightDirection(float heading) {
		_nextSightPointerArrow.setHeading(heading);
		_nextSightPointerArrow.invalidate();
		_nextSightPointerArrow.setVisibility(View.VISIBLE);	
		_playerPanel.setVisibility(View.INVISIBLE);	
	}

	@Override
	public void displayError(String message) {
		Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();	
	}
	
	@Override
	public void displayError(int messageResourceId) {
		Toast.makeText(getBaseContext(), messageResourceId, Toast.LENGTH_SHORT).show();
	}
	
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



}
