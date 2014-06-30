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
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hoho.android.usbserial.driver.*;
import com.hoho.android.usbserial.util.*;
import com.megamip.util.UsbCommand;

public class MipUsbDevice {

	public static final String TAG3 = "A3";

	// attributes
	private ArrayList<UsbListener> mListeListeners = new ArrayList<UsbListener>();
	private UsbManager mUsbManager;
	private UsbSerialDriver mUsbDriver;
	private SerialInputOutputManager mSerialIoManager;
	private static MipUsbDevice mMipUsbDevice = null;
	private static MipUsbDevice mMipUsbDeviceUno = null;
	private static MipUsbDevice mMipUsbDeviceNano = null;
	private static MipUsbDevice mMipUsbDeviceMicro = null;

	private class PartialResult {
		public byte[] buffer;
		public boolean flag;
		public int lastIndex;

		public PartialResult() {
			flag = false;
			buffer = new byte[10];
			lastIndex = 0;
		}
	}

	private PartialResult partialResult;

	// public static final String NANO = "nano", UNO = "uno";
	// public static final int NANO_PRODUCT_ID = 24577, UNO_PRODUCT_ID = 67;
	// test avec le Sparkfun pro micro

	public static final int MICRO_PRODUCT_ID = 37380, UNO_PRODUCT_ID = 67,
			NANO_PRODUCT_ID = 24577;

	public enum DeviceType {
		NANO, UNO, MICRO
	}

	private final ExecutorService mExecutor = Executors
			.newSingleThreadExecutor();

	// constructors

	public static MipUsbDevice getInstance(Context context) {
		if (null == mMipUsbDevice) {
			mMipUsbDevice = new MipUsbDevice(context);
		}

		return mMipUsbDevice;
	}

	public static MipUsbDevice getInstance(Context context, DeviceType type) {

		MipUsbDevice returnValue = null;

		if (type == DeviceType.UNO)
			returnValue = mMipUsbDeviceUno;
		if (type == DeviceType.NANO)
			returnValue = mMipUsbDeviceNano;
		if (type == DeviceType.MICRO)
			returnValue = mMipUsbDeviceMicro;

		if (null == mMipUsbDeviceUno && type == DeviceType.UNO) {
			mMipUsbDeviceUno = new MipUsbDevice(context, DeviceType.UNO);
			returnValue = mMipUsbDeviceUno;
		} else if (null == mMipUsbDeviceNano && type == DeviceType.NANO) {
			mMipUsbDeviceNano = new MipUsbDevice(context, DeviceType.NANO);
			returnValue = mMipUsbDeviceNano;
		} else if (null == mMipUsbDeviceMicro && type == DeviceType.MICRO) {
			mMipUsbDeviceMicro = new MipUsbDevice(context, DeviceType.MICRO);
			returnValue = mMipUsbDeviceMicro;
		}

		return returnValue;
	}

	private MipUsbDevice() {
		super();

	}

	private MipUsbDevice(Context context, DeviceType type) {
		super();

		partialResult = new PartialResult();
		

		mUsbManager = (UsbManager) context
				.getSystemService(Context.USB_SERVICE);
		HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
		UsbDevice usbDevice = null;
		for (UsbDevice element : deviceList.values()) {

			if (NANO_PRODUCT_ID == element.getProductId()
					&& type == DeviceType.NANO) {
				usbDevice = element;
			}

			if (UNO_PRODUCT_ID == element.getProductId()
					&& type == DeviceType.UNO) {
				usbDevice = element;
			}

			if (MICRO_PRODUCT_ID == element.getProductId()
					&& type == DeviceType.MICRO) {
				usbDevice = element;
			}
		}

		mUsbDriver = UsbSerialProber.acquire(mUsbManager, usbDevice);
		// mUsbDriver = UsbSerialProber.acquire(mUsbManager);
		Log.d(TAG3, "MipUsbDevice constructor - before try - mSerialDevice = "
				+ mUsbDriver + " mUsbManager" + mUsbManager);

		try {

			mUsbDriver.open();
			// mUsbDriver.setBaudRate(115200);
			mUsbDriver.setParameters(115200, UsbSerialDriver.DATABITS_8,
					UsbSerialDriver.STOPBITS_1, UsbSerialDriver.PARITY_NONE);
			mUsbDriver.setDTR(true);
			Log.d(TAG3, "MipUsbDevice  - driver open - baudrate = 115200");
		} catch (IOException e) {
			
			try {
				mUsbDriver.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			Log.e(TAG3,
					"MipUsbDevice - constructor - bloc catch ex = "
							+ e.getMessage());
		} catch (NullPointerException e) {

			e.printStackTrace();
			Log.e(TAG3,
					"MipUsbDevice - constructor - bloc catch ex = "
							+ e.getMessage());
		}
	    stopIoManager();
		startIoManager();
	}

	private MipUsbDevice(Context context) {
		super();

		partialResult = new PartialResult();
	

		mUsbManager = (UsbManager) context
				.getSystemService(Context.USB_SERVICE);
		UsbAccessory[] a = mUsbManager.getAccessoryList();
		HashMap<String, UsbDevice> b = mUsbManager.getDeviceList();
		mUsbDriver = UsbSerialProber.acquire(mUsbManager);
		Log.d(TAG3, "MipUsbDevice constructor - before try - mSerialDevice = "
				+ mUsbDriver + " mUsbManager" + mUsbManager);

		try {
			mUsbDriver.open();
			// mUsbDriver.setBaudRate(115200);
			mUsbDriver.setParameters(115200, UsbSerialDriver.DATABITS_8,
					UsbSerialDriver.STOPBITS_1, UsbSerialDriver.PARITY_NONE);
			Log.d(TAG3, "MipUsbDevice  - driver open - baudrate = 115200");
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG3,
					"MipUsbDevice - constructor - bloc catch ex = "
							+ e.getMessage());
		} catch (NullPointerException e) {

			e.printStackTrace();
			Log.e(TAG3,
					"MipUsbDevice - constructor - bloc catch ex = "
							+ e.getMessage());
		}
	    stopIoManager();
		startIoManager();
	}
	
	
	public boolean isUsbConnected(){
		return mUsbDriver != null;
	}

