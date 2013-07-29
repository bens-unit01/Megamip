package com.megamip.voice;

import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;

public class MipReceiver {
	
	private static final String JAVASCRIPT = "javascript:";
	private static final String BRC = "()";
	private static final String BRC_OPEN = "('";
	private static final String BRC_CLOSE = "')";
	private static final String Q = "?";
	private static final String HTML_ROOT = "file:///mnt/sdcard/DCIM/gui/";	
	private static final String TAG = "A3";
	
	// actors 
	
	private Handler handler; 
	private WebView webView;
// constructor 
	public MipReceiver() {}
	
	public MipReceiver(Handler handler, WebView webView) {
		this.handler = handler;
		this.webView = webView;
	}


// public methods 
	
 public void pictureSearch(String keywords){
	 
	 String seq = "clearScreen();hideEyes();showCenterPanel();pictureSearch";
	 callJsFunction(seq, keywords);

	 
 }
 
 public void videoSearch(){}
 
 public void webSearch(){}
 
 
 // methodes utilitaires
 
 public void callJsFunction(String functionName,String args){
		String json = "";
//		final String callbackFunction = JAVASCRIPT + "f1" + BRC_OPEN + json 
//	  		+ BRC_CLOSE;
		final String callbackFunction = JAVASCRIPT + functionName+BRC_OPEN+args+BRC_CLOSE ;
		Log.d(TAG ,callbackFunction);
		loadURL(callbackFunction); 	  	
	}

 
 private void loadURL(final String in){
 	handler.post(new Runnable() {
         public void run() {
         	webView.loadUrl(in);
         }
     });
 }
 

public void next() {
	callJsFunction("next", "");
}


}
