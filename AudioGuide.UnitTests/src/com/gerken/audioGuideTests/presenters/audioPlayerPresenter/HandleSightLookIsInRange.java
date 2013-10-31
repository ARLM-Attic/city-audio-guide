package com.gerken.audioGuideTests.presenters.audioPlayerPresenter;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Random;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.gerken.audioGuide.interfaces.AudioPlayer;
import com.gerken.audioGuide.interfaces.NewSightLookGotInRangeRaiser;
import com.gerken.audioGuide.interfaces.OnSightLookGotInRangeListener;
import com.gerken.audioGuide.interfaces.views.AudioPlayerView;
import com.gerken.audioGuide.objectModel.*;
import com.gerken.audioGuide.presenters.AudioPlayerPresenter;

public class HandleSightLookIsInRange {
	private Random _random = new Random(System.currentTimeMillis());
	
	@Test
	public void Given_NewSightGotInRange__Then_PlayerViewShownAsStopped() {
		City city = createSingleSightLookModel();
		
		AudioPlayerView playerView = mock(AudioPlayerView.class);
		AudioPlayer player = mock(AudioPlayer.class);
		NewSightLookGotInRangeRaiser raiser = mock(NewSightLookGotInRangeRaiser.class);
		
		SutSetupResult sutSetupResult = setupSut(playerView, player, raiser);
		
		// --- Act
		sutSetupResult.sightLookGotInRangeListener.onSightLookGotInRange(
				city.getSights().get(0).getSightLooks().get(0));
		
		// --- Assert
		verify(playerView).displayPlayerStopped();
	}
	
	private City createSingleSightLookModel() {
		final String WHATEVER_STRING = "whatever";
		return createSingleSightLookModel(_random.nextDouble(), _random.nextDouble(), WHATEVER_STRING);
	}
	
	private City createSingleSightLookModel(double latitude, double longitude, String sightName) {
		final String WHATEVER_STRING = "whatever";	
		
		SightLook expectedSightLook = new SightLook(
				latitude, longitude, WHATEVER_STRING);
		Sight expectedSight = new Sight(_random.nextInt(), sightName, WHATEVER_STRING);
		expectedSight.addLook(expectedSightLook);
		City city = new City(_random.nextInt(), WHATEVER_STRING, WHATEVER_STRING);
		city.getSights().add(expectedSight);
		
		return city;
	}
	
	
	private SutSetupResult setupSut(AudioPlayerView playerView, 
			AudioPlayer audioPlayer, 
			NewSightLookGotInRangeRaiser newSightLookGotInRangeRaiser) {
		SutSetupResult result = new SutSetupResult();

		ArgumentCaptor<OnSightLookGotInRangeListener> sightLookGotInRangeCaptor = 
				ArgumentCaptor.forClass(OnSightLookGotInRangeListener.class);
		doNothing().when(newSightLookGotInRangeRaiser).addSightLookGotInRangeListener(
				sightLookGotInRangeCaptor.capture());		
		
		AudioPlayerPresenter sut = new AudioPlayerPresenter(playerView, audioPlayer);
		sut.setNewSightLookGotInRangeRaiser(newSightLookGotInRangeRaiser);
		
		result.sut = sut;
		result.sightLookGotInRangeListener = sightLookGotInRangeCaptor.getValue();
		
		return result;
	}
	
	private class SutSetupResult {
		public AudioPlayerPresenter sut;
		public OnSightLookGotInRangeListener sightLookGotInRangeListener;
	}
}
