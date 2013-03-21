package com.gerken.audioGuide.objectModel;

import java.util.ArrayList;
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
	
	public Sight() {		
	}
	
	public Sight(int id, String name, String audioName) {
		_id = id;
		_name = name;
		_audioName = audioName;
		_looks = new ArrayList<SightLook>();
	}
	
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
	
	public void addLook(SightLook look) {
		_looks.add(look);
		look.setSight(this);
	}
}
