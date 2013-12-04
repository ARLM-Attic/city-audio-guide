package com.gerken.audioGuide.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.shapes.Shape;

public class ExitDemoButtonShape extends Shape {
	private float _relativeTextPartHeight = 0.74f;
	private float _relativeTagRoundingRadius = 0.3f;
	
	@Override
	public void draw(Canvas canvas, Paint paint) {
		float w = getWidth();
		float h = getHeight();
		float hh = 0.5f*h;
		float th = _relativeTextPartHeight*h;
		float hth = 0.5f*th;
		float rr = _relativeTagRoundingRadius*h;
		
		float da = (float)Math.toDegrees(Math.asin(_relativeTextPartHeight));		
		Path path = new Path();
		path.addArc(new RectF(0, 0, h, h), da, 360.0f-da-da);
		path.lineTo(w-rr, 0.5f*(h-th));
		path.arcTo(new RectF(w-rr, hh-hth, w, hh-hth+rr), -90, 90);
		path.lineTo(w, hh+hth-rr);
		path.arcTo(new RectF(w-rr, hh+hth-rr, w, hh+hth), 0, 90);
		path.lineTo(h, 0.5f*(h+th));
		path.close();
		
		canvas.drawPath(path, paint);
	}
}
