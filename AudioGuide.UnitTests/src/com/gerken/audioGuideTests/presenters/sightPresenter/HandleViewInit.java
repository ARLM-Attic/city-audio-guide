package com.gerken.audioGuideTests.presenters.sightPresenter;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.interfaces.ApplicationSettingsStorage;
import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.views.AudioPlayerView;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.presenters.SightPresenter;

public class HandleViewInit {

	@Test
	public void Given_ApplicationStartsForThe1stTime__Then_ShowHelp_SetHelpHasBeenShownPreference() {
		final boolean HELP_SHOW = true;
		ApplicationSettingsStorage prefStorage = mock(ApplicationSettingsStorage.class);
		when(prefStorage.showHelpAtStartup()).thenReturn(HELP_SHOW);
		
		SightView sightView = mock(SightView.class);
		ArgumentCaptor<OnEventListener> sightViewInitializedListenerCaptor = 
				ArgumentCaptor.forClass(OnEventListener.class);
		doNothing().when(sightView).addViewInitializedListener(sightViewInitializedListenerCaptor.capture());
		
		OnEventListener presenterHandlerForViewInit = SetupSut(sightView, prefStorage);
		
		// --- Act
		presenterHandlerForViewInit.onEvent();
				
		// --- Assert
		verify(sightView).showHelp();
		verify(prefStorage).setShowHelpAtStartup(false);
	}
	
	@Test
	public void Given_ApplicationDoesNotStartForThe1stTime__Then_HelpIsNotShown() {
		final boolean HELP_DONT_SHOW = false;
		ApplicationSettingsStorage prefStorage = mock(ApplicationSettingsStorage.class);
		when(prefStorage.showHelpAtStartup()).thenReturn(HELP_DONT_SHOW);
		
		SightView sightView = mock(SightView.class);
		ArgumentCaptor<OnEventListener> sightViewInitializedListenerCaptor = 
				ArgumentCaptor.forClass(OnEventListener.class);
		doNothing().when(sightView).addViewInitializedListener(sightViewInitializedListenerCaptor.capture());
		
		OnEventListener presenterHandlerForViewInit = SetupSut(sightView, prefStorage);
		
		// --- Act
		presenterHandlerForViewInit.onEvent();
				
		// --- Assert
		verify(sightView, never()).showHelp();
	}
	
	private OnEventListener SetupSut(SightView sightView, ApplicationSettingsStorage settingsStorage) {	
		ArgumentCaptor<OnEventListener> sightViewInitializedListenerCaptor = 
				ArgumentCaptor.forClass(OnEventListener.class);
		doNothing().when(sightView).addViewInitializedListener(sightViewInitializedListenerCaptor.capture());
		
		SightPresenter sut = 
				new SightPresenter(new City(), sightView, mock(AudioPlayerView.class));
		sut.setApplicationSettingsStorage(settingsStorage);
		
		return sightViewInitializedListenerCaptor.getValue();
	}
}
