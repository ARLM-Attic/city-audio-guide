package com.gerken.audioGuide;

import java.io.InputStream;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.graphics.PlayButtonDrawable;
import com.gerken.audioGuide.interfaces.SightView;
import com.gerken.audioGuide.presenters.SightPresenter;
import com.gerken.audioGuide.services.DefaultLoggingAdapter;
import com.gerken.audioGuide.services.GuideAssetManager;
import com.gerken.audioGuide.services.LocationManagerFacade;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class MainActivity extends Activity implements SightView {
	private final String LOG_TAG = "MainActivity";
	
	private boolean _isPlayerPlaying = false;
	private MediaPlayer _mediaPlayer;
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
        		new DefaultLoggingAdapter("SightPresenter"));
        
        _locationManager = new LocationManagerFacade(
        		getApplicationContext(), _presenter);     
        
        _playButton = findControl(R.id.playButton);
        ViewGroup.LayoutParams lp = _playButton.getLayoutParams();
        _playButton.setImageDrawable(new PlayButtonDrawable(lp.width, lp.height));
        _playButton.setOnClickListener(_playButtonClickListener);
        _playButton.invalidate();
        
        _mediaPlayer = new MediaPlayer();
        _mediaPlayer.setOnCompletionListener(_mediaPlayerCompletionListener);
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
	public void acceptNewSightGotInRange(String sightName, InputStream imageStream, String audioFileName) {
		TextView caption = findControl(R.id.sightCaption);
        caption.setText(sightName);
        
        setNewBackgroundImage(imageStream);        
        setNewAudioFile(audioFileName);
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
	
	private void setNewAudioFile(String audioFileName) {
		try {    
	        AssetManager assetManager = getApplicationContext().getAssets();
	        AssetFileDescriptor descriptor =  assetManager.openFd(audioFileName);
	        _mediaPlayer.reset();
           _mediaPlayer.setDataSource(descriptor.getFileDescriptor(), 
        		   descriptor.getStartOffset(), descriptor.getLength() );
           descriptor.close();
           _mediaPlayer.prepare();
        }
        catch(Exception ex){ 
        	String msg=String.format("Error when setting %s as the new MediaPlayer datasource", audioFileName);
        	Log.e(LOG_TAG, msg, ex);
    	}
	}

    
    private OnClickListener _playButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(_isPlayerPlaying) {
				_isPlayerPlaying = false;
				_playButton.setSelected(false);
				_mediaPlayer.pause();
			}
			else {
				_isPlayerPlaying = true;
				_playButton.setSelected(true);
				_mediaPlayer.start();
			}
		}
	};
	
	private OnCompletionListener _mediaPlayerCompletionListener = new OnCompletionListener() {
		
		@Override
		public void onCompletion(MediaPlayer mp) {
			_isPlayerPlaying = false;
			_playButton.setSelected(false);
		}
	};
	

}
