package com.gerken.audioGuide.objectModel;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@Element(name="look")
public class SightLook {
	@Attribute(name="latitude")
	private double _latitude;
	@Attribute(name="longitude")
	private double _longitude;
	
	@Attribute(name="imageName")
	private String _imageName;
	
	@ElementList(name="nextRoutePoints", required=false)
	private List<NextRoutePoint> _nextRoutePoints;
	
	private Sight _sight;
	
	public SightLook() {		
	}
	
	public SightLook(double latitude, double longitude, String imageName) {
		_latitude = latitude;
		_longitude = longitude;
		_imageName = imageName;
		_nextRoutePoints = new ArrayList<NextRoutePoint>();
	}
	
	public String getImageName() {
		return _imageName;
	}
	
	public double getLatitude() {
		return _latitude;
	}
	public double getLongitude() {
		return _longitude;
	}
	
	public List<NextRoutePoint> getNextRoutePoints() {
		return _nextRoutePoints;
	}
	
	
	public Sight getSight() {
		return _sight;
	}
	public void setSight(Sight sight) {
		_sight = sight;
	}	
}
