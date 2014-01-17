package com.gerken.audioGuideTests.presenters.mainPreferencePresenter;

import static org.mockito.Mockito.*;

import java.util.Random;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.interfaces.ApplicationSettingsStorage;
import com.gerken.audioGuide.interfaces.MediaAssetManager;
import com.gerken.audioGuide.interfaces.listeners.OnEventListener;
import com.gerken.audioGuide.interfaces.listeners.OnLocationChangedListener;
import com.gerken.audioGuide.interfaces.listeners.OnMultiTouchListener;
import com.gerken.audioGuide.interfaces.views.MainPreferenceView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.presenters.MainPreferencePresenter;
import com.gerken.audioGuide.presenters.RouteMapPresenter;

public class HandleOk {
	private Random _random = new Random(System.currentTimeMillis());
	
	@Test
	public void Given_RouteIsSelected__Then_SelectedRouteStoredInPreferences() {
		final int EXPECTED_ROUTE_ID = _random.nextInt();
		
		MainPreferenceView view = mock(MainPreferenceView.class);
		when(view.getSelectedRoute()).thenReturn(String.valueOf(EXPECTED_ROUTE_ID));
		ApplicationSettingsStorage settignsStorage = mock(ApplicationSettingsStorage.class);
		
		SutSetupResult sutSetupResult = setupSut(view, settignsStorage);
		
		// --- Act
		sutSetupResult.okButtonPressedListener.onEvent();
		
		// --- Assert
		verify(settignsStorage).setCurrentRouteId(EXPECTED_ROUTE_ID);
	}
	
	@Test
	public void Given_NoRouteIsSelected__Then_RouteSelectionResetInPreferences() {
		MainPreferenceView view = mock(MainPreferenceView.class);
		when(view.getSelectedRoute()).thenReturn(null);
		ApplicationSettingsStorage settignsStorage = mock(ApplicationSettingsStorage.class);
		
		SutSetupResult sutSetupResult = setupSut(view, settignsStorage);
		
		// --- Act
		sutSetupResult.okButtonPressedListener.onEvent();
		
		// --- Assert
		verify(settignsStorage).resetCurrentRoute();
	}
	
	private SutSetupResult setupSut(MainPreferenceView view, ApplicationSettingsStorage settignsStorage) {
		SutSetupResult result = new SutSetupResult();

		ArgumentCaptor<OnEventListener> okButtonPressedListenerCaptor = 
				ArgumentCaptor.forClass(OnEventListener.class);
		doNothing().when(view).addOkButtonPressedListener(okButtonPressedListenerCaptor.capture());
		
		result.sut = new MainPreferencePresenter(new City(), view, settignsStorage);
		result.okButtonPressedListener = okButtonPressedListenerCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		public MainPreferencePresenter sut;
		public OnEventListener okButtonPressedListener;
	}
}
