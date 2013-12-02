package com.gerken.audioGuide.interfaces;

public interface LockProvider {
	void acquireAudioPreparationLock();
	void releaseAudioPreparationLock();
}
