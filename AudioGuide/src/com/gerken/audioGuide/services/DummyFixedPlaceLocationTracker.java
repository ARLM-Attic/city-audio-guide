package com.gerken.audioGuide.services;

import java.util.ArrayList;

import com.gerken.audioGuide.interfaces.LocationTracker;
import com.gerken.audioGuide.interfaces.listeners.OnEventListener;
import com.gerken.audioGuide.interfaces.listeners.OnLocationChangedListener;

public class DummyFixedPlaceLocationTracker implements LocationTracker {
	private final double FIXED_LATITUDE = 50.08693;
	private final double FIXED_LONGITUDE = 14.42079;
	private final int DELAY_MS = 10000;
	
	private ArrayList<OnLocationChangedListener> _locationChangedListeners = 
			new ArrayList<OnLocationChangedListener>();
	
	private OnEventListener _dummyDelayFinishListener = new OnEventListener() {		
		public void onEvent() {
			for(OnLocationChangedListener l : _locationChangedListeners)
				l.onLocationChanged(FIXED_LATITUDE, FIXED_LONGITUDE);
		}
	};

	@Override
	public void startTracking() {
		new DummyWaitTask(DELAY_MS, _dummyDelayFinishListener).execute();		
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
