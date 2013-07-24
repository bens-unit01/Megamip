package com.megamip.voice;

import java.util.ArrayList;

import com.megamip.voice.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public static  Context context;  // reference vers l'activité MainActivity 
	protected static final int RESULT_SPEECH = 1;
	public static final String TAG = "A3";
	private ImageButton btnSpeak;
	private TextView txtText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		context = this;
		txtText = (TextView) findViewById(R.id.txtText);

		btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

		
		btnSpeak.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(
						RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

				try {
					startActivityForResult(intent, RESULT_SPEECH);
					txtText.setText("");
				} catch (ActivityNotFoundException a) {
					Toast t = Toast.makeText(getApplicationContext(),
							"Ops! Your device doesn't support Speech to Text",
							Toast.LENGTH_SHORT);
					t.show();
				}
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

				txtText.setText(text.get(0));
				
				VoiceCommand voiceCommand = new VoiceCommand(text.get(0));
				
				launchResults(voiceCommand);
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

}