	/*
	 * this method checks if we have a usb connection to the NANO and the UNO
	 */

	public static boolean isAllUSBConnected(Context context) {

		boolean unoConnected = false;
		boolean nanoConnected = false;
		UsbManager usbManager = (UsbManager) context
				.getSystemService(Context.USB_SERVICE);

		HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
		for (UsbDevice element : deviceList.values()) {

			if (NANO_PRODUCT_ID == element.getProductId()) {
				nanoConnected = true;
				Log.d("A3", "nano connected ...");
			}

			if (UNO_PRODUCT_ID == element.getProductId()) {
				unoConnected = true;
				Log.d("A3", "uno connected ...");
			}
		}

		return unoConnected && nanoConnected;
	}

	private final SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {

		@Override
		public void onRunError(Exception e) {
			Log.d(TAG3, "UsbDevice onRunError " + e.getMessage());
		}

		@Override
		public void onNewData(final byte[] data) {

			// ---- notifyAll

			Log.d(TAG3, "UsbDevice onNewData -----  ");
			if (partialResult.flag) {

				int j = partialResult.lastIndex;
				for (int i = 0; i < data.length; i++) {
					partialResult.buffer[j + i] = data[i];

				}
				partialResult.lastIndex = 0;
				partialResult.flag = false;
				for (UsbListener b : mListeListeners) {

					b.onNotify(new UsbEvent(this, partialResult.buffer));
					Log.d(TAG3, "UsbDevice  onNewData 1  ");
				}
				
              
			}else if (includeValue(data, UsbCommand.END_BYTE)
					&& (data.length > 3)) {
				for (UsbListener b : mListeListeners) {

					b.onNotify(new UsbEvent(this, data));
					Log.d(TAG3, "UsbDevice  onNewData 2  ");
				}
			} else { // we dont have the whole data, we save the data we just
						// got

				for (int i = 0; i < data.length; i++) {
					partialResult.buffer[i] = data[i];
					partialResult.lastIndex = i;
				}
				partialResult.lastIndex++;
				partialResult.flag = true;
			}
		}

	};

	private boolean includeValue(byte[] data, int value) {
		boolean returnValue = false;
		for (byte b : data) {
			if (b == value) {
				returnValue = true;
			}
		}
		return returnValue;
	}

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
		}

		public UsbEvent(Object sender, byte[] data) {
			super();
			Log.d(TAG3, "UsbDevice 3 ");
			this.sender = sender;
			this.data = data;
		}

	}

	public void addUsbListener(UsbListener usbListener) {
		mListeListeners.add(usbListener);
		Log.d(TAG3, "UsbDevice 4 mSerialDriver = " + mUsbDriver);

	}

	//

	private void startIoManager() {
		if (mUsbDriver != null) {
			Log.i(TAG3, "Starting io manager ..");
			mSerialIoManager = new SerialInputOutputManager(mUsbDriver,
					mListener);
			mExecutor.submit(mSerialIoManager);
			Log.d(TAG3, "UsbDevice 6");
		}
	}

	private void stopIoManager() {
		if (mSerialIoManager != null) {
			Log.i(TAG3, "Stopping io manager ..");
			mSerialIoManager.stop();
			mSerialIoManager = null;
		}
	}

	// public methods
	public void pause() {

		stopIoManager();
		if (mUsbDriver != null)
			try {
				mUsbDriver.close();

			} catch (IOException e) {
			}

		mMipUsbDeviceUno = null;
		mUsbDriver = null;
	}

	public void resume() {

		mUsbDriver = UsbSerialProber.acquire(mUsbManager);
		Log.d(TAG3, "Resumed, mSerialDevice=" + mUsbDriver);
		if (mUsbDriver == null) {

		} else {
			try {
				mUsbDriver.open();
				// mUsbDriver.setBaudRate(115200);
				mUsbDriver
						.setParameters(115200, UsbSerialDriver.DATABITS_8,
								UsbSerialDriver.STOPBITS_1,
								UsbSerialDriver.PARITY_NONE);
			} catch (IOException e) {
				Log.e(TAG3, "Error setting up device: " + e.getMessage(), e);

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

	public void writeAsync(byte[] data) {
		// mSerialIoManager.writeAsync(data);
		try {
			mUsbDriver.write(data, 1000);
			// Log.d(TAG3, "writeAsync - data:" + data[0] + " " + data[1]);
		} catch (Exception e) {

			Log.d(TAG3,
					"MipUsbDevice - writeAsync - block catch ex:"
							+ e.getMessage());
			e.printStackTrace();
		}

	}

}
