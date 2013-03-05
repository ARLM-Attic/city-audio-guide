package com.gerken.audioGuide.graphics;

import android.graphics.*;
import android.graphics.drawable.shapes.Shape;

public class RegularConvexShape extends Shape {
	
	private final float SIZE_DELTA = 0.1f;
	
	private int _sideCount;
	private float _size;
	private float _angle;
	
	private float _width;
	private float _height;
	
	private Path _convex;
	
	public RegularConvexShape(int sideCount, float size, float angle) {
		_sideCount = sideCount;
		_size = size;
		_angle = angle;
		
		_convex = createPath(sideCount, size, angle);
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		canvas.drawPath(_convex, paint);		
	}
	
	@Override
	protected void onResize(float width, float height) {
		if(Math.abs(width-_width) > SIZE_DELTA ||  Math.abs(height-_height) > SIZE_DELTA) {
			_width = width;
			_height = height;
			_convex = createPath(_sideCount, _size, _angle);
		}
		super.onResize(width, height);
	}
	
	private Path createPath(int sideCount, float size, float angle) {
		float w = getWidth();
		float h = getHeight();
		 
		Path path = new Path();
		float xc = 0.5f*w;
		float yc = 0.5f*h;
		float r = 0.5f*size;
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
