package com.gerken.audioGuide.graphics;

import com.gerken.audioGuide.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RouteArrowsView extends View {

	private final float TIP_HEIGHT_RATIO = 0.5f;
	private final float TAIL_WIDTH_RATIO = 0.5f;
	
	private final float DEF_ARROW_WIDTH = 40;
	private final float DEF_ARROW_HEIGHT = 60;
	
	private final float A_135 = (float)(Math.PI * 135.0 / 180.0);
	
	private Paint _arrowPaint;
	
	private float _tipX = 0.4f;
	private float _tipY = 0.4f;
	
	private float _arrowWidth = DEF_ARROW_WIDTH;
	private float _arrowHeight = DEF_ARROW_HEIGHT;
	
	private float _heading = 0.0f;
	
	private float _arrowLeft = 0;
	private float _arrowTop = 0;
	
	private float _xRotationAngle = 0;
	
	private Camera _camera;

	public RouteArrowsView(Context context) {		
		super(context);
		
		_arrowPaint = createArrowPaint();
		_camera = new Camera();
	}
	
	public RouteArrowsView(Context context, AttributeSet attrs) 
    { 
        super(context, attrs); 
        init(context, attrs); 
    } 

    public RouteArrowsView(Context context, AttributeSet attrs, int defStyle) 
    { 
        super(context, attrs, defStyle); 
        init(context, attrs); 
    } 
    
    private void init(Context context, AttributeSet attrs) {
    	_arrowPaint = createArrowPaint();
        _camera = new Camera();
        
    	TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.RouteArrowsView);
    	_arrowWidth = arr.getDimension(R.styleable.RouteArrowsView_arrowWidth, DEF_ARROW_WIDTH);
    	_arrowHeight = arr.getDimension(R.styleable.RouteArrowsView_arrowHeight, DEF_ARROW_HEIGHT);
    	
    	arr.recycle();
    }
    
    public void setVector(float heading, float horizon) {
    	_tipX = (float)(0.5+0.375*Math.sin(heading));
    	_tipY = 1.0f-horizon;
    	_heading = heading;
    	
    	float tx = _tipX * getWidth();
		float ty = _tipY * getHeight();
		_arrowLeft = tx - 0.5f*_arrowWidth;
		_arrowTop = getHeight() - getPaddingBottom() - _arrowHeight;
		
		double perspectiveRatio = 1.0 + (_arrowTop - ty)/_arrowHeight;
		_xRotationAngle = (perspectiveRatio >= 1.0) ? 0 :
			(float)(180.0*Math.acos(perspectiveRatio)/Math.PI);	
    }
    
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Path arrow = createArrow(_arrowWidth, _arrowHeight);
		canvas.save();
		canvas.translate(_arrowLeft, _arrowTop);
		applyPerspective(canvas);		
		canvas.drawPath(arrow, _arrowPaint);
		canvas.restore();
	}
	
	private Path createArrow(float w, float h) {	
		float tipBottomY = TIP_HEIGHT_RATIO*h;
		float tailHalfWidth = 0.5f*TAIL_WIDTH_RATIO*w;
		float cx = 0.5f*w;
		Path path = new Path();
		path.moveTo(0.5f*w, 0);
		path.lineTo(w, tipBottomY);
		path.lineTo(cx+tailHalfWidth, tipBottomY);
		
		float straightTailHeight = h-TIP_HEIGHT_RATIO*h-0.5f*tailHalfWidth;
		path.rLineTo(0, straightTailHeight);
		path.rQuadTo(-0.5f*TAIL_WIDTH_RATIO*w, 0.5f*TAIL_WIDTH_RATIO*w, -TAIL_WIDTH_RATIO*w, 0);
		path.rLineTo(0, -straightTailHeight);
		path.lineTo(0, tipBottomY);		
		path.close();
		
		return path;				
	}
	
	private Paint createArrowPaint(){
		Paint arrowPaint = new Paint();
		arrowPaint.setColor(0x904AFF00);
		arrowPaint.setStyle(Paint.Style.FILL);
		arrowPaint.setAntiAlias(true);
		return arrowPaint;
	}
	

	public void applyPerspective(Canvas canvas) {
		Matrix m = new Matrix();
		
		_camera.save();
		//_camera.rotateY(-(int)(180.0*_heading/Math.PI));
		_camera.rotateY(0);
		_camera.rotateX(_xRotationAngle);		
		//_camera.rotateZ(0);
		float dZ = -(int)(180.0*_heading/Math.PI);
		if(_heading > A_135)
			dZ += 180; 
		_camera.rotateZ(dZ);
		_camera.getMatrix(m);

		float cx = 0.5f*_arrowWidth;
		float cy = _arrowHeight;
		m.preTranslate(-cx, -cy);
		m.postTranslate(cx, cy); 
		
		canvas.concat(m);
		_camera.restore();    
		
		if(_heading > A_135) {
			m = new Matrix();
			
			_camera.save();
			_camera.rotateZ(180);
			_camera.getMatrix(m);

			cx = 0.5f*_arrowWidth;
			cy = 0.5f*_arrowHeight; 

			m.preTranslate(-cx, -cy);
			m.postTranslate(cx, cy); 
			
			canvas.concat(m);
			_camera.restore(); 
		}
	}
}
