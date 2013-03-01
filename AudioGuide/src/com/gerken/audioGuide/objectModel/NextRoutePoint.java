package com.gerken.audioGuide.objectModel;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element(name="nextRoutePoint")
public class NextRoutePoint {
	@Attribute(name="routeId")
	private int _routeId;
	@Attribute(name="heading")
	private short _heading;
}
