package com.gerken.audioGuide.graphics;

import com.gerken.audioGuide.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

public class RouteArrowsView extends View {

	private final float TIP_HEIGHT_RATIO = 0.5f;
	private final float TAIL_WIDTH_RATIO = 0.5f;
	
	private final float DEF_ARROW_WIDTH = 40;
	private final float DEF_ARROW_HEIGHT = 60;
	
	private final int COLOR_FILL   = 0xA000991E;
	private final int COLOR_STROKE = 0xC0CCFFD6;
	
	private Paint _arrowFillPaint;
	private Paint _arrowStrokePaint;
	
	private float _tipX = 0.4f;
	private float _tipY = 0.4f;
	
	private float _arrowWidth = DEF_ARROW_WIDTH;
	private float _arrowHeight = DEF_ARROW_HEIGHT;
	private float _arrowHeightAdjusted = DEF_ARROW_HEIGHT;
	
	private float _heading = 0.0f;
	
	private float _arrowLeft = 0;
	private float _arrowTop = 0;
	
	private float _xRotationAngle = 0;
	
	private Camera _camera;

	public RouteArrowsView(Context context) {		
		super(context);
		
		_arrowFillPaint = createArrowFillPaint();
		_arrowStrokePaint = createArrowStrokePaint();
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
    	_arrowFillPaint = createArrowFillPaint();
    	_arrowStrokePaint = createArrowStrokePaint();
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
		_arrowLeft = tx - 0.5f*_arrowWidth;
		_arrowTop = _tipY * getHeight();
		float arrowProjectionMaxHeight = getHeight() - getPaddingBottom() - _arrowTop;

		double ax = Math.acos(1.6*horizon);
		_xRotationAngle = (float)(180.0*ax/Math.PI);
		
		float arrowHeightMax = 
				calculateHeightFromProjection(_arrowWidth, arrowProjectionMaxHeight, ax, heading);
		if(_arrowHeight > arrowHeightMax)
			_arrowHeightAdjusted = arrowHeightMax;
		else {
			_arrowHeightAdjusted = _arrowHeight;
			float arrowProjectionHeight = 
					calculateProjectionHeight(_arrowWidth, _arrowHeight, ax, heading);
			float arrowTopCorrection = (arrowProjectionMaxHeight - arrowProjectionHeight) / 2.0f;
			_arrowTop += (float)arrowTopCorrection;
		}
    }
    
    private float calculateProjectionHeight(float width, float height, double ax, double az) {
    	az = mendAngle(az);
    	return (float)( (height*Math.cos(az)+width*Math.sin(az))*Math.cos(ax) );    	
    }
    
    private float calculateHeightFromProjection(float width, float height, double ax, double az) {
    	az = mendAngle(az);
    	return (float)( (height/Math.cos(ax)-width*Math.sin(az))/Math.cos(az) );
    }
    
    private double mendAngle(double angle) {
    	final double HALF_PI = Math.PI / 2.0;
    	final double MINUS_HALF_PI = -HALF_PI;
    	if(angle < 0) {
    		if(angle < MINUS_HALF_PI)
    			return (Math.PI + angle);
    		else
    			return -angle;
    	}
    	else {
    		if(angle > HALF_PI)
    			return Math.PI - angle;
    	}
    	return angle;
    }
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Path arrow = createArrow(_arrowWidth, _arrowHeightAdjusted);
		canvas.save();
		canvas.translate(_arrowLeft, _arrowTop);
		applyPerspective(canvas);		
		canvas.drawPath(arrow, _arrowFillPaint);
		canvas.drawPath(arrow, _arrowStrokePaint);
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
		
		Matrix rm = new Matrix();
		float dZ = (float)(180.0*_heading/Math.PI);
		rm.setRotate(dZ, 0.5f*w, 0.5f*h);
		path.transform(rm);
		
		return path;				
	}
	
	private Paint createArrowFillPaint(){
		Paint arrowPaint = new Paint();
		arrowPaint.setColor(COLOR_FILL);
		arrowPaint.setStyle(Paint.Style.FILL);
		arrowPaint.setAntiAlias(true);
		
		return arrowPaint;
	}
	
	private Paint createArrowStrokePaint(){
		Paint arrowPaint = new Paint();
		arrowPaint.setStyle(Paint.Style.STROKE);
		arrowPaint.setAntiAlias(true);
		arrowPaint.setColor(COLOR_STROKE);
		arrowPaint.setStrokeJoin(Paint.Join.ROUND);
		arrowPaint.setStrokeCap(Paint.Cap.ROUND);
		arrowPaint.setStrokeWidth(3.5f);
		arrowPaint.setMaskFilter(new BlurMaskFilter(1.5f, BlurMaskFilter.Blur.NORMAL));
		
		return arrowPaint;
	}	

	public void applyPerspective(Canvas canvas) {
		Matrix m = new Matrix();
		
		_camera.save();
		_camera.rotateY(0);
		_camera.rotateX(_xRotationAngle);		
		_camera.rotateZ(0);
		
		float tx = (float)(_arrowWidth*Math.sin(-_heading));
		_camera.translate(tx, 0, 0);
		
		_camera.getMatrix(m);

		float cx = 0.5f*_arrowWidth;
		m.preTranslate(-cx, 0);
		m.postTranslate(cx, 0);
		
		canvas.concat(m);
		_camera.restore();    
	}
}
