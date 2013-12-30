package com.gerken.audioGuide.containers;

public class Point<T> {
	private T _x;
	private T _y;
	
	public Point(T x, T y) {
		_x = x;
		_y = y;
	}
	
	public T getX() {
		return _x;
	}
	public T getY() {
		return _y;
	}
}
