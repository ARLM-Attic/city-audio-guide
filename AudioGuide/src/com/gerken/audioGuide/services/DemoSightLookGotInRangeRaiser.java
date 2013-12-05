package com.gerken.audioGuide.services;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import com.gerken.audioGuide.interfaces.NewSightLookGotInRangeRaiser;
import com.gerken.audioGuide.interfaces.OnSightLookGotInRangeListener;
import com.gerken.audioGuide.interfaces.Scheduler;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.objectModel.Sight;
import com.gerken.audioGuide.objectModel.SightLook;

public class DemoSightLookGotInRangeRaiser implements NewSightLookGotInRangeRaiser {
	private final int DEFAULT_FIRST_SIGNAL_DELAY_MS = 5000;
	private final int DEFAULT_SUBSEQUENT_SIGNAL_DELAY_MS = 10000; 
	
	private City _city;
	private List<SightLook> _demoSightLooks;
	private Scheduler _scheduler;	
	
	private int _firstSignalDelayMs = DEFAULT_FIRST_SIGNAL_DELAY_MS;
	private int _subsequentSignalDelayMs = DEFAULT_SUBSEQUENT_SIGNAL_DELAY_MS;
	
	public DemoSightLookGotInRangeRaiser(City city, Scheduler scheduler) {
		_city = city;
		_scheduler = scheduler;
		
		_demoSightLooks = new ArrayList<SightLook>();
		if(!_city.getSights().isEmpty()) {
			Sight demoSight = _city.getSights().get(0);
			for(SightLook sl: demoSight.getSightLooks())
				_demoSightLooks.add(sl);
		}
	}
	
	public void setFirstSignalDelay(int signalDelayMs) {
		_firstSignalDelayMs = signalDelayMs;
	}

	@Override
	public void addSightLookGotInRangeListener(OnSightLookGotInRangeListener listener) {
		if(!_demoSightLooks.isEmpty()) {
			for(int i=0; i<_demoSightLooks.size(); i++) {
				_scheduler.scheduleWithoutCheck(
					new NewSightLookGotInRangeNotificationTask(listener, _demoSightLooks.get(i)),
					_firstSignalDelayMs + i*_subsequentSignalDelayMs
				);
			}
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
