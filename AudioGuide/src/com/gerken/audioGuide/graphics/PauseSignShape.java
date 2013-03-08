package com.gerken.audioGuide.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.shapes.Shape;

public class PauseSignShape extends Shape {
	
	private final float BAR_RATIO = 0.2f;
	
	private float _sizeRatio;
	
	
	public PauseSignShape(float sizeRatio) {
		_sizeRatio = sizeRatio;
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		float w = getWidth();
		float h = getHeight();
		
		float barWidth = BAR_RATIO*_sizeRatio*w;
		float barHeight = _sizeRatio*h;
		
		float topLeftX = 0.5f*(w-_sizeRatio*w);
		float topLeftY = 0.5f*(h-_sizeRatio*h);
		
		canvas.drawRect(topLeftX, topLeftY, topLeftX+barWidth, topLeftY+barHeight, paint);
		
		float topRightX = 0.5f*(w+_sizeRatio*w);
		float topRightY = topLeftY;
		
		canvas.drawRect(topRightX-barWidth, topRightY, topRightX, topRightY+barHeight, paint);
	}
}
