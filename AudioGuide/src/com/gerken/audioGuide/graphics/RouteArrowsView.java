package com.gerken.audioGuide.graphics;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RouteArrowsView extends View {
	private final float ARROW_WIDTH_RATIO = 0.125f; 
	
	private Paint _arrowPaint;
	//private Path _arrowPath;
	
	private float _tipX = 0.16f;
	private float _tipY = 0.6f;

	public RouteArrowsView(Context context) {		
		super(context);
		
		_arrowPaint = createArrowPaint();
		
		//_arrowPath = 
	}
	
	public RouteArrowsView(Context context, AttributeSet attrs) 
    { 
        super(context, attrs); 
        _arrowPaint = createArrowPaint();
    } 

    public RouteArrowsView(Context context, AttributeSet attrs, int defStyle) 
    { 
        super(context, attrs, defStyle); 
        _arrowPaint = createArrowPaint();

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
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		Path arrow = createArrow(canvas);
		canvas.drawPath(arrow, _arrowPaint);
	}
	
	private Path createArrow(Canvas canvas) {
		float w = getWidth();
		float h = getHeight();
		
		Log.d("RouteArrowsView", String.format("%.1f-%.1f %d", w, h, getPaddingLeft()));
		
		float arrowWidth = w*ARROW_WIDTH_RATIO;
		
		PointF[] points = new PointF[] {
			new PointF(w/2.0f - arrowWidth/2.0f, h),
			new PointF(w/2.0f + arrowWidth/2.0f, h),
			new PointF(_tipX*w, _tipY*h)
		};
		
		Path arrow = new Path();
		arrow.moveTo(points[0].x, points[0].y);
		for(int pIdx=1; pIdx<points.length; pIdx++)
			arrow.lineTo(points[pIdx].x, points[pIdx].y);
		arrow.close();
		return arrow;				
	}
	
	private Paint createArrowPaint(){
		Paint arrowPaint = new Paint();
		arrowPaint.setColor(Color.argb(128, 100, 100, 100));
		arrowPaint.setStyle(Paint.Style.FILL);
		return arrowPaint;
	}
	

}
