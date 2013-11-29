package com.gerken.audioGuide.controls;

import android.widget.TextView;

public class TextViewUpdater implements ControlUpdater.Updater<String> {
	private TextView _textView;
	
	public TextViewUpdater(TextView textView) {
		_textView = textView;
	}

	@Override
	public void Update(String param) {
		_textView.setText(param);		
	}

}
