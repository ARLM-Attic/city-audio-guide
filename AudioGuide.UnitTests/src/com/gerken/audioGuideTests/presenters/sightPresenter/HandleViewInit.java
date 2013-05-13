package com.gerken.audioGuideTests.presenters.sightPresenter;

import static org.mockito.Mockito.*;

import org.junit.Test;

import com.gerken.audioGuide.interfaces.AssetStreamProvider;
import com.gerken.audioGuide.interfaces.AudioPlayer;
import com.gerken.audioGuide.interfaces.DownscalableBitmapCreator;
import com.gerken.audioGuide.interfaces.Logger;
import com.gerken.audioGuide.interfaces.ApplicationSettingsStorage;
import com.gerken.audioGuide.interfaces.SightPresenterDependencyCreator;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.presenters.SightPresenter;

public class HandleViewInit {
	@Test
	public void Given_ApplicationStartsForThe1stTime__Then_ShowHelp_SetHelpHasBeenShownPreference() {
		final boolean HELP_SHOW = true;
		ApplicationSettingsStorage prefStorage = mock(ApplicationSettingsStorage.class);
		when(prefStorage.showHelpAtStartup()).thenReturn(HELP_SHOW);
		
		SightView view = mock(SightView.class);
		
		SightPresenter sut = CreateSut(view, prefStorage);
		
		// --- Act
		sut.handleViewInit();
				
		// --- Assert
		verify(view).showHelp();
		verify(prefStorage).setShowHelpAtStartup(false);
	}
	
	@Test
	public void Given_ApplicationDoesNotStartForThe1stTime__Then_HelpIsNotShown() {
		final boolean HELP_DONT_SHOW = false;
		ApplicationSettingsStorage prefStorage = mock(ApplicationSettingsStorage.class);
		when(prefStorage.showHelpAtStartup()).thenReturn(HELP_DONT_SHOW);
		
		SightView view = mock(SightView.class);
		
		SightPresenter sut = CreateSut(view, prefStorage);
		
		// --- Act
		sut.handleViewInit();
				
		// --- Assert
		verify(view, never()).showHelp();
	}
	
	private SightPresenter CreateSut(SightView view, ApplicationSettingsStorage settingsStorage) {	
		final String WHATEVER_STRING = "whatever";
		City city = new City(1, "Default", WHATEVER_STRING);
		
		SightPresenterDependencyCreator factory = mock(SightPresenterDependencyCreator.class);
		when(factory.createApplicationSettingsStorage()).thenReturn(settingsStorage);
		
		return new SightPresenter(city, view, mock(AudioPlayer.class), factory);
	}
}
