package com.gerken.audioGuide.services;

import java.io.InputStream;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.objectModel.Sight;
import com.gerken.audioGuide.objectModel.SightLook;

import android.content.Context;
import android.content.res.AssetManager;

public class GuideDataManager {
	private Context _context;
	private City _city = null;
	
	public GuideDataManager(Context context){
		_context = context;
	}
	
	public void init() throws Exception {
		AssetManager am = _context.getAssets();
		InputStream guideDataStream = am.open("guide.xml");
		Serializer serializer = new Persister();
		_city = serializer.read(City.class, guideDataStream);
		postprocessCityData();
	}
	
	public City getCity() throws Exception {
		if(_city != null)
			return _city;
		throw new Exception("City was not initialized");
	}
	
	private void postprocessCityData(){
		for(Sight sight : _city.getSights()) {
			for(SightLook look : sight.getSightLooks())
				look.setSight(sight);
		}
	}
	
	
}
