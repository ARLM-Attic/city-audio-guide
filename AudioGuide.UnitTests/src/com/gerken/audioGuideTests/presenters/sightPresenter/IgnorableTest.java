package com.gerken.audioGuideTests.presenters.sightPresenter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import com.gerken.audioGuide.interfaces.ApplicationSettingsStorage;
import com.gerken.audioGuide.interfaces.listeners.OnEventListener;
import com.gerken.audioGuide.interfaces.views.AudioPlayerView;
import com.gerken.audioGuide.interfaces.views.SightView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.presenters.SightPresenter;

//@RunWith(MockitoJUnitRunner.class)
public class IgnorableTest {
	
	//@Mock
	private SightView sightView2;
	
	//@Captor
	private ArgumentCaptor<OnEventListener> listenerCaptor;
	
	@Test
	public void just_test() {
		final boolean HELP_SHOW = true;
		ApplicationSettingsStorage settingsStorage = mock(ApplicationSettingsStorage.class);
		when(settingsStorage.showHelpAtStartup()).thenReturn(HELP_SHOW);
				
		sightView2 = mock(SightView.class);
		listenerCaptor = ArgumentCaptor.forClass(OnEventListener.class);
		
		SightPresenter sut = 
				new SightPresenter(new City(), sightView2, mock(AudioPlayerView.class));
		sut.setApplicationSettingsStorage(settingsStorage);
		
		verify(sightView2).addViewInitializedListener(listenerCaptor.capture());
		
		listenerCaptor.getValue().onEvent();
		
		verify(sightView2).showHelp();
	}
}
