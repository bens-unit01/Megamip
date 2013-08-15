package com.megamip.voice;



import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;



public class SpeakAgainDlg extends Dialog {

	
	
	// members 
	private String mTitle;
	
	
	
	// constructor 
	public SpeakAgainDlg(String mTitle, Context context) {
		super(context);
		this.mTitle = mTitle;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.speak_now);
		
	//	TextView text = (TextView) this.findViewById(R.id.text);
	//	text.setText(mTitle);
		ImageView image = (ImageView) this.findViewById(R.id.image);
		image.setImageResource(R.drawable.android__voice_search);
	
	}


	
	
	
	
}
