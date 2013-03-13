package com.gerken.audioGuide;

import java.util.List;

import com.gerken.audioGuide.interfaces.views.MainPreferenceView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.objectModel.Route;
import com.gerken.audioGuide.presenters.MainPreferencePresenter;
import com.gerken.audioGuide.services.*;
import com.gerken.audioGuide.util.IntentExtraManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.LinearLayout.LayoutParams;


public class MainPreferenceActivity extends Activity implements MainPreferenceView {
	
	private MainPreferencePresenter _presenter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main_preference);
		
		_presenter = new MainPreferencePresenter(
				((GuideApplication)getApplication()).getCity(), 
				this, new SharedPreferenceManager(getApplicationContext()), 
				new DefaultLoggingAdapter("SightPresenter"));
		_presenter.init();
	}


	@Override
	public void setRouteChoices(CharSequence[] entries,
			CharSequence[] entryValues) {
		RadioGroup routeChoices = (RadioGroup)findViewById(R.id.routeChoiceGroup);
		
		int maxIdx = Math.min(entries.length, entryValues.length);
		for(int i=0; i<maxIdx; i++) 
			routeChoices.addView(createRouteChoice(entries[i], entryValues[i]));
	}
	
	private View createRouteChoice(CharSequence entry, CharSequence entryValue) {
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		
		RadioButton choice = new RadioButton(this);
		choice.setText(entry);
		choice.setTag(entryValue);
		layout.addView(choice, 
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
		);
		
		Button showMapButton = new Button(this);
		showMapButton.setText("Map");
		showMapButton.setTag(entryValue);
		showMapButton.setOnClickListener(_mapButtonOnClickListener);
		layout.addView(showMapButton, 
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
		);
		
		return layout;
	}

	private OnClickListener _mapButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String tag = (String)v.getTag();
			int routeId = Integer.valueOf(tag);
			Intent intent = new Intent(MainPreferenceActivity.this, RouteMapActivity.class);
			new IntentExtraManager(intent).setRouteId(routeId);
    		startActivity(intent);			
		}
	};
	
}
