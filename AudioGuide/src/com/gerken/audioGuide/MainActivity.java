package com.gerken.audioGuide;

import java.io.InputStream;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.graphics.PlayButtonDrawable;
import com.gerken.audioGuide.interfaces.SightView;
import com.gerken.audioGuide.presenters.SightPresenter;
import com.gerken.audioGuide.services.*;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class MainActivity extends Activity implements SightView {
	private final String LOG_TAG = "MainActivity";
	
	private ImageButton _playButton;
	
	private SightPresenter _presenter;
	private LocationManagerFacade _locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        _presenter = new SightPresenter(
        		((GuideApplication)getApplication()).getCity(), 
        		this, new GuideAssetManager(getApplicationContext()),
        		new DefaultAudioPlayer(getApplicationContext()),
        		new DefaultLoggingAdapter("SightPresenter"));
        
        _locationManager = new LocationManagerFacade(
        		getApplicationContext(), _presenter);     
        
        _playButton = findControl(R.id.playButton);
        ViewGroup.LayoutParams lp = _playButton.getLayoutParams();
        _playButton.setImageDrawable(new PlayButtonDrawable(lp.width, lp.height));
        _playButton.setOnClickListener(_playButtonClickListener);
        _playButton.invalidate();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	_locationManager.startTracking();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	_locationManager.stopTracking();
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
    		//Intent intent = new Intent(this, MainPreferenceActivity.class);
    		Intent intent = new Intent(this, RouteMapActivity.class);
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
		TextView caption = findControl(R.id.sightCaption);
        caption.setText(sightName);
        
        setNewBackgroundImage(imageStream);        
	}
	
	@Override
	public void acceptNewSightLookGotInRange(InputStream imageStream) {
		setNewBackgroundImage(imageStream);
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
	

    
    private OnClickListener _playButtonClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			_presenter.handlePlayButtonClick();
		}
	};


	@Override
	public void displayPlayerPlaying() {
		_playButton.setSelected(true);		
	}

	@Override
	public void displayPlayerStopped() {
		_playButton.setSelected(false);		
	}

	@Override
	public void displayError(String message) {
		//Toster.
		
	}
	
	@Override
	public void displayError(int messageResourceId) {
		//Toster.
		
	}
}
