package com.gerken.audioGuide.interfaces;

import java.io.IOException;

public interface AudioPlayerRewinder {
	void startRewinding();
	void stopRewinding() throws IOException;
}
