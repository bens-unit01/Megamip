package com.megamip.voice;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.cordova.DroidGap;


import com.megamip.voice.MipUsbDevice.UsbEvent;
import com.megamip.voice.MipUsbDevice.UsbListener;

public class MainActivity extends DroidGap {
	
	
	public static final int USER_MOBILE  = 0;
	public static final int USER_DESKTOP = 1;
	
	public static  Context context;  // reference vers l'activité MainActivity 
	protected static final int RESULT_SPEECH = 1;
	public static final String TAG1 = "A1", TAG2 = "A2", TAG3 = "A3";
	private ImageButton btnSpeak;
	private TextView textView;
	private EditText editText1;
	private WebView webView;
	private String accountType = null;
	private String accountName = null;
	private Handler handler = null;
	private MipUsbDevice mipUsbDevice;
	//private static final String HTML_ROOT = "file:///mnt/sdcard/DCIM/gui/";	
	private static final String HTML_ROOT = "file:///android_asset/www/";	
	private Command mCommand;
	private Invoker invoker;
	private MipReceiver receiver;
	private MipCommand mc;
	private SpeakNow mSpeakNowDlg;
	
	private SpeechRecognizer mSpeechRecognizer;
	private int compteur1 = 0;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		context = this;
		handler = new Handler();
		/*
		webView = (WebView) findViewById(R.id.webView1);
		
	    webView.getSettings().setJavaScriptEnabled(true);  
	    webView.setWebChromeClient(new WebChromeClient() {
	    	@Override
	    	public boolean onConsoleMessage(ConsoleMessage cm) {
	    		
	    		Log.d(TAG3,"console.log: "+ cm.message() + " line: "+cm.lineNumber()+ cm.sourceId() );
	    		return true;
	    	}
	    	
	    	
		});
		webView.getSettings().setPluginState(PluginState.ON);
		webView.getSettings().setUserAgent(USER_DESKTOP);
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.addJavascriptInterface(this, "megaMipJSInterface");    
        
       
        */
		
		//super.loadUrl("file:///android_asset/www/index.html");
		super.loadUrl("file:///android_asset/www/index.html");
		webView = super.appView;
		webView.addJavascriptInterface(this, "megaMipJSInterface");  
        invoker = new Invoker();
        receiver = new MipReceiver(handler, webView, this);
        mc = new MipCommand();
        
        
        mSpeakNowDlg = new SpeakNow("Speak now !!",context);
        setListeners();
       // loadPage("index.html");
        //loadPage("test.html");

	}
	
	public void loadPage(String in) {
		final String url = HTML_ROOT + in;
		
		Log.d(TAG3, " loadPage url = "+url);
    	loadURL(url);
		
	}

	
	  private void loadURL(final String in){
	    	handler.post(new Runnable() {
	            public void run() {
	            	webView.loadUrl(in);
	            }
	        });
	    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	
	



private void voiceHandler(VoiceInput command) {



String[] args = command.getArgs();
String action = command.getAction();


String keywords ="";
String apiSelect= "";

	for(int i = 0; i < args.length; i++){
		keywords += args[i]+" ";
	}



 Log.d(TAG3, "WebViewActivity launch - call of callJsFunction ");

 
	  // if(action.equals("picture"))
	   mCommand = mc.new PictureSearch(receiver, keywords);
	   if(action.equals("video"))
	   mCommand = mc.new VideoSearch(receiver, keywords);
	   if(action.equals("next"))
		   mCommand = mc.new GuiNext(receiver);
	   if(action.equals("home"))
		   mCommand = mc.new GuiHome(receiver);
	   if(action.equals("show"))
		   mCommand = mc.new GuiShow(receiver);
	   if(action.equals("back"))
		   mCommand = mc.new GuiBack(receiver);
		   
	   invoker.launch(mCommand);

}


protected void movementHandler(MovementInput movementInput) {
	
	
	
	String action = movementInput.getAction();
	 
	if(action.equals("82"))          // decimal ascii code for "R" - right 
		mCommand = mc.new GuiNext(receiver);
	else
		mCommand = mc.new Speak(receiver);  // it's a left movement
	
		
     Log.d(TAG2,"MainActivity - movementHandler ----- action = "+action);
	
	invoker.launch(mCommand);

	
}

private void setListeners() {
	
	// Listener for the usb device 
	
	mipUsbDevice  = new MipUsbDevice(context);
	mipUsbDevice.addUsbListener(new UsbListener() {
		
		@Override
		public void onNotify(UsbEvent e) {
			byte[] data = e.getData();
			movementHandler(new MovementInput(data));
			
		}

	
	});
     
  //Listener for the mic device 
	
	 mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);       
     mSpeechRecognizer.setRecognitionListener(new SpeechListener());        
	
	
}

