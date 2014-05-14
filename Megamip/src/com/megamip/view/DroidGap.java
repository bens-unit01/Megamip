package com.megamip.view;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.cordova.Config;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import com.megamip.voice.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebSettings;

public class DroidGap extends Activity implements CordovaInterface {

	private CordovaWebView cwv;
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
		cwv = (CordovaWebView) findViewById(R.id.tutorialView);
		Config.init(this);
		cwv.loadUrl(Config.getStartUrl());
	}

	public CordovaWebView getWebView() {
		return cwv;
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

	@Override
	public Activity getActivity() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public ExecutorService getThreadPool() {
		return threadPool;

	}

	@Override
	public Object onMessage(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActivityResultCallback(CordovaPlugin arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startActivityForResult(CordovaPlugin arg0, Intent arg1, int arg2) {
		// TODO Auto-generated method stub

	}
}