package com.gerken.audioGuide.interfaces;

import com.gerken.audioGuide.containers.Point;

public interface OnMultiTouchListener {
	void onMultiTouchDown(Point<Float>[] touchPoints);
	void onMultiTouchMove(Point<Float>[] touchPoints);
	void onMultiTouchUp();
}
