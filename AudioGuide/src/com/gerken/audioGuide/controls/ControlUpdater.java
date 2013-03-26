package com.gerken.audioGuide.controls;

public class ControlUpdater<T> implements Runnable {
	private Updater<T> _updater;
	private T _status;
	
	public ControlUpdater(Updater<T> updater, T initialStatus) {
		_updater = updater;		
		_status = initialStatus;
	}
	
	public void setStatus(T status) {
		_status = status;
	}

	@Override
	public void run() {
		_updater.Update(_status);			
	}

	public interface Updater<T> {
		void Update(T param);
	}
}
