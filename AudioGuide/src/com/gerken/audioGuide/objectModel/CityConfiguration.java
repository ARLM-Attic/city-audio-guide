package com.gerken.audioGuide.objectModel;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element(name="cityConfiguration")
public class CityConfiguration {
	@Attribute(name="outOfRangeImageName")
	private String _outOfRangeImageName;
	@Attribute(name="auxPortraitBackgroundImageName")
	private String _auxPortraitBackgroundImageName;
	@Attribute(name="auxLandscapeBackgroundImageName")
	private String _auxLandscapeBackgroundImageName;
	
	public CityConfiguration() {
		_outOfRangeImageName = "";
		_auxPortraitBackgroundImageName = "";
		_auxLandscapeBackgroundImageName = "";
	}
	
	public CityConfiguration(String outOfRangeImageName,
			String auxPortraitBackgroundImageName, String auxLandscapeBackgroundImageName) {
		_outOfRangeImageName = outOfRangeImageName;
		_auxPortraitBackgroundImageName = auxPortraitBackgroundImageName;
		_auxLandscapeBackgroundImageName = auxLandscapeBackgroundImageName;
	}
	
	public String getOutOfRangeImageName() {
		return _outOfRangeImageName;
	}
	
	public String getAuxPortraitBackgroundImageName() {
		return _auxPortraitBackgroundImageName;
	}
	
	public String getAuxLandscapeBackgroundImageName() {
		return _auxLandscapeBackgroundImageName;
	}

}
