package com.gerken.audioGuide.graphics;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;

public class ExitDemoButtonDrawable extends ShapeDrawable {
	private static final int DEF_COLOR = 0;
	private static final float DEF_STROKE_WIDTH = 1.0f;
	private static final float CROSS_STROKE_RATIO = 1.6f;	
	
	private int _fillColor = DEF_COLOR;
	private int _strokeColor = DEF_COLOR;
	private Shape _s;
	
	private Paint _fillPaint;
	private Paint _strokePaint;
	private Paint _crossPaint;	
	
	private float _strokeWidth = DEF_STROKE_WIDTH;
	
	public ExitDemoButtonDrawable(Shape s) {
		super(s);
		_s = s;
		
		_fillPaint = new Paint(this.getPaint());
		_fillPaint.setStyle(Style.FILL);
		_fillPaint.setColor(_fillColor);
		_fillPaint.setAntiAlias(true);
		
		_strokePaint = new Paint(this.getPaint());
		_strokePaint.setStyle(Style.STROKE);
		_strokePaint.setColor(_strokeColor);
		_strokePaint.setAntiAlias(true);
		_strokePaint.setStrokeWidth(_strokeWidth);
		_strokePaint.setStrokeJoin(Paint.Join.ROUND);
		_strokePaint.setStrokeCap(Paint.Cap.ROUND);
		
		_crossPaint = new Paint(this.getPaint());
		_crossPaint.setStyle(Style.STROKE);
		_crossPaint.setColor(_strokeColor);
		_crossPaint.setAntiAlias(true);
		_crossPaint.setStrokeWidth(CROSS_STROKE_RATIO*_strokeWidth);
		_crossPaint.setStrokeJoin(Paint.Join.ROUND);
		_crossPaint.setStrokeCap(Paint.Cap.ROUND);
	}
	
	public void setFillColor(int fillColor) {
		_fillColor = fillColor;
		_fillPaint.setColor(_fillColor);
	}
	
	public void setStrokeColor(int strokeColor) {
		_strokeColor = strokeColor;
		_strokePaint.setColor(_strokeColor);
		_crossPaint.setColor(_strokeColor);
	}
	
	public void setStrokeWidth(float strokeWidth) {
		_strokeWidth = strokeWidth;
		_strokePaint.setStrokeWidth(_strokeWidth);
		_crossPaint.setStrokeWidth(CROSS_STROKE_RATIO*_strokeWidth);
	}
	
	@Override
    protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
		_s.resize(canvas.getClipBounds().right, canvas.getClipBounds().bottom);
		_s.draw(canvas, _fillPaint);
		
		drawCross(canvas);
		
		float shrink = 0.5f*_strokeWidth;
		Matrix matrix = new Matrix();
	    matrix.setRectToRect(
	    		new RectF(0, 0, canvas.getClipBounds().right, canvas.getClipBounds().bottom),
	            new RectF(shrink, shrink, canvas.getClipBounds().right - shrink,
	                    canvas.getClipBounds().bottom - shrink),
	            Matrix.ScaleToFit.FILL);
	    canvas.concat(matrix);		
		_s.draw(canvas, _strokePaint);		
	}
	
	private void drawCross(Canvas canvas) {
		float h = canvas.getClipBounds().bottom;
		float hh = 0.5f*h;
		float chh = 0.15f*h;
		
		canvas.drawLine(hh, hh, hh+chh, hh+chh, _crossPaint);
		canvas.drawLine(hh, hh, hh-chh, hh+chh, _crossPaint);
		canvas.drawLine(hh, hh, hh+chh, hh-chh, _crossPaint);
		canvas.drawLine(hh, hh, hh-chh, hh-chh, _crossPaint);	
	}
}