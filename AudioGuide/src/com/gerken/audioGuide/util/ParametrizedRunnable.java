package com.gerken.audioGuide.util;

public abstract class ParametrizedRunnable<T> implements Runnable {	
	private T _runParam;
	
	public ParametrizedRunnable(T runParam) {
		_runParam = runParam;
	}
	
	public abstract void run(T runParam);

	@Override
	public void run() {
		run(_runParam);		
	}

}
