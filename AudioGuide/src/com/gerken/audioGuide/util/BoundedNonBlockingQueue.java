package com.gerken.audioGuide.util;

public class BoundedNonBlockingQueue<T> extends java.util.concurrent.LinkedBlockingQueue<T> {
	private Object _offerLock = new Object();

	public BoundedNonBlockingQueue(int capacity) {
		super(capacity);
	}
	
	@Override
	public boolean offer(T elem){
		synchronized(_offerLock){
			if(remainingCapacity() == 0) {
				try {
					take();
				}
				catch(InterruptedException iex) {}
			}
			super.offer(elem);
		}
		return true;
	}
}
