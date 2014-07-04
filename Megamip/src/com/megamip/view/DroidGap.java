package com.megamip.view;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.megamip.voice.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class DroidGap extends Activity  {

	private WebView wv;
	private MegamipGui megamipGui;
	private Handler handler;
	private ScreenOrientation screenOrientation;

	private final ExecutorService threadPool = Executors.newCachedThreadPool();

	public enum ScreenOrientation {
		POSITION_0, POSITION_180
	}

	/* Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		handler = new Handler();
		megamipGui = new MegamipGui(this);
		
		setContentView(megamipGui);
	//	megamipGui.setLayerType(View.LAYER_TYPE_HARDWARE, null);  // turn off the GPU hardware acceleration
		wv = (WebView) findViewById(R.id.web_view);
		wv.loadUrl("file:///android_asset/www/index.html");
	}

	public WebView getWebView() {
		return wv;
	}

	public void setScreenOrientation(ScreenOrientation screenOrientation) {
		this.screenOrientation = screenOrientation;
		if (this.screenOrientation == ScreenOrientation.POSITION_0) {
		
					handler.post(new Runnable() {

						@Override
						public void run() {
						
							megamipGui.setRotation(0);
							

						}
					});

			

		} else {

			
					handler.post(new Runnable() {

						@Override
						public void run() {
						
							megamipGui.setRotation(180);
							

						}
					});

			

		}
	}

	
}