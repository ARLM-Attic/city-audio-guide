package com.gerken.audioGuide.objectModel;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element(name="nextRoutePoint")
public class NextRoutePoint {
	@Attribute(name="routeId")
	private int _routeId;
	@Attribute(name="heading")
	private short _heading;
	@Attribute(name="horizon")
	private byte _horizon;
	@Attribute(name="name")
	private String _name; 
	
	public NextRoutePoint() {		
	}
	
	public NextRoutePoint(int routeId, short heading, byte horizon, String name) {		
		_routeId = routeId;
		_heading = heading;
		_horizon = horizon;
		_name = name;
	}
	
	public int getRouteId() {
		return _routeId;
	}
	
	public short getHeading() {
		return _heading;
	}
	
	public byte getHorizon() {
		return _horizon;
	}
	
	public String getName() {
		return _name;
	}
}
