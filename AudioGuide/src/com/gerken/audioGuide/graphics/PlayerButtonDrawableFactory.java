package com.gerken.audioGuide.graphics;

import android.graphics.BlurMaskFilter;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;

public class PlayerButtonDrawableFactory {
	private final int BUTTON_SIGN_MAIN_COLOR = 0xC04CFF00;
	private final float PLAY_SIGN_SIZE_RATIO = 0.8f;
	private final float STOP_SIGN_SIZE_RATIO = 0.5f;
	
	public Drawable createPlayButtonDefaultDrawable(int width, int height) {
		int signSize = (int)(PLAY_SIGN_SIZE_RATIO * (float)Math.min(width, height));
		Drawable playSign = createButtonDrawable(width, height,
				new RegularConvexShape(3, 0), signSize, signSize);
		
		return playSign;
	}
	
	public Drawable createPlayButtonPressedDrawable(int width, int height) {
		int signSize = (int)(PLAY_SIGN_SIZE_RATIO * (float)Math.min(width, height));
        Drawable pauseSign = createButtonDrawable(width, height,
    		new PauseSignShape(), signSize, signSize);
		
		return pauseSign;
	}

	public Drawable createStopButtonDrawable(int width, int height) {	
		int signSize = (int)(STOP_SIGN_SIZE_RATIO * (float)Math.min(width, height));

		return createButtonDrawable(width, height,
				new RectShape(), signSize, signSize);
	}
	
	public Drawable createRewindButtonDrawable(int width, int height) {		
		
		int signSize = (int)(STOP_SIGN_SIZE_RATIO * (float)Math.min(width, height));
		ShapeDrawable sign1 = new ShapeDrawable(
				new RegularConvexShape(3, (float)Math.PI));		
		sign1.setIntrinsicHeight(signSize);
		sign1.setIntrinsicWidth(signSize);
		sign1.setShaderFactory(
				new ButtonSignShaderFactory(signSize, signSize));
		
		Drawable sign2 = sign1.getConstantState().newDrawable();
		
		Drawable[] layers = new Drawable[]{ sign1, sign2 };
		LayerDrawable ld = new LayerDrawable(layers);
		ld.setBounds(0, 0, width, height);
		
		int signDy = (int)( (height - signSize)/2.0f );
		int hwidth = (int)(0.5f*width);
		int corr = (int)(0.25f*signSize);
		ld.setLayerInset(0, hwidth-signSize+corr, signDy+1, hwidth-corr, signDy+1);
		ld.setLayerInset(1, hwidth, signDy+1, hwidth-signSize, signDy+1);
		return ld;
	}
	
	private Drawable createButtonDrawable(int buttonWidth, int buttonHeight,
			Shape shape, int signWidth, int signHeight) {
		ShapeDrawable sign = new ShapeDrawable(shape);
		sign.setIntrinsicHeight(signHeight);
		sign.setIntrinsicWidth(signWidth);
		sign.setShaderFactory(
				new ButtonSignShaderFactory(signWidth, signHeight));
		
		ShapeDrawable sign2 = new ShapeDrawable(shape);
		sign2.setIntrinsicHeight(signHeight);
		sign2.setIntrinsicWidth(signWidth);
		Paint psPaint = sign2.getPaint();
		psPaint.setStyle(Style.STROKE);
		psPaint.setColor(BUTTON_SIGN_MAIN_COLOR);
		psPaint.setAntiAlias(true);
		psPaint.setStrokeJoin(Paint.Join.ROUND);
		psPaint.setStrokeCap(Paint.Cap.ROUND);
		psPaint.setStrokeWidth(1.5f);
		psPaint.setMaskFilter(new BlurMaskFilter(1.5f, BlurMaskFilter.Blur.NORMAL)); 
		
		Drawable[] layers = new Drawable[]{ sign, sign2 };
		LayerDrawable ld = new LayerDrawable(layers);
		ld.setBounds(0, 0, buttonWidth, buttonHeight);
		
		int signDx = (int)( (buttonWidth - signWidth)/2.0f );
		int signDy = (int)( (buttonHeight - signHeight)/2.0f );
		ld.setLayerInset(0, signDx, signDy+1, signDx, signDy+1);
		ld.setLayerInset(1, signDx, signDy+1, signDx+1, signDy+1);
	
		return ld;
	}
	
	private class ButtonSignShaderFactory extends ShapeDrawable.ShaderFactory {
		private final float HIGHLIGHT_CENTER_X = 0.35f;
		private final float HIGHLIGHT_CENTER_Y = 0.3f;
		private float _cx;
		private float _cy;
		private float _r;
		public ButtonSignShaderFactory(float signWidth, float signHeight) {
			_cx=HIGHLIGHT_CENTER_X*signWidth;
			_cy=HIGHLIGHT_CENTER_Y*signHeight;
			
			float rx = signWidth - _cx;
			float ry = signHeight - _cy;
			_r=(float)Math.sqrt(rx*rx+ry*ry);
		}
		@Override
		public Shader resize(int width, int height) {
			return new RadialGradient(_cx, _cy, _r, 
					new int[]{0xC0AEFF8C, BUTTON_SIGN_MAIN_COLOR}, 
					new float[]{0, 1},
					Shader.TileMode.REPEAT);
		}
	}

}