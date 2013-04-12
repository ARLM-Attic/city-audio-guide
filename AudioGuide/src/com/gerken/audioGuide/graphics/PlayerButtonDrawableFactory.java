package com.gerken.audioGuide.graphics;

import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;

public class PlayerButtonDrawableFactory {
	private final int PLAY_BUTTON_SIGN_COLOR = 0xFF4CFF00;
	private final float STOP_SIGN_SIZE_RATIO = 0.4f;
	
	public Drawable createPlayButtonDefaultDrawable(int width, int height) {
		Drawable playSign = createPlayButtonDrawable(
				new RegularConvexShape(3, 0.4f*width, 0), width, height);
		
		ShapeDrawable shadow = new ShapeDrawable(new ShadowShape());
		shadow.setIntrinsicHeight(height);
		shadow.setIntrinsicWidth(width);
		Paint psPaint = shadow.getPaint();
		psPaint.setStyle(Style.FILL);
		psPaint.setColor(0x40666666);
		//psPaint.setColor(0xC00080FF);
		
		Drawable[] layers = new Drawable[]{ shadow, playSign };
		LayerDrawable ld = new LayerDrawable(layers);
		ld.setBounds(0, 0, width, height);
		
		return ld;
	}
	
	public Drawable createPlayButtonPressedDrawable(int width, int height) {
        Drawable pauseSign = createPlayButtonDrawable(
    		new PauseSignShape(0.4f), width, height);
		
		return pauseSign;
	}
	
	public Drawable createStopButtonDrawable(int width, int height) {
		ShapeDrawable button = new ShapeDrawable(new RectShape());		
		button.setIntrinsicHeight(height);
		button.setIntrinsicWidth(width);
		/*
		button.setBounds(0, 0, width, height);
		*/
		button.setShaderFactory(new ShapeDrawable.ShaderFactory() {			
			@Override
			public Shader resize(int width, int height) {
				return new LinearGradient(0, 0, 0, height, 
						new int[]{0xCDCCCCCC, 0xCDFFFFFF, 0xCDCCCCCC}, 
						new float[]{0, 0.3f, 1},
						Shader.TileMode.REPEAT);
			}
		});
		
		
		ShapeDrawable sign = new ShapeDrawable(new RectShape());
		int signSize = (int)(STOP_SIGN_SIZE_RATIO * (float)Math.min(width, height));
		sign.setIntrinsicHeight(signSize);
		sign.setIntrinsicWidth(signSize);
		Paint psPaint = sign.getPaint();
		psPaint.setStyle(Style.FILL);
		psPaint.setColor(PLAY_BUTTON_SIGN_COLOR);
		
		Drawable[] layers = new Drawable[]{ button, sign };
		LayerDrawable ld = new LayerDrawable(layers);
		ld.setBounds(0, 0, width, height);
		ld.setLayerInset(0, 0, 0, 0, 0);
		
		int signDx = (int)( (width - signSize)/2.0f );
		int signDy = (int)( (height - signSize)/2.0f );
		//ld.setLayerInset(1, signDx, 10, signDx, 9);
		ld.setLayerInset(1, signDx, signDy+1, signDx, signDy+1);
		return ld;
	}
	
	private Drawable createPlayButtonDrawable(Shape shape, int width, int height) {
		ShapeDrawable sign = new ShapeDrawable(shape);
		sign.setIntrinsicHeight(height);
		sign.setIntrinsicWidth(width);
		Paint psPaint = sign.getPaint();
		psPaint.setStyle(Style.FILL);
		psPaint.setColor(PLAY_BUTTON_SIGN_COLOR);		
	
		return sign;
	}

}
