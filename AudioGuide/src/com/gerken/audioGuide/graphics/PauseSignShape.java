package com.gerken.audioGuide.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.shapes.Shape;

public class PauseSignShape extends Shape {
	
	private final float BAR_RATIO = 0.3f;
	private final float SIZE_RATIO = (float)(1.0/Math.sqrt(2));

	@Override
	public void draw(Canvas canvas, Paint paint) {
		float w = getWidth();
		float h = getHeight();
		
		float barWidth = BAR_RATIO*SIZE_RATIO*w;
		float barHeight = SIZE_RATIO*h;
		
		float topLeftX = 0.5f*(w-SIZE_RATIO*w);
		float topLeftY = 0.5f*(h-SIZE_RATIO*h);
		
		canvas.drawRect(topLeftX, topLeftY, topLeftX+barWidth, topLeftY+barHeight, paint);
		
		float topRightX = 0.5f*(w+SIZE_RATIO*w);
		float topRightY = topLeftY;
		
		canvas.drawRect(topRightX-barWidth, topRightY, topRightX, topRightY+barHeight, paint);
	}
}
