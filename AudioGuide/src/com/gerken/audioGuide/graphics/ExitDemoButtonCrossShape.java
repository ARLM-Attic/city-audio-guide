package com.gerken.audioGuide.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.shapes.Shape;

public class ExitDemoButtonCrossShape extends Shape {
	@Override
	public void draw(Canvas canvas, Paint paint) {
		float h = getHeight();
		float hh = 0.5f*h;
		float qh = 0.25f*h;
		
		canvas.drawLine(hh, hh, hh+qh, hh+qh, paint);
		canvas.drawLine(hh, hh, hh-qh, hh+qh, paint);
		canvas.drawLine(hh, hh, hh+qh, hh-qh, paint);
		canvas.drawLine(hh, hh, hh-qh, hh-qh, paint);
	}
}
