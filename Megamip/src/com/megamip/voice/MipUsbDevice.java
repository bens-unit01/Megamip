package com.megamip.voice;


// code of this class is partially inspired from :http://code.google.com/p/usb-serial-for-android/source/browse/UsbSerialLibrary/src/com/hoho/android/usbserial/util/SerialInputOutputManager.java
/**
 * 
 * @author Messaoud BENSALEM
 * @version 1.0 07/09/13 
 * 
 * This class provide a listener to an usb device through
 * the SerialListener interface according to the Observer design pattern
 * 
 */

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.*;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.*;



public class MipUsbDevice {

	public static final String TAG = "A3";

	// attributes
	private ArrayList<UsbListener> mListeListeners = new ArrayList<UsbListener>();
	private UsbManager mUsbManager;
	private UsbSerialDriver mUsbDriver;
	private SerialInputOutputManager mSerialIoManager;
	private static MipUsbDevice mMipUsbDevice = null;
	
	private final ExecutorService mExecutor = Executors
			.newSingleThreadExecutor();
	
	
	// constructors 
	
       public static MipUsbDevice getInstance(Context context){
    	   if(null == mMipUsbDevice){
    		   mMipUsbDevice = new MipUsbDevice(context);
    	   }
    	   
    	 
    	   
    	   return mMipUsbDevice;
       }
		private MipUsbDevice() {
			super();
		}

		private MipUsbDevice(Context context) {
			super();

			mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
			mUsbDriver = UsbSerialProber.acquire(mUsbManager);
			Log.d(TAG, "MipUsbDevice constructor - before try - mSerialDevice = "+mUsbDriver+" mUsbManager"+mUsbManager);
			
			try {
				mUsbDriver.open();
				mUsbDriver.setBaudRate(115200);
				Log.d(TAG,"MipUsbDevice  - driver open - baudrate = 115200");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG,"MipUsbDevice - constructor - bloc catch ex = "+e.getMessage());
			}catch(NullPointerException e){
				
				e.printStackTrace();
				Log.e(TAG,"MipUsbDevice - constructor - bloc catch ex = "+e.getMessage());
			}
		//	stopIoManager();
			startIoManager();
		}

	private final SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {

		@Override
		public void onRunError(Exception e) {
			Log.d(TAG, "UsbDevice onRunError "+e.getMessage());
		}

		@Override
		public void onNewData(final byte[] data) {

			// ---- notifyAll
		   
			Log.d(TAG, "UsbDevice onNewData -----  ");
			
			for (UsbListener b : mListeListeners) {
				
				b.onNotify(new UsbEvent(this, data));
				Log.d(TAG, "UsbDevice  onNewData 2  ");
			}
		}
	};

	
	
	// inner interface to implement the Observer design pattern 

	public interface UsbListener {

		public void onNotify(UsbEvent e);

	}

	
	
	public class UsbEvent {
		private Object sender;
		private byte[] data;

		public Object getSender() {
			return sender;
		}

		public byte[] getData() {
			return data;
		}

		public UsbEvent() {
			super();
			// TODO Auto-generated constructor stub
		}

	
		
		public UsbEvent(Object sender, byte[] data) {
			super();
			Log.d(TAG, "UsbDevice 3 ");
			this.sender = sender;
			this.data = data;
		}

	}

	public void addUsbListener(UsbListener usbListener) {
		mListeListeners.add(usbListener);
		Log.d(TAG, "UsbDevice 4 mSerialDriver = "+mUsbDriver);

	}
	
	

	//

	
	private void startIoManager() {
		if (mUsbDriver != null) {
			Log.i(TAG, "Starting io manager ..");
			mSerialIoManager = new SerialInputOutputManager(mUsbDriver,
					mListener);
			mExecutor.submit(mSerialIoManager);
			Log.d(TAG, "UsbDevice 6");
		}
	}
	
	 private void stopIoManager() {
	        if (mSerialIoManager != null) {
	            Log.i(TAG, "Stopping io manager ..");
	            mSerialIoManager.stop();
	            mSerialIoManager = null;
	        }
	    }

	 
	 // public methods 
	public void pause() {
		
		stopIoManager();
		if(mUsbDriver != null)
			try{
					mUsbDriver.close( );
					
	}catch(IOException e){}
	
	mUsbDriver = null;
	}

	public void resume() {
	
		 mUsbDriver = UsbSerialProber.acquire(mUsbManager);
	        Log.d(TAG, "Resumed, mSerialDevice=" + mUsbDriver);
	        if (mUsbDriver == null) {
	           
	        } else {
	            try {
	                mUsbDriver.open();
	                mUsbDriver.setBaudRate(115200);
	            } catch (IOException e) {
	                Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
	               
	                try {
	                    mUsbDriver.close();
	                } catch (IOException e2) {
	                    // Ignore.
	                }
	                mUsbDriver = null;
	                return;
	            }
	     
	        }
		stopIoManager();
		startIoManager();
		
	}

	public void writeAsync(byte[] data){
		//mSerialIoManager.writeAsync(data);
		try {
			mUsbDriver.write(data, 1000);
			Log.d(TAG,"writeAsync - data:"+data[0]+" "+data[1]);
		} catch (IOException e) {
		
			Log.d(TAG,"MipUsbDevice - writeAsync - block catch ex:"+e.getMessage());
		}
		
	}

}
