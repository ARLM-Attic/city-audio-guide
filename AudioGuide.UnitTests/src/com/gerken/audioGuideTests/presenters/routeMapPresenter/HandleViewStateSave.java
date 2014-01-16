package com.gerken.audioGuideTests.presenters.routeMapPresenter;

import static org.mockito.Mockito.*;
import junit.framework.Assert;

import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.containers.Point;
import com.gerken.audioGuide.interfaces.LocationTracker;
import com.gerken.audioGuide.interfaces.MediaAssetManager;
import com.gerken.audioGuide.interfaces.listeners.OnMultiTouchListener;
import com.gerken.audioGuide.interfaces.ViewStateContainer;
import com.gerken.audioGuide.interfaces.listeners.OnViewStateSaveListener;
import com.gerken.audioGuide.interfaces.views.RouteMapView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.presenters.RouteMapPresenter;
import com.gerken.audioGuideTests.SimpleViewStateContainer;

public class HandleViewStateSave {
	private static final float DEFAULT_DELTA = 0.001f; 

	@Test
	public void Given_ViewScrolledBeforeSavingState__Then_ScreenCenterAbsPositionPutToContainer() {
		final int EXPECTED_SCREEN_CENTER_ABS_X = 30;
		final int EXPECTED_SCREEN_CENTER_ABS_Y = 50;
		
		RouteMapView view = mock(RouteMapView.class);
		when(view.getWidth()).thenReturn(40);
		when(view.getHeight()).thenReturn(60);		
		when(view.getOriginalMapWidth()).thenReturn(160);
		when(view.getOriginalMapHeight()).thenReturn(160);
		when(view.getScrollX()).thenReturn(10);
		when(view.getScrollY()).thenReturn(20);
		
		SimpleViewStateContainer stateContainer = new SimpleViewStateContainer();
		RouteMapPresenter.RouteMapViewStateContainer containerWrapper = 
				new RouteMapPresenter.RouteMapViewStateContainer(stateContainer);
		
		SutSetupResult sutSetupResult = setupSut(view);
		
		// --- Act
		sutSetupResult.viewStateSaveListener.onStateSave(stateContainer);
		
		// --- Assert
		Assert.assertEquals("Screen center absolute X", 
			EXPECTED_SCREEN_CENTER_ABS_X, containerWrapper.getScreenCenterAbsX());
		Assert.assertEquals("Screen center absolute Y", 
			EXPECTED_SCREEN_CENTER_ABS_Y, containerWrapper.getScreenCenterAbsY());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void Given_ViewScaledBeforeSavingState__Then_ScalePutToContainer() {
		final float EXPECTED_SCALE = 0.5f;
		
		RouteMapView view = mock(RouteMapView.class);
		when(view.getWidth()).thenReturn(40);
		when(view.getHeight()).thenReturn(60);		
		when(view.getOriginalMapWidth()).thenReturn(160);
		when(view.getOriginalMapHeight()).thenReturn(160);
		
		SimpleViewStateContainer stateContainer = new SimpleViewStateContainer();
		RouteMapPresenter.RouteMapViewStateContainer containerWrapper = 
				new RouteMapPresenter.RouteMapViewStateContainer(stateContainer);
		
		SutSetupResult sutSetupResult = setupSut(view);
		sutSetupResult.multiTouchListener.onMultiTouchDown(
			(Point<Float>[])new Point[]{ new Point<Float>(20f, 0f), new Point<Float>(40f, 0f) }
		);		
		sutSetupResult.multiTouchListener.onMultiTouchMove(
			(Point<Float>[])new Point[]{ new Point<Float>(25f, 0f), new Point<Float>(35f, 0f) }
		);
		
		// --- Act
		sutSetupResult.viewStateSaveListener.onStateSave(stateContainer);
		
		// --- Assert
		Assert.assertEquals("Scale value", 
			EXPECTED_SCALE, containerWrapper.getScale(), DEFAULT_DELTA);
	}
	
	private SutSetupResult setupSut(RouteMapView view) {
		SutSetupResult result = new SutSetupResult();
		
		ArgumentCaptor<OnViewStateSaveListener> viewStateSaveListenerCaptor = 
				ArgumentCaptor.forClass(OnViewStateSaveListener.class);
		doNothing().when(view).addViewInstanceStateSavedListener(viewStateSaveListenerCaptor.capture());
		
		ArgumentCaptor<OnMultiTouchListener> multiTouchListenerCaptor = 
				ArgumentCaptor.forClass(OnMultiTouchListener.class);
		doNothing().when(view).addViewMultiTouchListener(multiTouchListenerCaptor.capture());
		
		result.sut = new RouteMapPresenter(new City(), view, mock(MediaAssetManager.class));
		result.viewStateSaveListener = viewStateSaveListenerCaptor.getValue();
		result.multiTouchListener = multiTouchListenerCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		public RouteMapPresenter sut;
		public OnViewStateSaveListener viewStateSaveListener;
		public OnMultiTouchListener multiTouchListener;
	}
}
