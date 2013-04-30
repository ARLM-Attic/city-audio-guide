package com.gerken.audioGuide.services;

import com.gerken.audioGuide.graphics.AndroidDownscalableBitmap;
import com.gerken.audioGuide.graphics.DownscalableBitmap;
import com.gerken.audioGuide.interfaces.DownscalableBitmapCreator;

public class DownscalableBitmapFactory implements DownscalableBitmapCreator {

	@Override
	public DownscalableBitmap CreateDownscalableBitmap() {
		return new AndroidDownscalableBitmap();
	}

}
