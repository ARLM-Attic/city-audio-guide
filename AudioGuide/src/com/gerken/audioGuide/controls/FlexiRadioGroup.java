package com.gerken.audioGuide.controls;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.*;
import android.widget.*;

public class FlexiRadioGroup extends RadioGroup {
	
	private List<RadioButton> _buttons = new ArrayList<RadioButton>();

	public FlexiRadioGroup(Context context) {
		super(context);
	}
	
	public FlexiRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public RadioButton getSelected() {
		for(RadioButton rb : _buttons) {
			if(rb.isChecked())
				return rb;
		}
		return null;
	}

	@Override
	public void addView(View child) {
		if(child instanceof RadioButton) {
			RadioButton rb = (RadioButton)child;
			rb.setOnCheckedChangeListener(_radioButtonCheckedChangeListener);
			_buttons.add(rb);
		}
		else if(child instanceof ViewGroup) {
			List<RadioButton> rbs = getRadioButtonChildren((ViewGroup)child);
			for(RadioButton rb : rbs)
				rb.setOnCheckedChangeListener(_radioButtonCheckedChangeListener);
			_buttons.addAll(rbs);
		}		
		
		super.addView(child);
	}
	
	private List<RadioButton> getRadioButtonChildren(ViewGroup parent) {
		ArrayList<RadioButton> result = new ArrayList<RadioButton>();
		for(int i=0; i<parent.getChildCount(); i++) {
			View child = parent.getChildAt(i);
			if(child instanceof RadioButton)
				result.add((RadioButton)child);
			else if(child instanceof ViewGroup)
				result.addAll(getRadioButtonChildren((ViewGroup)child));
		}
		
		return result;
	}
	
	private CompoundButton.OnCheckedChangeListener _radioButtonCheckedChangeListener = 
			new CompoundButton.OnCheckedChangeListener() {
				
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(isChecked) {
				RadioButton source = (RadioButton)buttonView;
				for(RadioButton rb : _buttons) {
					if(!rb.equals(source))
						rb.setChecked(false);
				}
			}
			
		}
	};
}