// onSpeak is called through the javascript interface : megaMipJSInterface
public void onSpeak() {
	// TODO Auto-generated method stub
	/*Intent intent = new Intent(
			RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

	intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

	try {
		startActivityForResult(intent, RESULT_SPEECH);
	
	} catch (ActivityNotFoundException a) {
		Toast t = Toast.makeText(getApplicationContext(),
				"Ops! Your device doesn't support Speech to Text",
				Toast.LENGTH_SHORT);
		t.show();
	}*/
	
	

    
   	handler.post(new Runnable() {
            public void run() {
           	
            	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);        
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1); 
                
                mSpeechRecognizer.startListening(intent);
                Log.i("A3","MainActivity -- onSpeak() ");
           	  
            }
        });
}


// inner class to implement the RecognitionListener interface 

class SpeechListener implements RecognitionListener          
{
         public void onReadyForSpeech(Bundle params)
         {
        	  
        	   /*WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
        	    lWindowParams.copyFrom(getDialog().getWindow.getAttributes());
        	    */
        	    mSpeakNowDlg.show();
        	    mSpeakNowDlg.showListening();
        	//	mCommand = mc.new GuiShowMic(receiver);   // we launch the mic animation on the GUI
        	//	invoker.launch(mCommand);
                  Log.d(TAG3, "onReadyForSpeech");
         }
         public void onBeginningOfSpeech()
         {
                  Log.d(TAG3, "onBeginningOfSpeech");
         }
         public void onRmsChanged(float rmsdB)
         {
        	 
        	 mSpeakNowDlg.updateVoiceMeter(rmsdB);
               //   Log.d(TAG, "onRmsChanged");
         }
         public void onBufferReceived(byte[] buffer)
         {
                if(0 == compteur1){ 
                	Log.d(TAG3, "onBufferReceived");
                	compteur1 = 1;
                }
         }
         public void onEndOfSpeech()
         {
        	 	mSpeakNowDlg.dismiss();
        	 
        	 // mCommand = mc.new GuiHideMic(receiver);   // we hide the mic animation on the GUI
     		 // invoker.launch(mCommand);
                  Log.d(TAG3, "onEndofSpeech");
         }
         public void onError(int error)
         { 
        	 compteur1 = 0;
        	  mSpeakNowDlg.dismiss();
        	// mCommand = mc.new GuiHideMic(receiver);   // we hide the mic animation on the GUI
     		 // invoker.launch(mCommand);
                  Log.d(TAG3,  "onError " +  error);
             //     mText.setText("error " + error);
                  
             if(SpeechRecognizer.ERROR_NO_MATCH == error)    {
            	 String message = "Recognition problem : the system is not able to recognize this word";
            	 mCommand = mc.new GuiShowMessage(receiver, message);  
            	 invoker.launch(mCommand);
             } 
               
         }
         public void onResults(Bundle results)                   
         {
        	 compteur1 = 0;
                  String str = new String();
                  
                 
                  
                  Log.d(TAG3, "onResults " + results);
                  ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                  for (int i = 0; i < data.size(); i++)
                  {
                            Log.d(TAG3, "result " + data.get(i));
                            str += data.get(i);
                  }
                  
                  
                VoiceInput voiceCommand = new VoiceInput(str);
  				Log.d(TAG3,"onResults -----vc= "+voiceCommand);
  				voiceHandler(voiceCommand);
  				
           //       mText.setText("results: "+String.valueOf(data.size()));   
             //    refDialog.dismiss();
                  
         }
         public void onPartialResults(Bundle partialResults)
         {
                  Log.d(TAG3, "onPartialResults");
         }
         public void onEvent(int eventType, Bundle params)
         {
                  Log.d(TAG3, "onEvent " + eventType);
         }
}



@Override
public void onPause() {
	
  super.onPause();	
  if(mSpeakNowDlg.isShowing()){
	  mSpeakNowDlg.dismiss();
	  Log.d(TAG3,"MainActivity onPause() block if");
  }
  
  Log.d(TAG3,"MainActivity onPause()");
}

}
