package com.gerken.audioGuide.services;

import java.util.concurrent.locks.ReentrantLock;
import com.gerken.audioGuide.interfaces.LockProvider;

public class DefaultLockProvider implements LockProvider {
	private ReentrantLock _audioPlayerLock = new ReentrantLock();

	@Override
	public void acquireAudioPreparationLock() {
		_audioPlayerLock.lock();
		
	}

	@Override
	public void releaseAudioPreparationLock() {
		_audioPlayerLock.unlock();		
	}

}
