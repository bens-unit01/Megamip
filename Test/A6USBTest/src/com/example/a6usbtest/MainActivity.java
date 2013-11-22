package com.example.a6usbtest;

import com.megamip.usb.MipUsbDevice;
import com.megamip.usb.MipUsbDevice.UsbEvent;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.ScrollView;
import android.widget.TextView;



public class MainActivity extends Activity {

	
	public static final String TAG = "A3";

	private Context context;
	private ScrollView scrollView;
	private TextView   txtView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setWidgets();
		setListeners();
	}

	
    private void setWidgets() {
		
    	scrollView = (ScrollView)findViewById(R.id.scrollView);
    	txtView = (TextView)findViewById(R.id.textView1);
		
	}
	private void setListeners() {
        Log.d(TAG, "MainActivity setlinsteners");      
		context = this;
		MipUsbDevice mip = new MipUsbDevice(context);
        MipUsbListener mipListener = new MipUsbListener();
        
        mip.addUsbListener(mipListener);
		
		
		
		
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

	public class MipUsbListener implements MipUsbDevice.UsbListener {

		@Override
		public void onNotify(UsbEvent e) {
		 
			byte[] data = e.getData();
			
			Log.d(TAG,data.toString());
			txtView.setText("---\n");
		}
		
	}
}
