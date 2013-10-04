package com.gerken.audioGuide.presenters;

import com.gerken.audioGuide.graphics.BitmapDownscalingResult;
import com.gerken.audioGuide.interfaces.DownscalingBitmapLoader;
import com.gerken.audioGuide.interfaces.Logger;
import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.views.AuxiliaryView;
import com.gerken.audioGuide.objectModel.City;

public class AuxiliaryPresenter {
	private AuxiliaryView _auxView;
	private City _city;
	
	private DownscalingBitmapLoader _bitmapLoader;
	private Logger _logger;

	private OnEventListener _viewLayoutCompleteListener = new OnEventListener() {		
		@Override
		public void onEvent() {
			handleViewLayoutComplete();
		}
	};
	
	public AuxiliaryPresenter(AuxiliaryView view, City city){
		_auxView = view;
		_city = city;
		_auxView.addViewLayoutCompleteListener(_viewLayoutCompleteListener);
	}
	
	public void setBitmapLoader(DownscalingBitmapLoader bitmapLoader) {
		_bitmapLoader = bitmapLoader;
	}
	
	public void setLogger(Logger logger) {
		_logger = logger;
	}
	
	private void handleViewLayoutComplete(){
		BitmapDownscalingResult backgroundImage = null;
		try {
			backgroundImage = _bitmapLoader.load(
				_city.getHelpBackgroundImageName(),	_auxView.getWidth(), _auxView.getHeight());
		}
		catch(Exception ex) {
			logError("Unable to set the background image", ex);
		}
		if(backgroundImage != null)		
			_auxView.setBackgroundImage(backgroundImage);
	}
	
	private void logError(String message, Throwable ex) {
		if(_logger != null)
			_logger.logError(message, ex);
	}
}
