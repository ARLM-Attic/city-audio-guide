package com.gerken.audioGuide;

import com.gerken.audioGuide.objectModel.City;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class MainPreferenceActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences_main);
		
		setRouteOptions();
	}
	
	private void setRouteOptions() {
		ListPreference lp = (ListPreference)findPreference("routeList");
		
		City city = ((GuideApplication)getApplication()).getCity();
		CharSequence[] entries = { "One", "Two", "Three" };
		CharSequence[] entryValues = { "", "2", "3" };
		lp.setEntries(entries);
		lp.setEntryValues(entryValues);
	}
	
}
