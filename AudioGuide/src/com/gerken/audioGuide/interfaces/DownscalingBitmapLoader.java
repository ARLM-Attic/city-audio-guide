package com.gerken.audioGuide.interfaces;

import com.gerken.audioGuide.graphics.BitmapDownscalingResult;

public interface DownscalingBitmapLoader {
	BitmapDownscalingResult load(String imageName, int targetWidth, int targetHeight) throws Exception;
}
