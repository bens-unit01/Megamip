package com.megamip.view;

import com.megamip.voice.R;
import com.megamip.voice.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebSettings.PluginState;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class NotificationsActivity extends CarouselActivity {

	private WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		final int wvWidth = displayMetrics.widthPixels;
		final int wvHeight = displayMetrics.heightPixels;
		
		webView = new WebView(this);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(new WebChromeClient(){});
		webView.getSettings().setPluginState(PluginState.ON);
		
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				wvWidth, wvHeight);
		lp.setMargins(0, 0, 0, 0);
		webView.setLayoutParams(lp);
		
		mCarouselContainer.addView(webView);
		webView.loadUrl("file:///android_asset/www/notifications.html");
	
	
	}
	
}
