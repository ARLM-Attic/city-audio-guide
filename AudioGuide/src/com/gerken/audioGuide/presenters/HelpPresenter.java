package com.gerken.audioGuide.presenters;

import com.gerken.audioGuide.graphics.BitmapDownscalingResult;
import com.gerken.audioGuide.interfaces.DownscalingBitmapLoader;
import com.gerken.audioGuide.interfaces.Logger;
import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.views.HelpView;
import com.gerken.audioGuide.objectModel.City;

public class HelpPresenter {
	private HelpView _helpView;
	private City _city;
	
	private DownscalingBitmapLoader _bitmapLoader;
	private Logger _logger;

	private OnEventListener _helpViewInitializedListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			handleHelpViewInitialized();
		}
	};
	
	public HelpPresenter(HelpView helpView, City city){
		_helpView = helpView;
		_city = city;
		_helpView.addViewLayoutCompleteListener(_helpViewInitializedListener);
	}
	
	public void setBitmapLoader(DownscalingBitmapLoader bitmapLoader) {
		_bitmapLoader = bitmapLoader;
	}
	
	public void setLogger(Logger logger) {
		_logger = logger;
	}
	
	private void handleHelpViewInitialized(){
		BitmapDownscalingResult backgroundImage = null;
		try {
			backgroundImage = _bitmapLoader.load(
				_city.getHelpBackgroundImageName(),	_helpView.getWidth(), _helpView.getHeight());
		}
		catch(Exception ex) {
			logError("Unable to set the background image", ex);
		}
		if(backgroundImage != null)		
			_helpView.setBackgroundImage(backgroundImage);
	}
	
	private void logError(String message, Throwable ex) {
		if(_logger != null)
			_logger.logError(message, ex);
	}
}
