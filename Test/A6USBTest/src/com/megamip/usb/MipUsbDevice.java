package com.megamip.usb;



import android.content.Context;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.megamip.driver.usbserial.driver.UsbSerialDriver;
import com.megamip.driver.*;
import com.megamip.driver.usbserial.driver.UsbSerialProber;
import com.megamip.driver.usbserial.util.SerialInputOutputManager;

/**
 * 
 * @author Messaoud BENSALEM
 * @version 1.0 07/09/13 
 * 
 * This class provide a listener to an usb device through
 * the SerialListener interface according to the Observer design pattern
 * 
 */

public class MipUsbDevice {

	public static final String TAG = "A3";
	// attributes
	private ArrayList<UsbListener> mListeListeners = new ArrayList<UsbListener>();
	private UsbManager mUsbManager;
	private UsbSerialDriver mSerialDevice;
	private SerialInputOutputManager mSerialIoManager;
	
	
	private final ExecutorService mExecutor = Executors
			.newSingleThreadExecutor();
	
	
	// constructors 
	

		public MipUsbDevice() {
			super();
		}

		public MipUsbDevice(Context context) {
			super();

			mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
			mSerialDevice = UsbSerialProber.acquire(mUsbManager);
			Log.d(TAG, "MipUsbDevice constructor - before try - mSerialDevice = "+mSerialDevice+" mUsbManager"+mUsbManager);
			
			try {
				mSerialDevice.open();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d(TAG,"MipUsbDevice - constructor - bloc catch ex = "+e.getMessage());
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
			Log.d(TAG, "UsbDevice onNewData 1-----------------------");
//			for (UsbListener b : mListeListeners) {
//				b.onNotify(new UsbEvent(this, data));
//				Log.d(TAG, "UsbDevice  onNewData 2  ");
//			}
		}
	};

	
	
	// inner interface and class

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
		Log.d(TAG, "UsbDevice 4 mSerialDriver = "+mSerialDevice);

	}
	
	

	//

	
	private void startIoManager() {
		if (mSerialDevice != null) {
			Log.i(TAG, "Starting io manager ..");
			mSerialIoManager = new SerialInputOutputManager(mSerialDevice,
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

}
