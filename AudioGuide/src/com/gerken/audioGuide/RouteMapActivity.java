package com.gerken.audioGuide;

import java.io.InputStream;

import com.gerken.audioGuide.interfaces.views.RouteMapView;
import com.gerken.audioGuide.util.IntentExtraManager;

import android.os.Bundle;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsoluteLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class RouteMapActivity extends BasicGuideActivity implements RouteMapView {
	private View _rootView;	
	private ImageView _mapImage;
	private IntentExtraManager _intentExtraManager;	
	private Animation _mapPointerAnimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_map);
		
		_rootView = findViewById(R.id.rootLayout);
		_mapImage = (ImageView)findViewById(R.id.mapImage);
		_intentExtraManager = new IntentExtraManager(getIntent());
		_mapPointerAnimation = createMapPointerAnimation();
		
		((GuideApplication)getApplication()).getPresenterContainer().initRouteMapPresenter(this);
		
		onInitialized();
	}

	@Override
	public void displayMap(InputStream mapStream) throws Exception {	

		_mapImage.setImageDrawable(Drawable.createFromStream(mapStream, ""));
		mapStream.close();
		findViewById(R.id.routeMapErrorMessage).setVisibility(View.INVISIBLE);
		
		View mapContainer = findViewById(R.id.mapContainer);
		mapContainer.setMinimumWidth(_mapImage.getWidth());
		mapContainer.setMinimumHeight(_mapImage.getHeight());
		
		View mapPointerContainer = findViewById(R.id.mapPointerContainer);
		mapPointerContainer.setMinimumWidth(_mapImage.getWidth());
		mapPointerContainer.setMinimumHeight(_mapImage.getHeight());
	}	

	@Override
	public void displayError(int messageResourceId) {
		findViewById(R.id.routeMapMainView).setVisibility(View.INVISIBLE);
		
		((TextView)findViewById(R.id.routeMapErrorMessage)).setText(
				getString(messageResourceId));		
	}	
	
	@Override
	public int getMapWidth() {
		return _mapImage.getWidth();
	}

	@Override
	public int getMapHeight() {
		return _mapImage.getHeight();
	}

	@Override
	public void showLocationPointerAt(int x, int y) {
		View mapPointer = findViewById(R.id.mapPointerImage);
		AbsoluteLayout.LayoutParams pointerLp = (AbsoluteLayout.LayoutParams)mapPointer.getLayoutParams();
		pointerLp.x = x - (int)(mapPointer.getWidth()/2);
		pointerLp.y = y - (int)(mapPointer.getHeight()/2);
		//pointerLp.setMargins(100, 200, pointerLp.rightMargin, pointerLp.bottomMargin);
		mapPointer.setLayoutParams(pointerLp);
		mapPointer.setVisibility(View.VISIBLE);
        mapPointer.startAnimation(_mapPointerAnimation);
	}

	@Override
	public void hideLocationPointer() {
		findViewById(R.id.mapPointerImage).setVisibility(View.INVISIBLE);		
	}

	@Override
	public int getRouteId() {
		return _intentExtraManager.getRouteId();
	}

	@Override
	public void scrollTo(int x, int y) {
		HorizontalScrollView hscroller = (HorizontalScrollView)findViewById(R.id.routeMapHorizontalScroller);
		hscroller.scrollTo(x, y);
		
		ScrollView vscroller = (ScrollView)findViewById(R.id.routeMapMainView);
		vscroller.scrollTo(x, y);
	}

	@Override
	protected View getRootView() {
		return _rootView;
	}

	private Animation createMapPointerAnimation() {
		Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        //anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        return anim;
	}
}
