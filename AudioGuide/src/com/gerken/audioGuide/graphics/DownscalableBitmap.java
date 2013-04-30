package com.gerken.audioGuide.graphics;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;

public abstract class DownscalableBitmap {
	
	private int _x0 = 0;
	private int _y0 = 0;
	private int _finalWidth = 0;
	private int _finalHeight= 0;
	private int _finalSample = 1;
	
	private Size<Integer> _originalSize;
	private InputStream _imageStream;
	
	public void init(InputStream imageStream, int targetWidth, int targetHeight) {
		_imageStream = imageStream;
		_originalSize = getOriginalSize(imageStream);
		double sample = Math.floor(Math.min(_originalSize.getWidth()/targetWidth, _originalSize.getHeight()/targetHeight));
		double power = Math.floor(log2(sample));
		double sample2 = (int)Math.pow(2, power);
		
		double downsizedBitmapWidth = _originalSize.getWidth()/sample2;
		double downsizedBitmapHeight = _originalSize.getHeight()/sample2;
    	
		double targetAspect = (double)targetWidth/(double)targetHeight;
		double bitmapAspect = downsizedBitmapWidth/downsizedBitmapHeight;
		
		if(targetAspect > bitmapAspect) {
			double newHeight = targetHeight*(downsizedBitmapWidth/targetWidth);
    		_y0 = (int)Math.round((downsizedBitmapHeight-newHeight)/2.0f);
    		_finalHeight = (int)newHeight;
    		_finalWidth = (int)downsizedBitmapWidth;
    	}
    	else if(targetAspect < bitmapAspect) {
    		double newWidth = targetWidth*(downsizedBitmapHeight/targetHeight);
    		_x0 = (int)Math.round((downsizedBitmapWidth-newWidth)/2.0f);
    		_finalWidth = (int)newWidth;
    		_finalHeight = (int)downsizedBitmapHeight;    		
    	}
    	else {
    		_finalWidth = (int)downsizedBitmapWidth;
    		_finalHeight = (int)downsizedBitmapHeight;
    	}
		_finalSample = (int)sample2;
	}
	
	public void recycle() throws IOException {
		_imageStream.close();
	}	
	
	protected abstract Size<Integer> getOriginalSize(InputStream imageStream);
	public abstract Bitmap getFinalBitmap();
	
	public int getOriginalWidth() {
		return _originalSize.getWidth();
	}
	public int getOriginalHeight() {
		return _originalSize.getHeight();
	}
	public int getFinalWidth() {
		return _finalWidth;
	}
	public int getFinalHeight() {
		return _finalHeight;
	}
	
	public int getFinalHorizontalPadding() {
		return _x0;
	}
	public int getFinalVerticalPadding() {
		return _y0;
	}
	
	protected int getSample() {
		return _finalSample;
	}
	
	protected InputStream getImageStream() {
		return _imageStream;		
	}	
	
	private double log2(double x) {
		final double ln2 = Math.log(2);
		return Math.log(x)/ln2;
	}
	
	protected class Size<T> {
		private T _width;
		private T _height;
		
		public Size(T width, T height) {
			_width = width;
			_height = height;
		}
		
		public T getWidth() {
			return _width;
		}
		public T getHeight() {
			return _height;
		}
	}
}
