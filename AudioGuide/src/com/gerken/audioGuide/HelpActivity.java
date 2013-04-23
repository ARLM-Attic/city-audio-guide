package com.gerken.audioGuide;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class HelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		findViewById(R.id.buttonHelpClose).setOnClickListener(_closeButtonOnClickListener);
	}
	
	private OnClickListener _closeButtonOnClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			finish();			
		}
	};
}
