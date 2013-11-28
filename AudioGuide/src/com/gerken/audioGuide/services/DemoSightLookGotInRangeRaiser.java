package com.gerken.audioGuide.services;

import java.util.ArrayList;
import java.util.TimerTask;

import android.os.Handler;

import com.gerken.audioGuide.interfaces.NewSightLookGotInRangeRaiser;
import com.gerken.audioGuide.interfaces.OnSightLookGotInRangeListener;
import com.gerken.audioGuide.interfaces.Scheduler;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.objectModel.Sight;
import com.gerken.audioGuide.objectModel.SightLook;

public class DemoSightLookGotInRangeRaiser implements NewSightLookGotInRangeRaiser {
	private final int DEFAULT_SIGNAL_DELAY_MS = 1000; 
	
	private City _city;
	private SightLook _demoSightLook = null;
	private Scheduler _scheduler;	
	
	private int _signalDelayMs = DEFAULT_SIGNAL_DELAY_MS;
	
	private ArrayList<OnSightLookGotInRangeListener> _sightLookGotInRangeListeners = 
			new ArrayList<OnSightLookGotInRangeListener>();
	
	private TimerTask _signalTask = null;
	private Handler _handler;
	
	public DemoSightLookGotInRangeRaiser(City city, Scheduler scheduler) {
		_city = city;
		_scheduler = scheduler;
		
		if(!_city.getSights().isEmpty()) {
			Sight demoSight = _city.getSights().get(0);
			if(!demoSight.getSightLooks().isEmpty())
				_demoSightLook = demoSight.getSightLooks().get(0);
		}
		
		if(_demoSightLook != null) {
			_signalTask = new TimerTask() {
			
				@Override
				public void run() {
					postSignal();					
				}
			};
		}
		_handler = new Handler();
	}
	
	public void setSignalDelay(int signalDelayMs) {
		_signalDelayMs = signalDelayMs;
	}

	@Override
	public void addSightLookGotInRangeListener(OnSightLookGotInRangeListener listener) {
		_sightLookGotInRangeListeners.add(listener);
		
		_scheduler.schedule(_signalTask, _signalDelayMs);	
	}
	
	private void postSignal() {
		_handler.post(_postSignalRunnable);
	}
	
	private void sendSightLookGotInRangeSignal() {
		if(_demoSightLook != null)
			for(OnSightLookGotInRangeListener l : _sightLookGotInRangeListeners)
				l.onSightLookGotInRange(_demoSightLook);
	}
	
	private Runnable _postSignalRunnable = new Runnable() {
		
		@Override
		public void run() {
			sendSightLookGotInRangeSignal();
			
		}
	};

}
