package com.gerken.audioGuide.services;

import java.util.TimerTask;

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
	
	public DemoSightLookGotInRangeRaiser(City city, Scheduler scheduler) {
		_city = city;
		_scheduler = scheduler;
		
		if(!_city.getSights().isEmpty()) {
			Sight demoSight = _city.getSights().get(0);
			if(!demoSight.getSightLooks().isEmpty())
				_demoSightLook = demoSight.getSightLooks().get(0);
		}
	}
	
	public void setSignalDelay(int signalDelayMs) {
		_signalDelayMs = signalDelayMs;
	}

	@Override
	public void addSightLookGotInRangeListener(OnSightLookGotInRangeListener listener) {
		if(_demoSightLook != null) {
			_scheduler.schedule(
				new NewSightLookGotInRangeNotificationTask(listener, _demoSightLook),
				_signalDelayMs
			);
		}	
	}
	
	private class NewSightLookGotInRangeNotificationTask extends TimerTask {
		private SightLook _sightLook;
		private OnSightLookGotInRangeListener _listener;
		
		public NewSightLookGotInRangeNotificationTask(
				OnSightLookGotInRangeListener listener, SightLook sightLook) {
			_sightLook = sightLook;
			_listener = listener;
		}
		
		public void run() {
			_listener.onSightLookGotInRange(_sightLook);					
		}
	}
}
