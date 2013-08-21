package com.gerken.audioGuide.services;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.gerken.audioGuide.graphics.BitmapDownscalingResult;
import com.gerken.audioGuide.interfaces.MediaAssetManager;
import com.gerken.audioGuide.interfaces.DownscalingBitmapLoader;

public class AndroidDownscalingBitmapLoader implements DownscalingBitmapLoader {
	private MediaAssetManager _assetStreamProvider;	
	
	public AndroidDownscalingBitmapLoader(MediaAssetManager assetStreamProvider){
		_assetStreamProvider = assetStreamProvider;
	}

	@Override
	public BitmapDownscalingResult load(String imageName, int targetWidth, int targetHeight) throws Exception {
		int originalWidth;
		int originalHeight;
		InputStream imageStream = null;
		try {
			imageStream = _assetStreamProvider.getImageAssetStream(imageName);
			BitmapFactory.Options boundsOpt = new BitmapFactory.Options();
	    	boundsOpt.inJustDecodeBounds = true;
	    	BitmapFactory.decodeStream(imageStream, null, boundsOpt);
	    	originalWidth = boundsOpt.outWidth;
	    	originalHeight = boundsOpt.outHeight;
		}
		finally {
			if(imageStream != null)
				imageStream.close();
		}    	

		double sample = Math.floor(Math.min(originalWidth/targetWidth, originalHeight/targetHeight));
		double power = Math.floor(log2(sample));
		double sample2 = (int)Math.pow(2, power);
		
		double downsizedBitmapWidth = originalWidth/sample2;
		double downsizedBitmapHeight = originalHeight/sample2;
    	
		double targetAspect = (double)targetWidth/(double)targetHeight;
		double bitmapAspect = downsizedBitmapWidth/downsizedBitmapHeight;
		
		int finalHorizontalPadding = 0;
		int finalVerticalPadding = 0;
		int finalWidth = 0;
		int finalHeight= 0;
		int finalSample = 1;
		if(targetAspect > bitmapAspect) {
			double newHeight = targetHeight*(downsizedBitmapWidth/targetWidth);
			finalVerticalPadding = (int)Math.round((downsizedBitmapHeight-newHeight)/2.0f);
    		finalHeight = (int)newHeight;
    		finalWidth = (int)downsizedBitmapWidth;
    	}
    	else if(targetAspect < bitmapAspect) {
    		double newWidth = targetWidth*(downsizedBitmapHeight/targetHeight);
    		finalHorizontalPadding = (int)Math.round((downsizedBitmapWidth-newWidth)/2.0f);
    		finalWidth = (int)newWidth;
    		finalHeight = (int)downsizedBitmapHeight;    		
    	}
    	else {
    		finalWidth = (int)downsizedBitmapWidth;
    		finalHeight = (int)downsizedBitmapHeight;
    	}
		finalSample = (int)sample2;
		
		BitmapFactory.Options loadOpt = new BitmapFactory.Options();
    	loadOpt.inSampleSize = finalSample;
    	
    	Bitmap sampledBitmap = null;
    	Bitmap finalBitmap = null;
    	try {
			imageStream = _assetStreamProvider.getImageAssetStream(imageName);
			sampledBitmap = BitmapFactory.decodeStream(imageStream, null, loadOpt);
			if(finalHorizontalPadding > 0 || finalVerticalPadding > 0) {
				Bitmap bmp = Bitmap.createBitmap(sampledBitmap, 
						finalHorizontalPadding, finalVerticalPadding, 
						finalWidth, finalHeight);
				sampledBitmap.recycle();
				finalBitmap = bmp;
			}
			else
				finalBitmap = sampledBitmap;
    	}
    	finally {
			if(imageStream != null)
				imageStream.close();
		}
    	
    	if(finalBitmap != null)
    		return new BitmapDownscalingResult(finalBitmap, finalWidth, finalHeight, 
    				finalHorizontalPadding, finalVerticalPadding);
    	
		return null;		
	}
	
	private double log2(double x) {
		final double ln2 = Math.log(2);
		return Math.log(x)/ln2;
	}
}
