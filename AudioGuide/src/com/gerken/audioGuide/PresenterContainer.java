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

	private SightPresenter _sightPresenter;
	private AudioPlayerPresenter _audioPlayerPresenter;
	private AuxiliaryPresenter _helpPresenter;
	private MainPreferencePresenter _mainPreferencePresenter;
	private RouteMapPresenter _routeMapPresenter;
	
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
		_sightPresenter = new SightPresenter(getCity(), sightView, playerView);
		_sightPresenter.setAudioPlayer(player);
		_sightPresenter.setAudioNotifier(new AndroidMediaPlayerNotifier(_context));
		_sightPresenter.setBitmapLoader(_bitmapLoader);
		_sightPresenter.setApplicationSettingsStorage(_settingsStorage);
		_sightPresenter.setPlayerPanelHidingScheduler(new SchedulerService());
		_sightPresenter.setMediaAssetManager(_assetManager);
		_sightPresenter.setLocationTracker(_defaultLocationTracker);
		_sightPresenter.setLogger(createLogger(SightPresenter.class));
		_sightPresenter.setLongTaskExecutor(_longTaskExecutor);
		_sightPresenter.setLockProvider(_lockProvider);
		
		if(isDemo) {
			DemoSightLookGotInRangeRaiser raiser = 
					new DemoSightLookGotInRangeRaiser(getCity(), new SchedulerService());
			_sightPresenter.setNewSightLookGotInRangeRaiser(raiser);
		}
		else {			
			SightLookFinderByLocation finder = 
					new SightLookFinderByLocation(getCity(), _defaultLocationTracker);
			finder.setLogger(createLogger(SightLookFinderByLocation.class));
			_sightPresenter.setNewSightLookGotInRangeRaiser(finder);
		}
	}
	
	public void initAudioPlayerPresenter(AudioPlayerView playerView, boolean isDemo){
		AudioPlayer player = isDemo ? getDemoAudioPlayer() : _player;
		_audioPlayerPresenter = new AudioPlayerPresenter(playerView, player);
		_audioPlayerPresenter.setMediaAssetManager(_assetManager);        
        _audioPlayerPresenter.setAudioUpdateScheduler(new SchedulerService());
        _audioPlayerPresenter.setAudioRewindScheduler(new SchedulerService());
        _audioPlayerPresenter.setLogger(new Log4JAdapter(AudioPlayerPresenter.class));
        _audioPlayerPresenter.setLongTaskExecutor(_longTaskExecutor);
        _audioPlayerPresenter.setLockProvider(_lockProvider);
        
        if(isDemo) {
			DemoSightLookGotInRangeRaiser raiser = 
					new DemoSightLookGotInRangeRaiser(getCity(), new SchedulerService());
			_audioPlayerPresenter.setNewSightLookGotInRangeRaiser(raiser);
		}
		else {			
			SightLookFinderByLocation finder = 
					new SightLookFinderByLocation(getCity(), _defaultLocationTracker);
			finder.setLogger(createLogger(SightLookFinderByLocation.class));
			_audioPlayerPresenter.setNewSightLookGotInRangeRaiser(finder);
		}
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
		_routeMapPresenter.setLocationTracker(createLocationTracker());
		_routeMapPresenter.setLogger(createLogger(RouteMapPresenter.class));
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
