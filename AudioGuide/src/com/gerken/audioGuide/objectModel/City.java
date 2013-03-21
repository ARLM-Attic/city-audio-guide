package com.gerken.audioGuide.objectModel;

import java.util.ArrayList;
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
	@ElementList(name="routes")
	private List<Route> _routes;
	
	public City() {		
	}
	
	public City(int id, String name, String mainImageName) {
		_id = id;
		_name = name;
		_mainImageName = mainImageName;
		_sights = new ArrayList<Sight>();
		_routes = new ArrayList<Route>();
	}
	
	public String getName() {
		return _name;
	}
	
	public List<Sight> getSights() {
		return _sights;
	}
	public List<Route> getRoutes() {
		return _routes;
	}
}
