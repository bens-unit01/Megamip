package com.megamip.voice;

import com.megamip.voice.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class WebViewActivity extends Activity {
	
	public static final String TAG = "A3";

	private WebView webView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);

		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		
		
		Bundle extras = getIntent().getExtras();
	    VoiceCommand command = (VoiceCommand)extras.get("command");
		
	    Log.d(TAG, "WebViewActivity command = "+command);
		
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

				       webView.loadData(customHtml, "text/html", "UTF-8");
	}

}