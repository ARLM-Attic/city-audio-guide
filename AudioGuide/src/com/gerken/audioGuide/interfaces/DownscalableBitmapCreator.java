package com.gerken.audioGuide.interfaces;

import java.io.InputStream;

import com.gerken.audioGuide.graphics.DownscalableBitmap;

public interface DownscalableBitmapCreator {
	DownscalableBitmap CreateDownscalableBitmap();
	DownscalableBitmap CreateDownscalableBitmap(InputStream imageStream, int targetWidth, int targetHeight);
}
