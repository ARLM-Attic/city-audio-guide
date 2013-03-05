package com.gerken.audioGuide;

import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

public class RouteMapActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_map);
		
		ImageView mapImage = (ImageView)findViewById(R.id.mapImage);
		AssetManager assetManager = getApplicationContext().getAssets();
		InputStream str;
		try {
			str = assetManager.open("images/rt_1.png");
			mapImage.setImageDrawable(Drawable.createFromStream(str, ""));
			str.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("RouteMapActivity", "can't read map", e);
		}	
		//mapImage.setScaleType(scaleType)
		Log.d("RouteMapActivity", String.format("img: %d %d", mapImage.getWidth(), mapImage.getHeight()));
		Log.d("RouteMapActivity", String.format("drw: %d %d", 
				mapImage.getDrawable().getMinimumWidth(), mapImage.getDrawable().getMinimumHeight()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_map, menu);
		return true;
	}

}
