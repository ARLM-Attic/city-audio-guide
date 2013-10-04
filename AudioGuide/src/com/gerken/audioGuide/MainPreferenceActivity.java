package com.gerken.audioGuide;

import java.util.ArrayList;
import java.util.List;

import com.gerken.audioGuide.controls.FlexiRadioGroup;
import com.gerken.audioGuide.interfaces.BitmapContainer;
import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.views.MainPreferenceView;
import com.gerken.audioGuide.objectModel.City;
import com.gerken.audioGuide.objectModel.Route;
import com.gerken.audioGuide.presenters.MainPreferencePresenter;
import com.gerken.audioGuide.services.*;
import com.gerken.audioGuide.util.IntentExtraManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.LinearLayout.LayoutParams;


public class MainPreferenceActivity extends Activity implements MainPreferenceView {
	private View _rootView;
	
	private FlexiRadioGroup _routeChoiceGroup;
	
	private ArrayList<OnEventListener> _viewInitializedListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _viewLayoutCompleteListeners = new ArrayList<OnEventListener>();
	private ArrayList<OnEventListener> _okButtonPressedListeners = new ArrayList<OnEventListener>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main_preference);
		
		_rootView = findViewById(R.id.rootLayout);
		_routeChoiceGroup = (FlexiRadioGroup)findViewById(R.id.routeChoiceGroup);
		
		((GuideApplication)getApplication()).getPresenterContainer().initMainPreferencePresenter(this);
		
		findViewById(R.id.buttonOk).setOnClickListener(_okButtonOnClickListener);
		findViewById(R.id.buttonCancel).setOnClickListener(_cancelButtonOnClickListener);	
		
		_rootView.getViewTreeObserver().addOnGlobalLayoutListener(
			    new ViewTreeObserver.OnGlobalLayoutListener() {
			    	public void onGlobalLayout() {
			    		for(OnEventListener l : _viewLayoutCompleteListeners)
			            	l.onEvent();
			    	}
			    }
	    );
		
		for(OnEventListener l : _viewInitializedListeners)
        	l.onEvent();
	}

	@Override
	public void setRouteChoices(CharSequence[] entries,
			CharSequence[] entryValues) {
		
		int maxIdx = Math.min(entries.length, entryValues.length);
		for(int i=0; i<maxIdx; i++) 
			_routeChoiceGroup.addView(createRouteChoice(entries[i], entryValues[i]));
	}	

	@Override
	public void setSelectedRoute(CharSequence value) {
		_routeChoiceGroup.setSelectedValue(value);		
	}
	
	@Override
	public void setBackgroundImage(BitmapContainer bitmapContainer) {
		_rootView.setBackgroundDrawable(new BitmapDrawable(bitmapContainer.getBitmap()));
	}	

	@Override
	public String getSelectedRoute() {
		Object selected = _routeChoiceGroup.getSelected().getTag();
		return (selected != null) ? selected.toString() : null;
	}

	@Override
	public Integer getWidth() {
		return _rootView.getWidth();
	}

	@Override
	public Integer getHeight() {
		return _rootView.getHeight();
	}
	
	@Override
	public void addViewInitializedListener(OnEventListener listener) {
		_viewInitializedListeners.add(listener);		
	}

	@Override
	public void addViewLayoutCompleteListener(OnEventListener listener) {
		_viewLayoutCompleteListeners.add(listener);		
	}

	@Override
	public void addOkButtonPressedListener(OnEventListener listener) {
		_okButtonPressedListeners.add(listener);		
	}

	private View createRouteChoice(CharSequence entry, CharSequence entryValue) {
		RelativeLayout layout = new RelativeLayout(this);
		//layout.setOrientation(LinearLayout.HORIZONTAL);
		
		RadioButton choice = new RadioButton(this);
		choice.setText(entry);
		choice.setTag(entryValue);
		choice.setTextColor(0xFFFEE73F);
		RelativeLayout.LayoutParams choiceLp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		choiceLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		layout.addView(choice, choiceLp);
		
		Button showMapButton = new Button(this);
		showMapButton.setText("Map");
		showMapButton.setTag(entryValue);
		showMapButton.setTextColor(0xFF00FF33);
		showMapButton.setTypeface(Typeface.DEFAULT_BOLD);
		showMapButton.setBackgroundColor(0x00000000);
		showMapButton.setOnClickListener(_mapButtonOnClickListener);
		RelativeLayout.LayoutParams showMapButtonLp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		showMapButtonLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		layout.addView(showMapButton, showMapButtonLp);
		
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
	
	private OnClickListener _okButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			for(OnEventListener l : _okButtonPressedListeners)
	        	l.onEvent();
		}
	};
	
	private OnClickListener _cancelButtonOnClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			finish();			
		}
	};

}
