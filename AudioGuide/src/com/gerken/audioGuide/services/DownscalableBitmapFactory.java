package com.gerken.audioGuide.services;

import java.io.InputStream;

import com.gerken.audioGuide.graphics.AndroidDownscalableBitmap;
import com.gerken.audioGuide.graphics.DownscalableBitmap;
import com.gerken.audioGuide.interfaces.DownscalableBitmapCreator;

public class DownscalableBitmapFactory implements DownscalableBitmapCreator {

	@Override
	public DownscalableBitmap CreateDownscalableBitmap() {
		return new AndroidDownscalableBitmap();
	}

	@Override
	public DownscalableBitmap CreateDownscalableBitmap(InputStream imageStream,
			int targetWidth, int targetHeight) {
		DownscalableBitmap bmp = new AndroidDownscalableBitmap();
		bmp.init(imageStream, targetWidth, targetHeight);
		return bmp;
	}

}
