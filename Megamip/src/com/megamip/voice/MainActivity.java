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
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.megamip.voice.MipUsbDevice.UsbEvent;
import com.megamip.voice.MipUsbDevice.UsbListener;

public class MainActivity extends Activity {
	
	
	public static final int USER_MOBILE  = 0;
	public static final int USER_DESKTOP = 1;
	
	public static  Context context;  // reference vers l'activité MainActivity 
	protected static final int RESULT_SPEECH = 1;
	public static final String TAG = "A3";
	public static final String TAG2 = "A2";
	private ImageButton btnSpeak;
	private TextView textView;
	private EditText editText1;
	private WebView webView;
	private String accountType = null;
	private String accountName = null;
	private Handler handler = null;
	private MipUsbDevice mipUsbDevice;
	private static final String HTML_ROOT = "file:///mnt/sdcard/DCIM/gui/";	
	private Command mCommand;
	private Invoker invoker;
	private MipReceiver receiver;
	private MipCommand mc;
	private SpeakAgainDlg mSpeakAgainDlg;
	
	private SpeechRecognizer mSpeechRecognizer;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		context = this;
	
		webView = (WebView) findViewById(R.id.webView1);
		handler = new Handler();
	    webView.getSettings().setJavaScriptEnabled(true);  
	    webView.setWebChromeClient(new WebChromeClient() {
		});
		webView.getSettings().setPluginState(PluginState.ON);
	//	webView.getSettings().setUserAgent(USER_DESKTOP);
        webView.addJavascriptInterface(this, "contactSupport");    
        
        setListeners();
        loadPage("index.html");
        
        invoker = new Invoker();
        receiver = new MipReceiver(handler, webView, this);
        mc = new MipCommand();
        

	}
	
	public void loadPage(String in) {
		final String url = HTML_ROOT + in;
		
		Log.d(TAG, " loadPage url = "+url);
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
	
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SPEECH: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> text = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			
				
				VoiceInput voiceCommand = new VoiceInput(text.get(0));
				Log.d(TAG2,"MainActivity - onActivityResult -----vc= "+voiceCommand);
				voiceHandler(voiceCommand);
			}
			break;
		}

		}
	}*/
	
	
	



private void voiceHandler(VoiceInput command) {



String[] args = command.getArgs();
String action = command.getAction();


String keywords ="";
String apiSelect= "";

	for(int i = 0; i < args.length; i++){
		keywords += args[i]+" ";
	}



 Log.d(TAG, "WebViewActivity launch - call of callJsFunction ");

 
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
	
	// usb device 
	
	mipUsbDevice  = new MipUsbDevice(context);
	mipUsbDevice.addUsbListener(new UsbListener() {
		
		@Override
		public void onNotify(UsbEvent e) {
			byte[] data = e.getData();
			movementHandler(new MovementInput(data));
			
		}

	
	});
     
  // mic device 
	
	 mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);       
     mSpeechRecognizer.setRecognitionListener(new SpeechListener());        
	
	
}


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
	
	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);        
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1); 
    
    mSpeechRecognizer.startListening(intent);
    Log.i("A3","MainActivity -- onSpeak() ");

    
}


// inner class to implement the RecognitionListener interface 

class SpeechListener implements RecognitionListener          
{
         public void onReadyForSpeech(Bundle params)
         {
        	    mSpeakAgainDlg = new SpeakAgainDlg("Speak now !!",context);
        	    mSpeakAgainDlg.show();
                  Log.d(TAG, "onReadyForSpeech");
         }
         public void onBeginningOfSpeech()
         {
                  Log.d(TAG, "onBeginningOfSpeech");
         }
         public void onRmsChanged(float rmsdB)
         {
                  Log.d(TAG, "onRmsChanged");
         }
         public void onBufferReceived(byte[] buffer)
         {
                  Log.d(TAG, "onBufferReceived");
         }
         public void onEndOfSpeech()
         {
        	 	mSpeakAgainDlg.dismiss();
                  Log.d(TAG, "onEndofSpeech");
         }
         public void onError(int error)
         { 
        	 
        	 mSpeakAgainDlg.dismiss();
                  Log.d(TAG,  "error " +  error);
             //     mText.setText("error " + error);
         }
         public void onResults(Bundle results)                   
         {
                  String str = new String();
                  
                 
                  
                  Log.d(TAG, "onResults " + results);
                  ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                  for (int i = 0; i < data.size(); i++)
                  {
                            Log.d(TAG, "result " + data.get(i));
                            str += data.get(i);
                  }
                  
                  
                VoiceInput voiceCommand = new VoiceInput(str);
  				Log.d(TAG2,"onResults -----vc= "+voiceCommand);
  				voiceHandler(voiceCommand);
  				
           //       mText.setText("results: "+String.valueOf(data.size()));   
             //    refDialog.dismiss();
                  
         }
         public void onPartialResults(Bundle partialResults)
         {
                  Log.d(TAG, "onPartialResults");
         }
         public void onEvent(int eventType, Bundle params)
         {
                  Log.d(TAG, "onEvent " + eventType);
         }
}

}
