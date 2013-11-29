package com.gerken.audioGuide.controls;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.graphics.PlayerButtonDrawableFactory;
import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.views.AudioPlayerView;

public class AudioPlayerControl extends RelativeLayout implements AudioPlayerView {

	private ImageButton _playButton;
	private ImageButton _stopButton;
	private ImageButton _rewindButton;
	private ProgressBar _audioProgressBar;
	private TextView _audioDuration;
	private TextView _audioPlayed;
	
	private Drawable _playButtonDefaultDrawable;
	private Drawable _playButtonPressedDrawable;
	private Drawable _stopButtonDefaultDrawable;
	private Drawable _rewindButtonDefaultDrawable;
	
	private PlayerButtonDrawableFactory _buttonDrawableFactory = 
			new PlayerButtonDrawableFactory();
	
	private Handler _handler;
	
	private ArrayList<OnEventListener> _stopPressedListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _playPressedListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _rewindPressedListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _rewindReleasedListeners = new ArrayList<OnEventListener>();
	
	private ControlUpdater<String> _audioPlayedUpdater;	
	private ControlUpdater<String> _audioDurationUpdater;
	
	private ControlUpdater<Integer> _audioProgressBarUpdater;	
	private ControlUpdater<Integer> _audioProgressBarMaximumUpdater;
	
	private ControlUpdater<Integer> _toastShower;
	
	public AudioPlayerControl(Context context) {
		super(context);
		init(context);
	}
	
	public AudioPlayerControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
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
		_audioProgressBarMaximumUpdater.setStatus(ms);
		_handler.post(_audioProgressBarMaximumUpdater);
	}

	@Override
	public void setAudioProgressPosition(int ms) {
		_audioProgressBarUpdater.setStatus(ms);
		_handler.post(_audioProgressBarUpdater);
	}

	@Override
	public void setAudioDuration(String formattedDuration) {
		_audioDurationUpdater.setStatus(formattedDuration);
		_handler.post(_audioDurationUpdater);
	}

	@Override
	public void setAudioPosition(String formattedPosition) {
		_audioPlayedUpdater.setStatus(formattedPosition);
		_handler.post(_audioPlayedUpdater);
	}
	
	@Override
	public void addPlayPressedListener(OnEventListener listener) {
		_playPressedListeners.add(listener);
	}
	
	@Override
	public void addStopPressedListener(OnEventListener listener) {
		_stopPressedListeners.add(listener);
	}
	
	@Override
	public void addRewindPressedListener(OnEventListener listener) {
		_rewindPressedListeners.add(listener);
	}
	
	@Override
	public void addRewindReleasedListener(OnEventListener listener) {
		_rewindReleasedListeners.add(listener);
	}
	
	@Override
	public void displayError(int messageResourceId) {
		//Toast.makeText(getContext(), messageResourceId, Toast.LENGTH_SHORT).show();
		_toastShower.setStatus(messageResourceId);
		_handler.post(_toastShower);
	}
	
	@Override
	public void setClickable(boolean clickable) {
		super.setClickable(clickable);
		_playButton.setClickable(clickable);
		_stopButton.setClickable(clickable);
	}
	
	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.ctrl_audio_player, this);
        
        _handler = new Handler();
        
		findControls();
		initPlayButton();
		initStopButton();
		initRewindButton();
		initControlUpdaters();
	}
	
	private void findControls() {
		_playButton = findControl(R.id.playButton);
		_stopButton = findControl(R.id.stopButton);
		_rewindButton = findControl(R.id.rewindButton);
		
		_audioProgressBar = findControl(R.id.audioProgressBar);
        _audioDuration = findControl(R.id.audioDuration);
        _audioPlayed = findControl(R.id.audioPlayed);
	}
	
    
    private void initPlayButton() {    	
        ViewGroup.LayoutParams lp = _playButton.getLayoutParams();      
		
		_playButtonDefaultDrawable = 
				_buttonDrawableFactory.createPlayButtonDefaultDrawable(lp.width, lp.height);
		_playButtonPressedDrawable = 
				_buttonDrawableFactory.createPlayButtonPressedDrawable(lp.width, lp.height);

        _playButton.setImageDrawable(_playButtonDefaultDrawable);
        _playButton.setOnClickListener(_playButtonClickListener);
    }
    
    private void initStopButton() {    	
    	ViewGroup.LayoutParams lp = _stopButton.getLayoutParams();   
        _stopButtonDefaultDrawable = 
        		_buttonDrawableFactory.createStopButtonDrawable(lp.width, lp.height);
        _stopButton.setImageDrawable(_stopButtonDefaultDrawable);
        
        _stopButton.setOnClickListener(_stopButtonClickListener);
    }
    
    private void initRewindButton() {
    	
    	ViewGroup.LayoutParams lp = _stopButton.getLayoutParams();   
    	_rewindButtonDefaultDrawable = 
        		_buttonDrawableFactory.createRewindButtonDrawable(lp.width, lp.height);
    	_rewindButton.setImageDrawable(_rewindButtonDefaultDrawable);

        _rewindButton.setOnTouchListener(_rewindButtonTouchListener);
    }
    
    private void initControlUpdaters() {
    	 _audioPlayedUpdater = new ControlUpdater<String>(
    				new TextViewUpdater(_audioPlayed), 
    				"0:00"//MainActivity.this.getString(R.string.audio_formatted_position_default)
    			);
    	 
    	 _audioDurationUpdater = new ControlUpdater<String>(
			new TextViewUpdater(_audioDuration), 
    				"0:00"
    			);
    	 
    	 _audioProgressBarUpdater = new ControlUpdater<Integer>(
    				new ControlUpdater.Updater<Integer>() {
    					@Override
    					public void Update(Integer param) {
    						 _audioProgressBar.setProgress(param);
    					}
    				}, 
    				0
    			);
    	 _audioProgressBarMaximumUpdater = new ControlUpdater<Integer>(
    				new ControlUpdater.Updater<Integer>() {
    					public void Update(Integer param) {
    						 _audioProgressBar.setMax(param);
    					}
    				}, 
    				0
    			);
    	 
    	 _toastShower = new ControlUpdater<Integer>(
    				new ControlUpdater.Updater<Integer>(){
    					@Override
    					public void Update(Integer param) {
    						Toast.makeText(getContext(), param, Toast.LENGTH_SHORT).show();
    					}
    				}, 0
    			);
    }
    
    private <T> T findControl(int controlId) {
    	T control = (T)findViewById(controlId);
    	return control;
    }
    
	
	private OnClickListener _playButtonClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			for(OnEventListener l: _playPressedListeners)
				l.onEvent();
		}
	};
	
    private OnClickListener _stopButtonClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			for(OnEventListener l: _stopPressedListeners)
				l.onEvent();
		}
	};
	

	private OnTouchListener _rewindButtonTouchListener = new OnTouchListener() {		
		@Override
		public boolean onTouch(View v, MotionEvent e) {
			if(e.getAction() == MotionEvent.ACTION_UP) {
				for(OnEventListener l: _rewindReleasedListeners)
					l.onEvent();
				return true;
			}
			else if(e.getAction() == MotionEvent.ACTION_DOWN) {
				for(OnEventListener l: _rewindPressedListeners)
					l.onEvent();
				return true;
			}
			return false;
		}
	};
}
