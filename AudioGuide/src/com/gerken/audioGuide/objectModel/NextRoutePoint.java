package com.gerken.audioGuide.objectModel;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element(name="nextRoutePoint")
public class NextRoutePoint {
	@Attribute(name="routeId")
	private int _routeId;
	@Attribute(name="heading")
	private short _heading;
	@Attribute(name="name")
	private String _name; 
	
	public NextRoutePoint() {		
	}
	
	public NextRoutePoint(int routeId, short heading, String name) {		
		_routeId = routeId;
		_heading = heading;
		_name = name;
	}
	
	public int getRouteId() {
		return _routeId;
	}
	
	public short getHeading() {
		return _heading;
	}
	
	public String getName() {
		return _name;
	}
}
