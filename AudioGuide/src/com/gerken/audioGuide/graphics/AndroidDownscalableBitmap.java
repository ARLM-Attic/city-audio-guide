package com.gerken.audioGuide.graphics;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class AndroidDownscalableBitmap extends DownscalableBitmap {

	@Override
	protected Size<Integer> getOriginalSize(InputStream imageStream) {
		BitmapFactory.Options boundsOpt = new BitmapFactory.Options();
    	boundsOpt.inJustDecodeBounds = true;
    	BitmapFactory.decodeStream(imageStream, null, boundsOpt);
    	return new Size<Integer>(boundsOpt.outWidth, boundsOpt.outHeight);
	}

	@Override
	public Bitmap getFinalBitmap() {
		BitmapFactory.Options loadOpt = new BitmapFactory.Options();
    	loadOpt.inSampleSize = getSample();
		Bitmap sampledBitmap = BitmapFactory.decodeStream(getImageStream(), null, loadOpt);
		if(getFinalHorizontalPadding() > 0 || getFinalVerticalPadding() > 0) {
			Bitmap bmp = Bitmap.createBitmap(sampledBitmap, 
					getFinalHorizontalPadding(), getFinalVerticalPadding(), 
					getFinalWidth(), getFinalHeight());
			sampledBitmap.recycle();
			return bmp;
		}
		return sampledBitmap;
	}

}
