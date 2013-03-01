package com.gerken.audioGuide.objectModel;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element(name="route")
public class Route {
	@Attribute(name="id")
	private int _id;
	@Attribute(name="name")
	private String _name;
	
	
	public int getId() {
		return _id;
	}
	public String getName() {
		return _name;
	}
}
