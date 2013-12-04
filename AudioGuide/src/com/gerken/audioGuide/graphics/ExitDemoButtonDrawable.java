package com.gerken.audioGuide.graphics;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;

public class ExitDemoButtonDrawable extends ShapeDrawable {
	private int _fillColor;
	private int _strokeColor;
	private Shape _s;
	
	private Paint _fillPaint;
	private Paint _strokePaint;
	private Paint _crossPaint;
	
	private static final float STROKE_WIDTH = 1.0f;
	
	public ExitDemoButtonDrawable(Shape s, int fillColor, int strokeColor) {
		super(s);
		_s = s;
		_fillColor = fillColor;
		_strokeColor = strokeColor;
		
		_fillPaint = new Paint(this.getPaint());
		_fillPaint.setStyle(Style.FILL);
		_fillPaint.setColor(_fillColor);
		_fillPaint.setAntiAlias(true);
		
		_strokePaint = new Paint(this.getPaint());
		_strokePaint.setStyle(Style.STROKE);
		_strokePaint.setColor(_strokeColor);
		_strokePaint.setAntiAlias(true);
		_strokePaint.setStrokeWidth(STROKE_WIDTH);
		_strokePaint.setStrokeJoin(Paint.Join.ROUND);
		_strokePaint.setStrokeCap(Paint.Cap.ROUND);
		//_strokePaint.setMaskFilter(new BlurMaskFilter(1.0f, BlurMaskFilter.Blur.NORMAL));
		
		_crossPaint = new Paint(this.getPaint());
		_crossPaint.setStyle(Style.STROKE);
		_crossPaint.setColor(strokeColor);
		_crossPaint.setAntiAlias(true);
		_crossPaint.setStrokeWidth(1.6f);
		_crossPaint.setStrokeJoin(Paint.Join.ROUND);
		_crossPaint.setStrokeCap(Paint.Cap.ROUND);
	}
	
	@Override
    protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
		_s.resize(canvas.getClipBounds().right, canvas.getClipBounds().bottom);
		_s.draw(canvas, _fillPaint);
		
		float strokeWidth = STROKE_WIDTH;
		Matrix matrix = new Matrix();
	    matrix.setRectToRect(
	    		new RectF(0, 0, canvas.getClipBounds().right, canvas.getClipBounds().bottom),
	            new RectF(strokeWidth/2.0f, strokeWidth/2.0f, canvas.getClipBounds().right - strokeWidth/2.0f,
	                    canvas.getClipBounds().bottom - strokeWidth/2.0f),
	            Matrix.ScaleToFit.FILL);
	    canvas.concat(matrix);
		
		float h = canvas.getClipBounds().bottom;
		float hh = 0.5f*h;
		float chh = 0.15f*h;
		
		canvas.drawLine(hh, hh, hh+chh, hh+chh, _crossPaint);
		canvas.drawLine(hh, hh, hh-chh, hh+chh, _crossPaint);
		canvas.drawLine(hh, hh, hh+chh, hh-chh, _crossPaint);
		canvas.drawLine(hh, hh, hh-chh, hh-chh, _crossPaint);		

		_s.draw(canvas, _strokePaint);		
	}
}