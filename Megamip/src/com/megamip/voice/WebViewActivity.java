package com.megamip.voice;

import com.megamip.voice.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class WebViewActivity extends Activity {
	
	public static final String TAG = "A3";
	private TextView textView;
	private EditText editText1;
	private WebView webView;
	private Button btnGo, btnNext, btnShow;
	private String accountType = null;
	private String accountName = null;
	private Handler handler = null;
	private static final String JAVASCRIPT = "javascript:";
	private static final String BRC = "()";
	private static final String BRC_OPEN = "('";
	private static final String BRC_CLOSE = "')";
	private static final String Q = "?";
	private static final String HTML_ROOT = "file:///mnt/sdcard/DCIM/gui/";	

	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);

		webView = (WebView) findViewById(R.id.webView1);
		handler = new Handler();
	    webView.getSettings().setJavaScriptEnabled(true);  
        webView.addJavascriptInterface(this, "contactSupport");    
        setWidgets();
        setListeners();
        loadPage("index.html");

		Bundle extras = getIntent().getExtras();
	    VoiceCommand command = (VoiceCommand)extras.get("command");
		
	    Log.d(TAG, "WebViewActivity command = "+command);
		launch(command);
	    
	    
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

			//	       webView.loadData(customHtml, "text/html", "UTF-8");
		 Log.d(TAG, "WebViewActivity launch - call of callJsFunction ");
	    callJsFunction("pictureSearch", keywords);
	    
	}






private void setListeners() {
	
	
	//--- btnGo 
	
	btnGo.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			
			String input = editText1.getText().toString();
			 callJsFunction("pictureSearch", input);
			// callJsFunction("videoSearch", input);
			//loadURL("http://www.google.com");
			
		}
	});
	
	
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
	btnGo = (Button)findViewById(R.id.btnGo);
	btnShow = (Button)findViewById(R.id.btnShow);
	btnNext = (Button)findViewById(R.id.btnNext);
	editText1 = (EditText)findViewById(R.id.editText1);
	
}


}