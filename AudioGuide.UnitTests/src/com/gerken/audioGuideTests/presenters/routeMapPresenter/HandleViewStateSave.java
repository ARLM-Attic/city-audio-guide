package com.gerken.audioGuideTests.presenters.routeMapPresenter;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.containers.Point;
import com.gerken.audioGuide.interfaces.LocationTracker;
import com.gerken.audioGuide.interfaces.MediaAssetManager;
import com.gerken.audioGuide.interfaces.OnLocationChangedListener;
import com.gerken.audioGuide.interfaces.OnMultiTouchListener;
import com.gerken.audioGuide.interfaces.ViewStateContainer;
import com.gerken.audioGuide.interfaces.listeners.OnViewStateSaveListener;
import com.gerken.audioGuide.interfaces.views.RouteMapView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.presenters.RouteMapPresenter;

public class HandleViewStateSave {

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
		
		ViewStateContainer stateContainer = mock(ViewStateContainer.class);
		
		SutSetupResult sutSetupResult = setupSut(view);
		
		// --- Act
		sutSetupResult.viewStateSaveListener.onStateSave(stateContainer);
		
		// --- Assert
		verify(stateContainer).putInt(anyString(), eq(EXPECTED_SCREEN_CENTER_ABS_X));
		verify(stateContainer).putInt(anyString(), eq(EXPECTED_SCREEN_CENTER_ABS_Y));
	}
	
	private SutSetupResult setupSut(RouteMapView view) {
		SutSetupResult result = new SutSetupResult();
		
		ArgumentCaptor<OnViewStateSaveListener> viewStateSaveListenerCaptor = 
				ArgumentCaptor.forClass(OnViewStateSaveListener.class);
		doNothing().when(view).addViewInstanceStateSavedListener(viewStateSaveListenerCaptor.capture());
		
		result.sut = new RouteMapPresenter(new City(), view, mock(MediaAssetManager.class));
		result.viewStateSaveListener = viewStateSaveListenerCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		public RouteMapPresenter sut;
		public OnViewStateSaveListener viewStateSaveListener;
	}
}
