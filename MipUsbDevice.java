package com.megamip.usb;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.util.Log;


import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 
 * @author Messaoud BENSALEM
 * @version 1.0 07/09/13
 * This class provide a listener to an usb device through the SerialListener interface 
 * according to the Observer design pattern 
 * 
 */

public class MipUsbDevice {
	
	public static final String TAG = "A3";
	// attributes 
	 private UsbManager mUsbManager;
	 private UsbSerialDriver mSerialDevice;
	 private SerialInputOutputManager mSerialIoManager;
	 private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
	 private final SerialInputOutputManager.Listener mListener =
	            new SerialInputOutputManager.Listener() {



	        @Override
	        public void onRunError(Exception e) {
	            Log.d(TAG, "UsbDevice onRunError ");
	        }


	        @Override
	        public void onNewData(final byte[] data) {
	            
			
			// fire event -- 
	        }
	    };

	  
	    
	    // 
	    
	 // constructors 
	 
	 public MipUsbDevice() {
			super();
	 }
	 
	 public MipUsbDevice(Context context) {
			super();
		
			mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);	
			mSerialDevice = UsbSerialProber.acquire(mUsbManager);
			
	 }
	 
	 

	  private void startIoManager() {
		        if (mSerialDevice != null) {
		            Log.i(TAG, "Starting io manager ..");
		            mSerialIoManager = new SerialInputOutputManager(mSerialDevice, mListener);
		            mExecutor.submit(mSerialIoManager);
		        }
	 }	    
		    
	 
}
