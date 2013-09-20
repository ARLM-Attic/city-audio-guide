package com.gerken.audioGuide.services;

import java.util.Timer;
import java.util.TimerTask;

import com.gerken.audioGuide.interfaces.Scheduler;

public class SchedulerService implements Scheduler {
	private Timer _timer;
	
	private boolean _isTimerStarted = false;
	
	@Override
	public void schedule(TimerTask task, long delay) {
		if(!_isTimerStarted) {
			_timer = new Timer();
			_timer.schedule(decorateWithResetter(task),	delay);
			_isTimerStarted = true;
		}
	}
	
	private TimerTask decorateWithResetter(final TimerTask task) {
		return new TimerTask() {				
			@Override
			public void run() {
				task.run();
				_isTimerStarted = false;
			}
		};
	}

	@Override
	public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
		if(!_isTimerStarted) {
			_timer = new Timer();
			_timer.scheduleAtFixedRate(task, delay, period);
			_isTimerStarted = true;
		}
	}

	@Override
	public void cancel() {
		if(_timer != null)
			_timer.cancel();
		_isTimerStarted = false;
	}

}
