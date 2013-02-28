package com.gerken.audioGuide;

import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.services.GuideDataManager;

import android.app.Application;
import android.util.Log;

public class GuideApplication extends Application {
	private final String LOG_TAG="AudioGuide";
	
	private City _city = null;
	
	public City getCity() {
		return _city;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		GuideDataManager mgr = new GuideDataManager(getApplicationContext());
		
		try {
			mgr.init();
			_city = mgr.getCity();
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "Guide initialization failed", ex);
		}
	}
}
