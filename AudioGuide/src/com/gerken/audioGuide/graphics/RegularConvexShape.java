package com.gerken.audioGuide.graphics;

import android.graphics.*;
import android.graphics.drawable.shapes.Shape;

public class RegularConvexShape extends Shape {

	private int _sideCount;
	private float _angle;
	
	private Path _convex;
	
	public RegularConvexShape(int sideCount, float angle) {
		_sideCount = sideCount;
		_angle = angle;
		
		_convex = createPath(sideCount, angle);
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		canvas.drawPath(_convex, paint);		
	}
	
	@Override
	protected void onResize(float width, float height) {
		_convex = createPath(_sideCount, _angle);
		super.onResize(width, height);
	}
	
	private Path createPath(int sideCount, float angle) {
		float w = getWidth();
		float h = getHeight();
		 
		Path path = new Path();
		float xc = 0.5f*w;
		float yc = 0.5f*h;
		float r = 0.5f*Math.min(w, h);
		path.moveTo(
				xc + (float)(r*Math.cos(angle)), 
				yc + (float)(r*Math.sin(angle))
		);
		for(int i=1; i<sideCount; i++) {
			double ai = angle+2.0*Math.PI*i/sideCount;
			float xi = xc + (float)(r*Math.cos(ai));
			float yi = yc + (float)(r*Math.sin(ai));
			path.lineTo(xi, yi);
		}
		
		return path;
	}

}
