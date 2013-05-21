package com.gerken.audioGuide.interfaces;

import java.util.TimerTask;

public interface Scheduler {
	void scheduleAtFixedRate(TimerTask task, long delay, long period);
	void cancel();
}
