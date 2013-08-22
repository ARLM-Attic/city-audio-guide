package com.gerken.audioGuide;

import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.presenters.AudioPlayerPresenter;
import com.gerken.audioGuide.presenters.SightPresenter;
import com.gerken.audioGuide.services.GuideDataManager;

import android.app.Application;
import android.util.Log;
import org.apache.log4j.Level;
import de.mindpipe.android.logging.log4j.LogConfigurator;

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
			configureLog4J();
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "Guide initialization failed", ex);
		}
	}
	
	private void configureLog4J() {
		final LogConfigurator logConfigurator = new LogConfigurator();
        
		String logFileName = getApplicationContext().getFilesDir() +
				java.io.File.separator + "AudioGuide.log";
        logConfigurator.setFileName(logFileName);
        logConfigurator.setRootLevel(Level.DEBUG);
        // Set log level of a specific logger
        logConfigurator.setLevel(SightPresenter.class.getName(), Level.ERROR);
        logConfigurator.setLevel(AudioPlayerPresenter.class.getName(), Level.ERROR);
        logConfigurator.configure();
	}
}
