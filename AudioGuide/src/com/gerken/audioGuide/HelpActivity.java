package com.gerken.audioGuide;

import java.util.ArrayList;

import com.gerken.audioGuide.interfaces.BitmapContainer;
import com.gerken.audioGuide.interfaces.OnEventListener;
import com.gerken.audioGuide.interfaces.views.AuxiliaryView;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;

public class HelpActivity extends Activity implements AuxiliaryView {
	
	private View _rootView;
	
	private ArrayList<OnEventListener> _viewLayoutCompleteListeners = new ArrayList<OnEventListener>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		_rootView = findViewById(R.id.rootLayout);
		findViewById(R.id.buttonHelpClose).setOnClickListener(_closeButtonOnClickListener);
		
		((GuideApplication)getApplication()).getPresenterContainer().initHelpPresenter(this);	
		
		_rootView.getViewTreeObserver().addOnGlobalLayoutListener(
			    new ViewTreeObserver.OnGlobalLayoutListener() {
			    	public void onGlobalLayout() {
			    		for(OnEventListener l : _viewLayoutCompleteListeners)
			            	l.onEvent();
			    	}
			    }
			    );
	}
	
	@Override
	public void setBackgroundImage(BitmapContainer bitmapContainer) {
		_rootView.setBackgroundDrawable(new BitmapDrawable(bitmapContainer.getBitmap()));
	}
	
	private OnClickListener _closeButtonOnClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			finish();			
		}
	};

	@Override
	public Integer getWidth() {
		return _rootView.getWidth();
	}

	@Override
	public Integer getHeight() {
		return _rootView.getHeight();
	}

	@Override
	public void addViewLayoutCompleteListener(OnEventListener listener) {
		_viewLayoutCompleteListeners.add(listener);		
	}
}
