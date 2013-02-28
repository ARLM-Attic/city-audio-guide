package com.gerken.audioGuide.objectModel;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element(name="look")
public class SightLook {
	@Attribute(name="latitude")
	private double _latitude;
	@Attribute(name="longitude")
	private double _longitude;
	
	@Attribute(name="imageName")
	private String _imageName;
	
	private Sight _sight;
	
	public String getImageName() {
		return _imageName;
	}
	
	public double getLatitude() {
		return _latitude;
	}
	public double getLongitude() {
		return _longitude;
	}
	
	
	public Sight getSight() {
		return _sight;
	}
	public void setSight(Sight sight) {
		_sight = sight;
	}
}
