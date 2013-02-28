package com.gerken.audioGuide.objectModel;

import java.util.List;

import org.simpleframework.xml.*;

@Element(name="sight")
public class Sight {
	@Attribute(name="id")
	private int _id;
	@Attribute(name="name")
	private String _name;
	
	@Attribute(name="audioName")
	private String _audioName;
	
	@ElementList(name="looks")
	private List<SightLook> _looks;
	
	public int getId(){
		return _id;
	}
	
	public String getName() {
		return _name;
	}

	public String getAudioName() {
		return _audioName;
	}

	public List<SightLook> getSightLooks() {
		return _looks;
	}
}
