package com.gerken.audioGuide.graphics;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;

public class ExitDemoButtonDrawable extends ShapeDrawable {
	private int _fillColor;
	private int _strokeColor;
	private Shape _s;
	
	private Paint _fillPaint;
	private Paint _strokePaint;
	private Paint _crossPaint;
	
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
		_strokePaint.setStrokeWidth(1.0f);
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
		_s.draw(canvas, _fillPaint);
		_s.draw(canvas, _strokePaint);
		
		float h = canvas.getClipBounds().bottom;
		float hh = 0.5f*h;
		float chh = 0.15f*h;
		
		canvas.drawLine(hh, hh, hh+chh, hh+chh, _crossPaint);
		canvas.drawLine(hh, hh, hh-chh, hh+chh, _crossPaint);
		canvas.drawLine(hh, hh, hh+chh, hh-chh, _crossPaint);
		canvas.drawLine(hh, hh, hh-chh, hh-chh, _crossPaint);
	}
}