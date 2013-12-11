package com.megamip.voice;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.megamip.control.ArduinoCtrl;
import com.megamip.control.ArduinoCtrlMM;
import com.megamip.util.JettyServer;
import com.megamip.voice.MainActivity;

public class MipReceiver {
	
	protected static final int RESULT_SPEECH = 1;
	private static final String JAVASCRIPT = "javascript:";
	private static final String BRC = "()";
	private static final String BRC_OPEN = "('";
	private static final String BRC_CLOSE = "')";
	private static final String Q = "?";
	private static final String HTML_ROOT = "file:///mnt/sdcard/DCIM/gui/";	

	public static final String TAG1 = "A1", TAG2 = "A2", TAG3 = "A3";
	
	// actors 
	
	private Handler handler; 
	private WebView webView;
	private MainActivity activity;
	private ArduinoCtrl arduinoCtrl;
// constructor 
	public MipReceiver() {}
	
	public MipReceiver(Handler handler, WebView webView, MainActivity activity) {
		this.handler = handler;
		this.webView = webView;
		this.activity = activity;
		this.arduinoCtrl = new ArduinoCtrlMM(activity.getApplicationContext());
	}
	


// public methods 
public void guiShowMic(){	
	callJsFunction("hideCenterPanel();showEyes();showMic", "");
}

public void guiHideMic(){
	callJsFunction("hideMic", "");
	
}
 public void pictureSearch(String keywords){
	 
	 String seq = "pictureSearch";
	 callJsFunction(seq, keywords);
	 callJsFunction("log","<b>Picture search for: </b>"+keywords);

	 
 }
 
 public void videoSearch(String keywords){
	 
	 String seq = "clearScreen();hideEyes();showCenterPanel();videoSearch";
	 callJsFunction(seq, keywords);
	 callJsFunction("log","<b>Video search for: </b>"+keywords);
 }
 
 public void webSearch(){}
 
 public void guiNext() {
		callJsFunction("next", "");
	}
 
 public void guiBack() {
		
	 callJsFunction("back", "");
	// webView.performClick();
		
 }
 
 public void guiHome() {
	 String seq = "clearScreen();hideCenterPanel();showEyes";
	 callJsFunction(seq, "");
		
	}
  
 public void guiShow(String mMode) {
	
	 callJsFunction("show", mMode);
	}
 
 public void guiShowMessage(String message){
	 
	 callJsFunction("log",message);
 }
 
 
 public void guiDisplayNotifications(String notifications){
	 callJsFunction("showNotifications1",notifications);
 }
 public void guiDisplayNotifications(String notifications,int period) {
		

	 final String callbackFunction = JAVASCRIPT + "showNotifications2"+BRC_OPEN+notifications+"', "+period+")" ;
		Log.d(TAG3 ,callbackFunction);
		loadURL(callbackFunction); 	 
		
 }

public void guiBlink() {
	callJsFunction("blink","");
	
}
 public void speak(){
	 
		// TODO Auto-generated method stub
	/*	Intent intent = new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

		try {
			
			activity.startActivityForResult(intent, RESULT_SPEECH);
		
		} catch (ActivityNotFoundException a) {
			Toast t = Toast.makeText(activity.getApplicationContext(),
					"Ops! Your device doesn't support Speech to Text",
				Toast.LENGTH_SHORT);
			
		}*/
	 Log.d(TAG3 ,"MipReceiver - speak()");
	 
	 
	/* handler.post(new Runnable() {
         public void run() {
        	 //activity.onSpeak();
        	  
         }
     });*/
	
	 callJsFunction("sequence01", "");
	 
 }
 
 
 
 // Megamip motion methods 
 //  drive(speedLeft, speedRight, distanceLeft, distanceRight);
 public void mipMoveForward(){
		Log.d(TAG2, "MipReceiver moveForward ...");
	 arduinoCtrl.drive(70, 70, 5, 5);
 }
 
 public void mipMoveForward(String params) {
	 String[] input = params.split(JettyServer.SPLIT_CHAR);
	 int speed = Integer.parseInt(input[2]);
	 int time = Integer.parseInt(input[3]);
	 Log.d(TAG2, "MipReceiver moveForward - speed: "+speed+" time: "+time);
	 arduinoCtrl.drive(speed,speed,time, time);
}
 


public void mipMoveBackward(){
	 arduinoCtrl.drive(-70, -70, 5, 5);
	 Log.d(TAG2, "MipReceiver moveBackward ...");
 }
 public void mipMoveBackward(String params) {
	 String[] input = params.split(JettyServer.SPLIT_CHAR);
	 int speed = Integer.parseInt(input[2]);
	 int time = Integer.parseInt(input[3]);
	 Log.d(TAG2, "MipReceiver moveBackward - speed: "+speed+" time: "+time);
	 arduinoCtrl.drive(-speed,-speed,time, time);
	
		
}
 
 public void mipMoveLeft(){
	 arduinoCtrl.drive(-70, 70, 5, 5);
	 Log.d(TAG2, "MipReceiver moveLeft ...");
 }
 
 public void mipMoveLeft(String params) {
	 String[] input = params.split(JettyServer.SPLIT_CHAR);
	 int speed = Integer.parseInt(input[2]);
	 int time = Integer.parseInt(input[3]);
	 Log.d(TAG2, "MipReceiver moveLeft - speed: "+speed+" time: "+time);
	 arduinoCtrl.drive(-speed,speed,time, time);
		
}
 
 public void mipMoveRight(){
	 arduinoCtrl.drive(70, -70, 5, 5);
	 Log.d(TAG2, "MipReceiver moveRight ...");
 }
 
 public void mipMoveRight(String params){
	 String[] input = params.split(JettyServer.SPLIT_CHAR);
	 int speed = Integer.parseInt(input[2]);
	 int time = Integer.parseInt(input[3]);
	 Log.d(TAG2, "MipReceiver moveRight - speed: "+speed+" time: "+time);
	 arduinoCtrl.drive(speed,-speed,time, time);
 }
 // methodes utilitaires
 
 public void callJsFunction(String functionName,String args){
		String json = "";
//		final String callbackFunction = JAVASCRIPT + "f1" + BRC_OPEN + json 
//	  		+ BRC_CLOSE;
		final String callbackFunction = JAVASCRIPT + functionName+BRC_OPEN+args+BRC_CLOSE ;
		Log.d(TAG3 ,callbackFunction);
		loadURL(callbackFunction); 	  	
	}

 
 private void loadURL(final String in){
 	handler.post(new Runnable() {
         public void run() {
         	webView.loadUrl(in);
         }
     });
 }



















}
