package com.gerken.audioGuide;

import com.gerken.audioGuide.interfaces.BitmapContainer;
import com.gerken.audioGuide.interfaces.views.AuxiliaryView;
import com.gerken.audioGuide.util.SightIntentExtraWrapper;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnClickListener;

public class HelpActivity extends BasicGuideActivity implements AuxiliaryView {
	
	private View _rootView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		_rootView = findViewById(R.id.rootLayout);
		findViewById(R.id.buttonHelpClose).setOnClickListener(_closeButtonOnClickListener);
		findViewById(R.id.buttonHelpDemo).setOnClickListener(_demoButtonOnClickListener);
		
		((GuideApplication)getApplication()).getPresenterContainer().initHelpPresenter(this);
		onInitialized();
	}
	
	@Override
	public void setBackgroundImage(BitmapContainer bitmapContainer) {
		_rootView.setBackgroundDrawable(new BitmapDrawable(bitmapContainer.getBitmap()));
	}
	
	@Override
	protected View getRootView() {
		return _rootView;
	}
	
	private OnClickListener _closeButtonOnClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			finish();			
		}
	};	
	
	private OnClickListener _demoButtonOnClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			Intent sightIntent = new Intent(HelpActivity.this, SightActivity.class);
			new SightIntentExtraWrapper(sightIntent).setIsDemoMode(true);
    		startActivity(sightIntent);			
		}
	};
}
