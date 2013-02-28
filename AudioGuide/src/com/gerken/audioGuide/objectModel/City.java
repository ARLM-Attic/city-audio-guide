package com.gerken.audioGuide.objectModel;

import java.util.List;

import org.simpleframework.xml.*;

@Root(name="city")
public class City {
	@Attribute(name="id")
	private int _id;
	@Attribute(name="name")
	private String _name;
	@Attribute(name="mainImageName")
	private String _mainImageName;
	
	@ElementList(name="sights")
	private List<Sight> _sights;
	
	public String getName() {
		return _name;
	}
	
	public List<Sight> getSights() {
		return _sights;
	}
}
