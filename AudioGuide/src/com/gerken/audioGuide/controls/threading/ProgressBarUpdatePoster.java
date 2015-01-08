package com.gerken.audioGuide.controls.threading;

import com.gerken.audioGuide.controls.ControlUpdater;

import android.os.Handler;
import android.widget.ProgressBar;

public class ProgressBarUpdatePoster {
	private ProgressBar _progressBar;
	private Handler _handler;
	
	private ControlUpdater<Integer> _progressUpdater;
	
	public ProgressBarUpdatePoster(Handler handler, ProgressBar progressBar) {
		_progressBar = progressBar;		
		_handler = handler;
		
		_progressUpdater = new ControlUpdater<Integer>(
			new ControlUpdater.Updater<Integer>(){
				public void Update(Integer progress) {
					_progressBar.setProgress(progress);
				}
			}, 
			0
		);
	}

	public void updateProgress(int progress) {
		_progressUpdater.setStatus(progress);
		_handler.post(_progressUpdater);
	}
}
