package com.gerken.audioGuideTests.presenters.mainPreferencePresenter;

import static org.mockito.Mockito.*;

import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.interfaces.ApplicationSettingsStorage;
import com.gerken.audioGuide.interfaces.listeners.OnEventListener;
import com.gerken.audioGuide.interfaces.views.MainPreferenceView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.objectModel.Route;
import com.gerken.audioGuide.presenters.MainPreferencePresenter;

public class HandleViewInitialized {
	private Random _random = new Random(System.currentTimeMillis());
	
	@Test
	public void Given_SelectedRouteIsStoredInPreferences__Then_RouteIsSelectedInView() {
		final int EXPECTED_ROUTE_ID = _random.nextInt();
		final boolean A_ROUTE_IS_CHOSEN = true;
		
		MainPreferenceView view = mock(MainPreferenceView.class);		
		ApplicationSettingsStorage settignsStorage = mock(ApplicationSettingsStorage.class);
		when(settignsStorage.getCurrentRouteId()).thenReturn(EXPECTED_ROUTE_ID);
		when(settignsStorage.isRouteChosen()).thenReturn(A_ROUTE_IS_CHOSEN);
		
		SutSetupResult sutSetupResult = setupSut(view, settignsStorage);
		
		// --- Act
		sutSetupResult.viewInitializedListener.onEvent();
		
		// --- Assert
		verify(view).setSelectedRoute(String.valueOf(EXPECTED_ROUTE_ID));
	}
	
	@Test
	public void Given_CityHasRoutes__Then_RouteListPopulatedInView() {
		final int ROUTE_ID_1 = _random.nextInt();
		final String ROUTE_NAME_1 = createRandomString();
		final int ROUTE_ID_2 = _random.nextInt();
		final String ROUTE_NAME_2 = createRandomString();
		
		final CharSequence[] EXPECTED_ENTRIES = new CharSequence[] { ROUTE_NAME_1, ROUTE_NAME_2 };
		final CharSequence[] EXPECTED_ENTRY_VALUES = 
			new CharSequence[] { String.valueOf(ROUTE_ID_1), String.valueOf(ROUTE_ID_2) };
		
		City city = new City();
		city.getRoutes().add(new Route(ROUTE_ID_1, ROUTE_NAME_1));
		city.getRoutes().add(new Route(ROUTE_ID_2, ROUTE_NAME_2));
		
		MainPreferenceView view = mock(MainPreferenceView.class);		
		ApplicationSettingsStorage settignsStorage = mock(ApplicationSettingsStorage.class);
		
		SutSetupResult sutSetupResult = setupSut(city, view, settignsStorage);
		
		// --- Act
		sutSetupResult.viewInitializedListener.onEvent();
		
		// --- Assert
		verify(view).setRouteChoices(
			AdditionalMatchers.aryEq(EXPECTED_ENTRIES), AdditionalMatchers.aryEq(EXPECTED_ENTRY_VALUES));
	}
	
	private String createRandomString() {
		return String.valueOf(_random.nextLong());
	}
	
	private SutSetupResult setupSut(MainPreferenceView view, ApplicationSettingsStorage settignsStorage) {
		return setupSut(new City(), view, settignsStorage);
	}
	
	private SutSetupResult setupSut(City city, MainPreferenceView view, ApplicationSettingsStorage settignsStorage) {
		SutSetupResult result = new SutSetupResult();

		ArgumentCaptor<OnEventListener> viewInitializedListenerCaptor = 
				ArgumentCaptor.forClass(OnEventListener.class);
		doNothing().when(view).addViewInitializedListener(viewInitializedListenerCaptor.capture());
		
		result.sut = new MainPreferencePresenter(city, view, settignsStorage);
		result.viewInitializedListener = viewInitializedListenerCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		public MainPreferencePresenter sut;
		public OnEventListener viewInitializedListener;
	}
}
