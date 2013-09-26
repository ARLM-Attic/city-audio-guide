package com.gerken.audioGuide;

import android.content.Context;

import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.presenters.*;
import com.gerken.audioGuide.services.AndroidDownscalingBitmapLoader;
import com.gerken.audioGuide.services.Log4JAdapter;
import com.gerken.audioGuide.services.PlainMediaAssetManager;
import com.gerken.audioGuide.interfaces.MediaAssetManager;
import com.gerken.audioGuide.interfaces.views.*;

public class PresenterContainer {
	private Context _context;
	private GuideApplication _application;

	private SightPresenter _sightPresenter;
	private HelpPresenter _helpPresenter;
	
	private MediaAssetManager _assetManager;
	
	public PresenterContainer(Context ctx, GuideApplication app){
		_context = ctx;
		_application = app;
		
		_assetManager = new PlainMediaAssetManager(ctx);
	}
	
	
	public void initSightPresenter(SightView sightView, AudioPlayerView playerView){
		_sightPresenter = new SightPresenter(sightView, playerView);
	}
	
	public void initHelpPresenter(HelpView helpView){
		_helpPresenter = new HelpPresenter(helpView, _application.getCity());
		_helpPresenter.setBitmapLoader(new AndroidDownscalingBitmapLoader(_assetManager));
		_helpPresenter.setLogger(new Log4JAdapter(HelpPresenter.class));
	}
	

}
