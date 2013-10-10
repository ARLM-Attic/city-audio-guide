package com.gerken.audioGuide.objectModel;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element(name="mapBounds")
public class MapBounds {
	@Attribute(name="north")
	private double _north;
	@Attribute(name="west")
	private double _west;
	@Attribute(name="south")
	private double _south;
	@Attribute(name="east")
	private double _east;
	
	public MapBounds() {
		_north = 0.0;
		_west = 0.0;
		_south = 0.0;
		_east = 0.0;
	}
	
	public MapBounds(double north, double west, double south, double east) {
		_north = north;
		_west = west;
		_south = south;
		_east = east;
	}
	
	public double getNorth() {
		return _north;
	}
	public double getWest() {
		return _west;
	}
	public double getSouth() {
		return _south;
	}
	public double getEast() {
		return _east;
	}
}
