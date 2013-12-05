package com.gerken.audioGuide.controls;

import com.gerken.audioGuide.R;
import com.gerken.audioGuide.graphics.ExitDemoButtonDrawable;
import com.gerken.audioGuide.graphics.ExitDemoButtonShape;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;

public class ExitDemoButton extends Button {
	private static final int DEF_COLOR = 0;
	private static final float DEF_STROKE_WIDTH = 1.0f;
	
	private int _fillColor;
	private int _strokeColor;
	private float _strokeWidth;
	
	public ExitDemoButton(Context context, AttributeSet attrs) 
    { 
        super(context, attrs); 
        init(context, attrs); 
    } 

    public ExitDemoButton(Context context, AttributeSet attrs, int defStyle) 
    { 
        super(context, attrs, defStyle); 
        init(context, attrs); 
    } 
    
    private void init(Context context, AttributeSet attrs) {        
    	TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.ExitDemoButton);
    	_fillColor = arr.getColor(R.styleable.ExitDemoButton_fillColor, DEF_COLOR);
    	_strokeColor = arr.getColor(R.styleable.ExitDemoButton_strokeColor, DEF_COLOR);
    	_strokeWidth = arr.getDimension(R.styleable.ExitDemoButton_strokeWidth, DEF_STROKE_WIDTH);
    	arr.recycle();
    	
    	ExitDemoButtonDrawable d= new ExitDemoButtonDrawable(new ExitDemoButtonShape());
    	d.setFillColor(_fillColor);
    	d.setStrokeColor(_strokeColor);
    	d.setStrokeWidth(_strokeWidth);
    	setBackgroundDrawable(d);
    }
}
