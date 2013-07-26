package com.megamip.voice;

import java.util.ArrayList;

import com.megamip.voice.R;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public static  Context context;  // reference vers l'activité MainActivity 
	protected static final int RESULT_SPEECH = 1;
	public static final String TAG = "A3";
	private ImageButton btnSpeak;
	private TextView textView;
	private EditText editText1;
	private WebView webView;
	private Button  btnNext, btnShow;
	private String accountType = null;
	private String accountName = null;
	private Handler handler = null;
	private static final String JAVASCRIPT = "javascript:";
	private static final String BRC = "()";
	private static final String BRC_OPEN = "('";
	private static final String BRC_CLOSE = "')";
	private static final String Q = "?";
	private static final String HTML_ROOT = "file:///mnt/sdcard/DCIM/gui/";	



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		context = this;
	
		webView = (WebView) findViewById(R.id.webView1);
		handler = new Handler();
	    webView.getSettings().setJavaScriptEnabled(true);  
        webView.addJavascriptInterface(this, "contactSupport");    
        setWidgets();
        setListeners();
        loadPage("index.html");

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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SPEECH: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> text = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			
				
				VoiceCommand voiceCommand = new VoiceCommand(text.get(0));
				
				launch(voiceCommand);
			}
			break;
		}

		}
	}
	
	
	
private void launchResults(VoiceCommand voiceCommand) {
		
	   	String action = voiceCommand.getAction();
		char action0 = action.toCharArray()[0];
		
		
		
		//Intent intent = new Intent(ref, WebViewActivity.class);
		//startActivity(intent);
		
		Log.d(TAG,"MainActivity launchResults action0 = "+action0);
		
		Intent intent = new Intent(context, WebViewActivity.class);
	    intent.putExtra("command", voiceCommand);
		startActivity(intent);
		
		
		
	}

public void callJsFunction(String functionName,String args){
	String json = "";
//	final String callbackFunction = JAVASCRIPT + "f1" + BRC_OPEN + json 
//  		+ BRC_CLOSE;
	final String callbackFunction = JAVASCRIPT + functionName+BRC_OPEN+args+BRC_CLOSE ;
	Log.d(TAG ,callbackFunction);
	loadURL(callbackFunction); 	  	
}

private void launch(VoiceCommand command) {



String[] args = command.getArgs();
String action = command.getAction();


String keywords ="";
String apiSelect= "";

for(int i = 0; i < args.length; i++){
	keywords += args[i]+" ";
}

/*
if(action.equals("picture") || action.equals("video")){
	if(action.equals("picture")){
		apiSelect = "new google.search.ImageSearch();";  // picture search
	}else{
		apiSelect = "new google.search.VideoSearch();";  // video search
	}
}else{
	apiSelect = "new google.search.LocalSearch();"; // google search
	keywords += action;
	
}

Log.d(TAG, "WebViewActivity keywords = "+keywords+" action = "+action);
//webView.loadUrl("http://www.google.com");

String customHtml = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Strict//EN'"+
         "'http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd'>" +
         		"<html xmlns='http://www.w3.org/1999/xhtml'>   <head> <meta http-equiv='content-type' content='text/html; charset=utf-8'/>"+
               " <title>MegaMip picture search</title>"+
               "<script src='https://www.google.com/jsapi'"+
               " type='text/javascript'></script>  <script language='Javascript' type='text/javascript'>"+
               " google.load('search', '1');"+
               "function OnLoad() {"+
               "  var searchControl = new google.search.SearchControl();"+
               " var localSearch = "+apiSelect+
               "searchControl.addSearcher(localSearch);"+
               "searchControl.draw(document.getElementById('searchcontrol'));"+
               "searchControl.execute('"+keywords+"');  }"+
               "google.setOnLoadCallback(OnLoad);   </script>   </head>"+
               "<body> <div id='searchcontrol'>Loading</div> </body> </html>";

	//	       webView.loadData(customHtml, "text/html", "UTF-8");*/

 Log.d(TAG, "WebViewActivity launch - call of callJsFunction ");
callJsFunction("clearScreen();hideEyes();pictureSearch", keywords);

}

private void setListeners() {
	
	
	// btnSpeak 
	
	btnSpeak.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			Intent intent = new Intent(
					RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

			try {
				startActivityForResult(intent, RESULT_SPEECH);
			
			} catch (ActivityNotFoundException a) {
				Toast t = Toast.makeText(getApplicationContext(),
						"Ops! Your device doesn't support Speech to Text",
						Toast.LENGTH_SHORT);
				t.show();
			}
		}
	});
	
	
	//--- btnGo 
	
	
	
	//--- btnNext 
	btnNext.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			callJsFunction("next", "");
			
		}
	});
	
	//--- btnShow
	btnShow.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			callJsFunction("show", "");
			
		}
	});
}

private void setWidgets() {
	
	textView = (TextView)findViewById(R.id.textView1);
	webView = (WebView)findViewById(R.id.webView1);
	btnShow = (Button)findViewById(R.id.btnShow);
	btnNext = (Button)findViewById(R.id.btnNext);
	editText1 = (EditText)findViewById(R.id.editText1);
	btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
	
}

}
