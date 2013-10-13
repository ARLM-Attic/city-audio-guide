package com.gerken.audioGuide;

import android.content.Context;

import com.gerken.audioGuide.interfaces.*;
import com.gerken.audioGuide.interfaces.views.*;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.presenters.*;
import com.gerken.audioGuide.services.AndroidDownscalingBitmapLoader;
import com.gerken.audioGuide.services.AndroidLocationManagerFacade;
import com.gerken.audioGuide.services.AndroidMediaPlayerFacade;
import com.gerken.audioGuide.services.AndroidMediaPlayerNotifier;
import com.gerken.audioGuide.services.Log4JAdapter;
import com.gerken.audioGuide.services.PlainMediaAssetManager;
import com.gerken.audioGuide.services.SchedulerService;
import com.gerken.audioGuide.services.SharedPreferenceManager;
import com.gerken.audioGuide.services.SightLookFinderByLocation;

public class PresenterContainer {
	private Context _context;
	private GuideApplication _application;

	private SightPresenter _sightPresenter;
	private AudioPlayerPresenter _audioPlayerPresenter;
	private AuxiliaryPresenter _helpPresenter;
	private MainPreferencePresenter _mainPreferencePresenter;
	private RouteMapPresenter _routeMapPresenter;
	
	private MediaAssetManager _assetManager;
	private ApplicationSettingsStorage _settingsStorage;
	private DownscalingBitmapLoader _bitmapLoader;
	private AudioPlayer _player;
	private LocationTracker _locationTracker;
	
	public PresenterContainer(Context ctx, GuideApplication app){
		_context = ctx;
		_application = app;
		
		_assetManager = new PlainMediaAssetManager(ctx);
		_settingsStorage = new SharedPreferenceManager(ctx);
		_bitmapLoader = new AndroidDownscalingBitmapLoader(_assetManager);
		_player = new AndroidMediaPlayerFacade();
		
		AndroidLocationManagerFacade locMgr = new AndroidLocationManagerFacade(ctx);
		locMgr.setLogger(createLogger(AndroidLocationManagerFacade.class));
		_locationTracker = locMgr;		
	}
	
	
	public void initSightPresenter(SightView sightView, AudioPlayerView playerView){
		_sightPresenter = new SightPresenter(getCity(), sightView, playerView);
		_sightPresenter.setAudioPlayer(_player);
		_sightPresenter.setAudioNotifier(new AndroidMediaPlayerNotifier(_context));
		_sightPresenter.setBitmapLoader(_bitmapLoader);
		_sightPresenter.setApplicationSettingsStorage(_settingsStorage);
		_sightPresenter.setNewSightLookGotInRangeRaiser(
				new SightLookFinderByLocation(getCity(), _locationTracker));
		_sightPresenter.setPlayerPanelHidingScheduler(new SchedulerService());
		_sightPresenter.setMediaAssetManager(_assetManager);
        //_presenter.setLogger(new DefaultLoggingAdapter("SightPresenter"));
		_sightPresenter.setLocationTracker(_locationTracker);
		_sightPresenter.setLogger(createLogger(SightPresenter.class));
	}
	
	public void initAudioPlayerPresenter(AudioPlayerView playerView){
		_audioPlayerPresenter = new AudioPlayerPresenter(playerView, _player);
		_audioPlayerPresenter.setMediaAssetManager(_assetManager);        
        _audioPlayerPresenter.setAudioUpdateScheduler(new SchedulerService());
        _audioPlayerPresenter.setAudioRewindScheduler(new SchedulerService());
        _audioPlayerPresenter.setNewSightLookGotInRangeRaiser(
        		new SightLookFinderByLocation(getCity(), _locationTracker));
        _audioPlayerPresenter.setLogger(new Log4JAdapter(AudioPlayerPresenter.class));
	}
	
	public void initHelpPresenter(AuxiliaryView helpView){
		_helpPresenter = new AuxiliaryPresenter(helpView, getCity());
		_helpPresenter.setBitmapLoader(_bitmapLoader);
		_helpPresenter.setLogger(createLogger(AuxiliaryPresenter.class));
	}
	
	public void initMainPreferencePresenter(MainPreferenceView mainPreferenceView) {
		_mainPreferencePresenter = new MainPreferencePresenter(getCity(),
			mainPreferenceView, _settingsStorage);
		_mainPreferencePresenter.setBitmapLoader(_bitmapLoader);
		_mainPreferencePresenter.setLogger(createLogger(MainPreferencePresenter.class));
	}
	
	public void initRouteMapPresenter(RouteMapView routeMapView) {
		_routeMapPresenter = new RouteMapPresenter(getCity(), routeMapView,
			_assetManager);
		_routeMapPresenter.setLocationTracker(_locationTracker);
		_routeMapPresenter.setLogger(createLogger(RouteMapPresenter.class));
	}
	
	private Logger createLogger(Class cls) {
		return new Log4JAdapter(cls);
	}
	
	private City getCity() {
		return _application.getCity();
	}
}
