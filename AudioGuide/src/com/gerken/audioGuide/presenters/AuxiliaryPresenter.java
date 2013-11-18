package com.gerken.audioGuide.presenters;

import com.gerken.audioGuide.graphics.BitmapDownscalingResult;
import com.gerken.audioGuide.interfaces.DownscalingBitmapLoader;
import com.gerken.audioGuide.interfaces.Logger;
import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.views.AuxiliaryView;
import com.gerken.audioGuide.objectModel.City;

public class AuxiliaryPresenter {
	private static final String DEFAULT_SUFFIX_PORT = "_port";
	private static final String DEFAULT_SUFFIX_LAND = "_land";
	
	private AuxiliaryView _auxView;
	private City _city;
	
	private DownscalingBitmapLoader _bitmapLoader;
	private Logger _logger;
	
	private String _suffixPortrait = DEFAULT_SUFFIX_PORT;
	private String _suffixLandscape = DEFAULT_SUFFIX_LAND;

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
			String img = getBackgroundImageFileName();			
			logDebug("Loading " + img);
			backgroundImage = _bitmapLoader.load(
				img, _auxView.getWidth(), _auxView.getHeight());
		}
		catch(Exception ex) {
			logError("Unable to set the background image", ex);
		}
		if(backgroundImage != null)		
			_auxView.setBackgroundImage(backgroundImage);
	}
	
	private String getBackgroundImageFileName() {
		String suffix = (_auxView.getWidth() > _auxView.getHeight()) ? 
				_suffixLandscape : _suffixPortrait;		
		
		String[] fileNameParts = _city.getHelpBackgroundImageName().split("\\.");
		if(fileNameParts.length == 2) {
			return fileNameParts[0] + suffix + "." + fileNameParts[1];
		}
		else
			return _city.getHelpBackgroundImageName() + suffix;
	}
	
	private void logError(String message, Throwable ex) {
		if(_logger != null)
			_logger.logError(message, ex);
	}
	private void logDebug(String message) {
		if(_logger != null)
			_logger.logDebug(message);
	}
}
