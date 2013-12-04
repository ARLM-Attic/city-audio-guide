package com.gerken.audioGuide.graphics;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.shapes.Shape;


public class ExitDemoButtonShape extends Shape {
	private float _relativeTextPartHeight = 0.74f;
	@Override
	public void draw(Canvas canvas, Paint paint) {
		float w = getWidth();
		float h = getHeight();
		float hh = 0.5f*h;
		float th = _relativeTextPartHeight*h;
		float hth = 0.5f*th;
		
		float da = (float)Math.toDegrees(Math.asin(_relativeTextPartHeight));		
		Path path = new Path();
		path.addArc(new RectF(0, 0, h, h), da, 360.0f-da-da);
		//path.addArc(new RectF(0, 0, h, h), 0.0f, 160.0f);
		path.lineTo(w-hth, 0.5f*(h-th));
		path.arcTo(new RectF(w-th, hh-hth, w, hh+hth), -90, 180);
		//path.lineTo(w, h/2.0f);
		path.lineTo(h, 0.5f*(h+th));
		path.close();
		
		float strokeWidth = 1.0f;
		Matrix matrix = new Matrix();
	    matrix.setRectToRect(
	    		new RectF(0, 0, canvas.getClipBounds().right, canvas.getClipBounds().bottom),
	            new RectF(strokeWidth/2.0f, strokeWidth/2.0f, canvas.getClipBounds().right - strokeWidth/2.0f,
	                    canvas.getClipBounds().bottom - strokeWidth/2.0f),
	            Matrix.ScaleToFit.FILL);
	    canvas.concat(matrix);
		
		canvas.drawPath(path, paint);
	}
}
