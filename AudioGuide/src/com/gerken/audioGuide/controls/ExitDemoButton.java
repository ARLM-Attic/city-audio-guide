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
	
	private int _fillColor;
	private int _strokeColor;
	
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
    	arr.recycle();
    	
    	setBackgroundDrawable(new ExitDemoButtonDrawable(new ExitDemoButtonShape(), _fillColor, _strokeColor));
    }
}
