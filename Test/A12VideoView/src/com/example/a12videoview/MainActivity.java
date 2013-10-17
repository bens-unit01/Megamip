package com.example.a12videoview;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
//		 Intent lVideoIntent = new Intent(null, Uri.parse("ytv://"+"bBH0ele7qdw"), this, IntroVideoActivity.class);
//		      lVideoIntent.putExtra("com.keyes.video.msg.init", "str_video_intro");
//		      lVideoIntent.putExtra("com.keyes.video.msg.detect", "str_video_detecting_bandwidth");
//	
//		     startActivity(lVideoIntent);
		
		startActivity(new Intent(this, FullscreenDemoActivity.class));
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
