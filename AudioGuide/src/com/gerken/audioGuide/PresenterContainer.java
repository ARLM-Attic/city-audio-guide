package com.gerken.audioGuide;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.content.Context;

import com.gerken.audioGuide.interfaces.*;
import com.gerken.audioGuide.interfaces.views.*;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.presenters.*;
import com.gerken.audioGuide.services.AndroidDownscalingBitmapLoader;
import com.gerken.audioGuide.services.AndroidLocationManagerFacade;
import com.gerken.audioGuide.services.AndroidMediaPlayerFacade;
import com.gerken.audioGuide.services.AndroidMediaPlayerNotifier;
import com.gerken.audioGuide.services.DefaultLockProvider;
import com.gerken.audioGuide.services.DemoSightLookGotInRangeRaiser;
import com.gerken.audioGuide.services.Log4JAdapter;
import com.gerken.audioGuide.services.PlainMediaAssetManager;
import com.gerken.audioGuide.services.SchedulerService;
import com.gerken.audioGuide.services.SharedPreferenceManager;
import com.gerken.audioGuide.services.SightLookFinderByLocation;

public class PresenterContainer {
	private Context _context;
	private GuideApplication _application;
	
	private MediaAssetManager _assetManager;
	private ApplicationSettingsStorage _settingsStorage;
	private DownscalingBitmapLoader _bitmapLoader;
	private AudioPlayer _player;
	private AudioPlayer _demoPlayer;
	private LocationTracker _defaultLocationTracker;
	private Executor _longTaskExecutor;
	private LockProvider _lockProvider;
	
	public PresenterContainer(Context ctx, GuideApplication app){
		_context = ctx;
		_application = app;
		
		_assetManager = new PlainMediaAssetManager(ctx);
		_settingsStorage = new SharedPreferenceManager(ctx);
		_bitmapLoader = new AndroidDownscalingBitmapLoader(_assetManager);
		_player = new AndroidMediaPlayerFacade();

		_defaultLocationTracker = createLocationTracker();
		_longTaskExecutor = Executors.newCachedThreadPool();
		_lockProvider = new DefaultLockProvider();
	}
	
	private synchronized AudioPlayer getDemoAudioPlayer() {
		if(_demoPlayer == null)
			_demoPlayer = new AndroidMediaPlayerFacade();
		return _demoPlayer;
	}	
	
	public void initSightPresenter(SightView sightView, AudioPlayerView playerView, boolean isDemo){
		AudioPlayer player = isDemo ? getDemoAudioPlayer() : _player;
		SightPresenter sightPresenter = new SightPresenter(getCity(), sightView, playerView);
		sightPresenter.setAudioPlayer(player);
		sightPresenter.setAudioNotifier(new AndroidMediaPlayerNotifier(_context));
		sightPresenter.setBitmapLoader(_bitmapLoader);
		sightPresenter.setApplicationSettingsStorage(_settingsStorage);
		sightPresenter.setPlayerPanelHidingScheduler(new SchedulerService());
		sightPresenter.setMediaAssetManager(_assetManager);
		sightPresenter.setLocationTracker(_defaultLocationTracker);
		sightPresenter.setLogger(createLogger(SightPresenter.class));
		sightPresenter.setLongTaskExecutor(_longTaskExecutor);
		sightPresenter.setLockProvider(_lockProvider);
		
		if(isDemo) {
			DemoSightLookGotInRangeRaiser raiser = 
					new DemoSightLookGotInRangeRaiser(getCity(), new SchedulerService());
			sightPresenter.setNewSightLookGotInRangeRaiser(raiser);
		}
		else {			
			SightLookFinderByLocation finder = 
					new SightLookFinderByLocation(getCity(), _defaultLocationTracker);
			finder.setLogger(createLogger(SightLookFinderByLocation.class));
			sightPresenter.setNewSightLookGotInRangeRaiser(finder);
		}
		sightView.setPresenter(sightPresenter);
	}	
	
	public void initAudioPlayerPresenter(AudioPlayerView playerView, boolean isDemo){
		AudioPlayer player = isDemo ? getDemoAudioPlayer() : _player;
		AudioPlayerPresenter audioPlayerPresenter = new AudioPlayerPresenter(playerView, player);
		audioPlayerPresenter.setMediaAssetManager(_assetManager);        
        audioPlayerPresenter.setAudioUpdateScheduler(new SchedulerService());
        audioPlayerPresenter.setAudioRewindScheduler(new SchedulerService());
        audioPlayerPresenter.setLogger(new Log4JAdapter(AudioPlayerPresenter.class));
        audioPlayerPresenter.setLongTaskExecutor(_longTaskExecutor);
        audioPlayerPresenter.setLockProvider(_lockProvider);
        
        if(isDemo) {
			DemoSightLookGotInRangeRaiser raiser = 
					new DemoSightLookGotInRangeRaiser(getCity(), new SchedulerService());
			audioPlayerPresenter.setNewSightLookGotInRangeRaiser(raiser);
		}
		else {			
			SightLookFinderByLocation finder = 
					new SightLookFinderByLocation(getCity(), _defaultLocationTracker);
			finder.setLogger(createLogger(SightLookFinderByLocation.class));
			audioPlayerPresenter.setNewSightLookGotInRangeRaiser(finder);
		}
        playerView.setPresenter(audioPlayerPresenter);
	}
	
	public void initHelpPresenter(AuxiliaryView helpView){
		AuxiliaryPresenter auxPresenter = new AuxiliaryPresenter(helpView, getCity());
		auxPresenter.setBitmapLoader(_bitmapLoader);
		auxPresenter.setLogger(createLogger(AuxiliaryPresenter.class));
		helpView.setPresenter(auxPresenter);
	}
	
	public void initMainPreferencePresenter(MainPreferenceView mainPreferenceView) {
		MainPreferencePresenter mainPreferencePresenter = new MainPreferencePresenter(getCity(),
			mainPreferenceView, _settingsStorage);
		mainPreferencePresenter.setBitmapLoader(_bitmapLoader);
		mainPreferencePresenter.setLogger(createLogger(MainPreferencePresenter.class));
		mainPreferenceView.setPresenter(mainPreferencePresenter);
	}
	
	public void initRouteMapPresenter(RouteMapView routeMapView) {
		RouteMapPresenter routeMapPresenter = new RouteMapPresenter(getCity(), routeMapView,
			_assetManager);
		routeMapPresenter.setLocationTracker(createLocationTracker());
		routeMapPresenter.setLogger(createLogger(RouteMapPresenter.class));
		routeMapView.setPresenter(routeMapPresenter);
	}
	
	private Logger createLogger(Class cls) {
        //return new DefaultLoggingAdapter(cls.getName());
		return new Log4JAdapter(cls);
	}
	
	private LocationTracker createLocationTracker() {
		AndroidLocationManagerFacade locMgr = new AndroidLocationManagerFacade(_context);
		locMgr.setLogger(createLogger(AndroidLocationManagerFacade.class));
		return locMgr;
	}
	
	private City getCity() {
		return _application.getCity();
	}
}
