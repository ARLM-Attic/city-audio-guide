package com.gerken.audioGuide.graphics;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;

public class PlayButtonDrawable extends Drawable {
	
	private float _size;
	
	private StateListDrawable _selector;
	private Drawable _defaultLook;
	private Drawable _pressedLook;
	
	public PlayButtonDrawable(float width, float height) {
		_size = Math.min(width, height);
		
		_defaultLook = createDefaultLook();
		_pressedLook = createDefaultLook();
		_selector = new StateListDrawable();
		_selector.addState(new int[] {android.R.attr.state_enabled}, _defaultLook);
		_selector.addState(new int[] {android.R.attr.state_pressed}, _pressedLook);
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		//_selector.draw(canvas);
		_defaultLook.draw(canvas);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.RGB_888;
	}

	@Override
	public void setAlpha(int alpha) {
		
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
	}
	
	@Override
	protected void onBoundsChange(Rect bounds) {
		// TODO Auto-generated method stub
		super.onBoundsChange(bounds);
	}
	
	private Drawable createDefaultLook() {
		int boundSize = (int)_size;
		GradientDrawable oval = new GradientDrawable(Orientation.BOTTOM_TOP, 
				new int[]{0xCDFFFFFF, 0xCDCCCCCC});

		oval.setGradientType(GradientDrawable.RADIAL_GRADIENT);
		oval.setShape(GradientDrawable.OVAL);
		oval.setBounds(0, 0, boundSize, boundSize);
		oval.setStroke(4, 0xFF4CFF00);
		oval.setGradientRadius(0.5f*_size);
		oval.setGradientCenter(0.3f, 0.3f);
		
		ShapeDrawable playSign = new ShapeDrawable(
				new RegularConvexShape(3, 0.4f*_size, 0));
		Paint psPaint = playSign.getPaint();
		psPaint.setStyle(Style.FILL);
		psPaint.setColor(0xFF4CFF00);
		playSign.setBounds(0, 0, boundSize, boundSize);
		playSign.invalidateSelf();

		Drawable[] layers = new Drawable[]{ oval, playSign };
		LayerDrawable ld = new LayerDrawable(layers);
		ld.setBounds(0, 0, boundSize, boundSize);
		return ld;
	}
}
