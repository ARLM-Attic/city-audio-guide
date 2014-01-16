package com.gerken.audioGuide.services;

import java.util.ArrayList;

import com.gerken.audioGuide.interfaces.LocationTracker;
import com.gerken.audioGuide.interfaces.listeners.OnEventListener;
import com.gerken.audioGuide.interfaces.listeners.OnLocationChangedListener;

public class DummyFixedPlaceLocationTracker implements LocationTracker {
	private final double FIRST_FIXED_LATITUDE = 50.08693;
	private final double FIRST_FIXED_LONGITUDE = 14.42079;
	private final int FIRST_DELAY_MS = 7000;
	
	private final double SECOND_FIXED_LATITUDE = 50.08771;
	private final double SECOND_FIXED_LONGITUDE = 14.42143;
	private final int SECOND_DELAY_MS = 15000;
	
	private ArrayList<OnLocationChangedListener> _locationChangedListeners = 
			new ArrayList<OnLocationChangedListener>();
	
	private OnEventListener _dummyFirstDelayFinishListener = new OnEventListener() {		
		public void onEvent() {
			for(OnLocationChangedListener l : _locationChangedListeners)
				l.onLocationChanged(FIRST_FIXED_LATITUDE, FIRST_FIXED_LONGITUDE);
		}
	};
	
	private OnEventListener _dummySecondDelayFinishListener = new OnEventListener() {		
		public void onEvent() {
			for(OnLocationChangedListener l : _locationChangedListeners)
				l.onLocationChanged(SECOND_FIXED_LATITUDE, SECOND_FIXED_LONGITUDE);
		}
	};

	@Override
	public void startTracking() {
		new DummyWaitTask(FIRST_DELAY_MS, _dummyFirstDelayFinishListener).execute();
		new DummyWaitTask(SECOND_DELAY_MS, _dummySecondDelayFinishListener).execute();
	}

	@Override
	public void stopTracking() {
	}

	@Override
	public void addLocationChangedListener(OnLocationChangedListener listener) {
		_locationChangedListeners.add(listener);		
	}

	private class DummyWaitTask extends android.os.AsyncTask<Void, Void, Void> {
		private final int _delay;
		private OnEventListener _onTaskFinishListener;
		
		public DummyWaitTask(int delay, OnEventListener onTaskFinishListener) {
			_delay = delay;
			_onTaskFinishListener = onTaskFinishListener;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Thread.sleep(_delay);
			} catch (InterruptedException e) {	}
			return null;
		}
		
		protected void onPostExecute(Void arg) {
			_onTaskFinishListener.onEvent();
	     }
		
	}
}
