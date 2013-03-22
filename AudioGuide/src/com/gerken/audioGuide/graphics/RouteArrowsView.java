package com.gerken.audioGuide.graphics;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RouteArrowsView extends View {
	private final float HORIZON_HEIGHT = 0.333333f; 
	
	private final float TIP_HEIGHT_RATIO = 0.5f;
	private final float TAIL_WIDTH_RATIO = 0.5f;
	
	private Paint _arrowPaint;
	
	private float _tipX = 0.4f;
	private float _tipY = 0.4f;
	
	private float _arrowWidth = 40.0f;
	private float _arrowHeight = 60.0f;
	private float _heading = 0.0f;
	
	private Camera _camera;

	public RouteArrowsView(Context context) {		
		super(context);
		
		_arrowPaint = createArrowPaint();
		_camera = new Camera();
	}
	
	public RouteArrowsView(Context context, AttributeSet attrs) 
    { 
        super(context, attrs); 
        _arrowPaint = createArrowPaint();
        _camera = new Camera();
    } 

    public RouteArrowsView(Context context, AttributeSet attrs, int defStyle) 
    { 
        super(context, attrs, defStyle); 
        _arrowPaint = createArrowPaint();
        _camera = new Camera();
    } 
    
    public void setHeading(float heading) {
    	_tipX = (float)(0.5+0.375*Math.sin(heading));
    	_tipY = 1.0f-HORIZON_HEIGHT;
    	_heading = heading;
    }
    
    public void setTipX(float v){
    	validateTipCoordinate(v);
    	_tipX = v;
    }
    public void setTipY(float v){
    	validateTipCoordinate(v);
    	_tipY = v;
    }
    
    private void validateTipCoordinate(float coord) {
    	if(coord<0.0f || coord>1.0f)
    		throw new IllegalArgumentException("The value must be between 0.0 and 1.0");
    }
    
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Path arrow = createArrow(canvas);
		canvas.save();
		applyPerspective(canvas);
		canvas.drawPath(arrow, _arrowPaint);
		canvas.restore();
	}
	
	private Path createArrow(Canvas canvas) {
		float tx = _tipX*getWidth();
		float ty = _tipY*getHeight();
		float left = tx - 0.5f*_arrowWidth;
		float top = ty;
		float right = tx + 0.5f*_arrowWidth;
		float bottom = ty + _arrowHeight;
		Log.d("RouteArrowsView", String.format("tip %.1f,%.1f", tx, ty));
		
		canvas.translate(left, top);
		canvas.clipRect(0, 0, _arrowWidth, _arrowHeight, Region.Op.REPLACE);
		Log.d("RouteArrowsView", String.format("%.1f,%.1f-%.1f,%.1f", left, top, right, bottom));
		
		float w = _arrowWidth;
		float h = _arrowHeight;
		
		float tipBottomY = TIP_HEIGHT_RATIO*h;
		float tailHalfWidth = 0.5f*TAIL_WIDTH_RATIO*w;
		float cx = 0.5f*w;
		Path path = new Path();
		path.moveTo(0.5f*w, 0);
		path.lineTo(w, tipBottomY);
		path.lineTo(cx+tailHalfWidth, tipBottomY);
		float tailHeight = h-tipBottomY;
		path.rLineTo(0, tailHeight);
		path.rLineTo(-TAIL_WIDTH_RATIO*w, 0);
		path.rLineTo(0, -tailHeight);
		path.lineTo(0, tipBottomY);
		path.close();
		
		return path;				
	}
	
	private Paint createArrowPaint(){
		Paint arrowPaint = new Paint();
		arrowPaint.setColor(0x904AFF00);
		arrowPaint.setStyle(Paint.Style.FILL);
		return arrowPaint;
	}
	

	public void applyPerspective(Canvas canvas) {
		Matrix m = new Matrix();
		
		_camera.save();
		_camera.rotateY(-(int)(180.0*_heading/Math.PI));
		_camera.rotateX(70);		
		_camera.rotateZ(0);
		_camera.getMatrix(m);

		float cx = 0.5f*_arrowWidth;
		float cy = 0.5f*_arrowHeight; 
		m.preTranslate(-cx, -cy);
		m.postTranslate(cx, cy); 
		
		canvas.concat(m);
		_camera.restore();    
	}
}
