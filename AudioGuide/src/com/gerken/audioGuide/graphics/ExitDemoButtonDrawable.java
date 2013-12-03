package com.gerken.audioGuide.graphics;

import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;

public class ExitDemoButtonDrawable extends LayerDrawable {

	public ExitDemoButtonDrawable(int fillColor, int strokeColor) {
		super(createLayers(fillColor, strokeColor));
	}
	
	private static Drawable[] createLayers(int fillColor, int strokeColor) {
		ShapeDrawable fill = new ShapeDrawable(new ExitDemoButtonShape());
		Paint fillPaint = fill.getPaint();
		fillPaint.setStyle(Style.FILL);
		fillPaint.setColor(fillColor);
		fillPaint.setAntiAlias(true);
		
		ShapeDrawable stroke = new ShapeDrawable(new ExitDemoButtonShape());
		Paint strokePaint = stroke.getPaint();
		strokePaint.setStyle(Style.STROKE);
		strokePaint.setColor(strokeColor);
		strokePaint.setAntiAlias(true);
		strokePaint.setStrokeWidth(1.5f);
		strokePaint.setStrokeJoin(Paint.Join.ROUND);
		strokePaint.setStrokeCap(Paint.Cap.ROUND);
		
		ShapeDrawable cross = new ShapeDrawable(new ExitDemoButtonCrossShape());
		Paint crossPaint = cross.getPaint();
		crossPaint.setStyle(Style.STROKE);
		crossPaint.setColor(strokeColor);
		crossPaint.setAntiAlias(true);
		crossPaint.setStrokeWidth(1.5f);
		crossPaint.setStrokeJoin(Paint.Join.ROUND);
		crossPaint.setStrokeCap(Paint.Cap.ROUND);
		
		return new Drawable[] { stroke, cross, fill };
	}
}