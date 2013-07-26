package com.gerken.audioGuide.interfaces;

import com.gerken.audioGuide.graphics.DownscaledBitmap;

public interface BitmapLoader {
	DownscaledBitmap load(String imageName, int targetWidth, int targetHeight) throws Exception;
}
