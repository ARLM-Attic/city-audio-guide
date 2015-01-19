package com.gerken.audioGuide.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.shapes.Shape;

public class PadLockShape extends Shape {
	private final float PADDING_X_RATIO = 0.15625f;
	private final float PADDING_Y_RATIO = 0.0625f;
	private final float BODY_R_RATIO = 0.03125f;
	private final float BODY_TOP_RATIO = 0.4375f;
	private final float SHACKLE_OUTER_DX_RATIO = 0.25f;
	private final float SHACKLE_OUTER_R_RATIO = 0.25f;
	
	
	private Path _path;

	@Override
	public void draw(Canvas canvas, Paint paint) {
		canvas.drawPath(_path, paint);
	}
	
	@Override
	protected void onResize(float width, float height) {
		_path = createPath();
		super.onResize(width, height);
	}
	
	private Path createPath() {
		float w = getWidth();
		float h = getHeight();
		 
		Path path = new Path();
		RectF tlArcBox = new RectF(PADDING_X_RATIO*w, BODY_TOP_RATIO*h, 
				(PADDING_X_RATIO+2f*BODY_R_RATIO)*w, (BODY_TOP_RATIO+2f*BODY_R_RATIO)*h);
		path.arcTo(tlArcBox, 180, 90);
		path.lineTo(SHACKLE_OUTER_DX_RATIO*w, BODY_TOP_RATIO*h);
		
		RectF shackleBox = new RectF(SHACKLE_OUTER_DX_RATIO*w, PADDING_Y_RATIO*h, 
			(1f-SHACKLE_OUTER_DX_RATIO)*w, (PADDING_Y_RATIO+2f*SHACKLE_OUTER_R_RATIO)*h);
		path.arcTo(shackleBox, 180, 180);
		path.lineTo((1f-SHACKLE_OUTER_DX_RATIO)*w, BODY_TOP_RATIO*h);
		
		RectF trArcBox = new RectF((1f-PADDING_X_RATIO-2f*BODY_R_RATIO)*w, BODY_TOP_RATIO*h, 
				(1f-PADDING_X_RATIO)*w, (BODY_TOP_RATIO+2f*BODY_R_RATIO)*h);
		path.arcTo(trArcBox, -90, 90);
		
		RectF brArcBox = new RectF((1f-PADDING_X_RATIO-2f*BODY_R_RATIO)*w, (1f-PADDING_Y_RATIO-2f*BODY_R_RATIO)*h, 
				(1f-PADDING_X_RATIO)*w, (1f-PADDING_Y_RATIO)*h);
		path.arcTo(brArcBox, 0, 90);
		
		RectF blArcBox = new RectF(PADDING_X_RATIO*w, (1f-PADDING_Y_RATIO-2f*BODY_R_RATIO)*h, 
				(PADDING_X_RATIO+2f*BODY_R_RATIO)*w, (1f-PADDING_Y_RATIO)*h);
		path.arcTo(blArcBox, 90, 90);
		
		path.close();		
		return path;
	}
}
