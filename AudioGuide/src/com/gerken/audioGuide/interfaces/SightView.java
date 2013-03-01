package com.gerken.audioGuide.interfaces;

import java.io.InputStream;

public interface SightView {
	void acceptNewSightGotInRange(String sightName, InputStream imageStream, String audioFileName);
	void acceptNewSightLookGotInRange(InputStream imageStream);
}
