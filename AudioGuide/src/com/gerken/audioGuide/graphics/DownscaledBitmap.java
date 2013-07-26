package com.gerken.audioGuide.graphics;

import android.graphics.Bitmap;

public class DownscaledBitmap {
	private Bitmap _finalBitmap;
	
	private int _x0 = 0;
	private int _y0 = 0;
	private int _finalWidth = 0;
	private int _finalHeight= 0;
	
	public DownscaledBitmap(Bitmap finalBitmap,
			int finalWidth, int finalHeight,
			int finalHorizontalPadding, int finalVerticalPadding) {
		_finalBitmap = finalBitmap;
		_finalWidth = finalWidth;
		_finalHeight = finalHeight;
		_x0 = finalHorizontalPadding;
		_y0 = finalVerticalPadding;
	}
	
	public Bitmap getFinalBitmap() {
		return _finalBitmap;
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

}
