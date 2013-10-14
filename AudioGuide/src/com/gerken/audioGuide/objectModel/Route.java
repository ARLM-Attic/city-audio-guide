package com.gerken.audioGuide.objectModel;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element(name="route")
public class Route {
	@Attribute(name="id")
	private int _id;
	@Attribute(name="name")
	private String _name;
	@Element(name="mapBounds", required=false)
	private MapBounds _mapBounds;
	
	public Route() {		
	}
	
	public Route(int id, String name) {
		_id=id;
		_name=name;
	}
	
	public int getId() {
		return _id;
	}
	public String getName() {
		return _name;
	}
	public MapBounds getMapBounds() {
		return _mapBounds;
	}
	public void setMapBounds(MapBounds mapBounds){
		_mapBounds = mapBounds;
	}
}
