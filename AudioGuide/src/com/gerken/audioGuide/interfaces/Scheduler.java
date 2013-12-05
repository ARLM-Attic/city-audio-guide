package com.gerken.audioGuide.interfaces;

import java.util.TimerTask;

public interface Scheduler {
	void schedule(TimerTask task, long delay);
	void scheduleWithoutCheck(TimerTask task, long delay);
	void scheduleAtFixedRate(TimerTask task, long delay, long period);
	void cancel();
}
