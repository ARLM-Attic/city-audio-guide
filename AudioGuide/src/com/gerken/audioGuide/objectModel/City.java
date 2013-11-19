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
	
	@Element(name="cityConfiguration")
	private CityConfiguration _config;	
	
	@ElementList(name="sights")
	private List<Sight> _sights;
	@ElementList(name="routes")
	private List<Route> _routes;
	
	public City() {	
		_sights = new ArrayList<Sight>();
		_routes = new ArrayList<Route>();
	}
	
	public City(int id, String name) {
		_id = id;
		_name = name;
		_config = new CityConfiguration();
		_sights = new ArrayList<Sight>();
		_routes = new ArrayList<Route>();
	}
	
	public City(int id, String name, CityConfiguration cityConfiguration) {
		_id = id;
		_name = name;
		_config = cityConfiguration;
		_sights = new ArrayList<Sight>();
		_routes = new ArrayList<Route>();
	}
	
	public String getName() {
		return _name;
	}
	
	public CityConfiguration getConfiguration() {
		return _config;
	}
	
	public List<Sight> getSights() {
		return _sights;
	}
	public List<Route> getRoutes() {
		return _routes;
	}	
}
