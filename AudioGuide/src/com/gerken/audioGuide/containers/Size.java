package com.gerken.audioGuide.containers;

public class Size<T> {
	private T _width;
	private T _height;
	
	public Size(T width, T height) {
		_width = width;
		_height = height;
	}
	
	public T getWidth() {
		return _width;
	}
	public T getHeight() {
		return _height;
	}
}
