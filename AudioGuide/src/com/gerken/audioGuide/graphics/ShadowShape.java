package com.gerken.audioGuide.graphics;

import android.graphics.*;
import android.graphics.drawable.shapes.Shape;

public class ShadowShape extends Shape {

	@Override
	public void draw(Canvas canvas, Paint paint) {
		float padding = 2.0f;
		float w = getWidth();
		float h = getHeight();
		
		android.util.Log.d("ShadowShape", 
				String.format("clip to: %d,%d", canvas.getClipBounds().right, canvas.getClipBounds().bottom));
		
		Matrix matrix = new Matrix();
		matrix.setRectToRect(new RectF(0, 0, canvas.getClipBounds().right,
                canvas.getClipBounds().bottom),
            new RectF(padding, padding, (float)(canvas.getClipBounds().right) - padding,
                    (float)(canvas.getClipBounds().bottom) - padding),
            Matrix.ScaleToFit.FILL);
		canvas.concat(matrix);
		
		
		Path p = new Path();
		p.moveTo(w, 0.5f*h);
		p.arcTo(new RectF(0, 0, w, h), 0, 135);
		p.arcTo(new RectF(-0.1f*w, 0.4f*h, 1.4f*w, 2.0f*h), 215, 70);
		p.close();
		
		canvas.drawPath(p, paint);		
	}

}
