package com.gerken.audioGuide.interfaces.listeners;

import com.gerken.audioGuide.containers.Point;

public interface OnMultiTouchListener {
	void onMultiTouchDown(Point<Float>[] touchPoints);
	void onMultiTouchMove(Point<Float>[] touchPoints);
	void onMultiTouchUp();
}
