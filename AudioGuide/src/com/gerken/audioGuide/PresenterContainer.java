package com.gerken.audioGuide;

import android.content.Context;

import com.gerken.audioGuide.interfaces.*;
import com.gerken.audioGuide.interfaces.views.*;
import com.gerken.audioGuide.presenters.*;
import com.gerken.audioGuide.services.AndroidDownscalingBitmapLoader;
import com.gerken.audioGuide.services.Log4JAdapter;
import com.gerken.audioGuide.services.PlainMediaAssetManager;
import com.gerken.audioGuide.services.SharedPreferenceManager;

public class PresenterContainer {
	private Context _context;
	private GuideApplication _application;

	private SightPresenter _sightPresenter;
	private AuxiliaryPresenter _helpPresenter;
	private MainPreferencePresenter _mainPreferencePresenter;
	
	private MediaAssetManager _assetManager;
	private ApplicationSettingsStorage _settingsStorage;
	private DownscalingBitmapLoader _bitmapLoader;
	
	public PresenterContainer(Context ctx, GuideApplication app){
		_context = ctx;
		_application = app;
		
		_assetManager = new PlainMediaAssetManager(ctx);
		_settingsStorage = new SharedPreferenceManager(ctx);
		_bitmapLoader = new AndroidDownscalingBitmapLoader(_assetManager);
	}
	
	
	public void initSightPresenter(SightView sightView, AudioPlayerView playerView, LocationTracker locTracker){
		_sightPresenter = new SightPresenter(_application.getCity(), sightView, playerView);
	}
	
	public void initHelpPresenter(AuxiliaryView helpView){
		_helpPresenter = new AuxiliaryPresenter(helpView, _application.getCity());
		_helpPresenter.setBitmapLoader(_bitmapLoader);
		_helpPresenter.setLogger(createLogger(AuxiliaryPresenter.class));
	}
	
	public void initMainPreferencePresenter(MainPreferenceView mainPreferenceView) {
		_mainPreferencePresenter = new MainPreferencePresenter(_application.getCity(),
				mainPreferenceView, _settingsStorage);
		_mainPreferencePresenter.setBitmapLoader(_bitmapLoader);
		_mainPreferencePresenter.setLogger(createLogger(MainPreferencePresenter.class));
	}
	
	private Logger createLogger(Class cls) {
		return new Log4JAdapter(cls);
	}
	

}
