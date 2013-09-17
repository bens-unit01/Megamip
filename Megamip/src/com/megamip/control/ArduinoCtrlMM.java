package com.megamip.control;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import com.megamip.util.UsbCommand;
import com.megamip.voice.MipUsbDevice;

public class ArduinoCtrlMM implements ArduinoCtrl{
	
	public static boolean listen =false;
	public static final String TAG3 = "A3";
	
	private MipUsbDevice mipUsbDevice;
	private Context context;
	
	// constructor 
	private ArduinoCtrlMM(){}
	public ArduinoCtrlMM(Context context) {
		super();
		mipUsbDevice = MipUsbDevice.getInstance(context);
		this.context = context;
	}
	
	
	//  implemented methods 

	@Override
	public int drive(int speedL, int speedR, int distanceL, int distanceR) {


		byte[] packet = new byte[]{
				  UsbCommand.START_BYTE,
				  UsbCommand.DRIVE,
				  new Integer(speedL).byteValue(),
				  new Integer(speedR).byteValue(),
				  new Integer(distanceL).byteValue(),
				  new Integer(distanceR).byteValue(),
				  UsbCommand.END_BYTE};
		mipUsbDevice.writeAsync(packet);
		
		Log.d(TAG3,"ArduinoCtrlMM - send  cmd:"+packet[0]+" "+packet[1]+" "+packet[2]+" "+packet[3]
				+" "+packet[4]+" "+packet[5]+" "+packet[6]);
		
		return 0;
	}

	

	@Override
	public int driveQueue(int speedL, int speedR, int distanceL, int distanceR) {
		mipUsbDevice.writeAsync(new byte[]{
				  UsbCommand.START_BYTE,
				  UsbCommand.DRIVE_QUEUE,
				  new Integer(speedL).byteValue(),
				  new Integer(speedR).byteValue(),
				  new Integer(distanceL).byteValue(),
				  new Integer(distanceR).byteValue(),
				  UsbCommand.END_BYTE});
		return 0;
	}

	@Override
	public int upright() {
		mipUsbDevice.writeAsync(new byte[]{
				UsbCommand.START_BYTE,
				UsbCommand.UPRIGHT,
				UsbCommand.END_BYTE});
		return 0;
	}

	@Override
	public int fallover() {
		mipUsbDevice.writeAsync(new byte[]{
				UsbCommand.START_BYTE,
				UsbCommand.FALLOVER,
				UsbCommand.END_BYTE});
		return 0;
	}

	@Override
	public int stop() {
		mipUsbDevice.writeAsync(new byte[]{
			    UsbCommand.START_BYTE,
				UsbCommand.STOP,
				UsbCommand.END_BYTE});
		return 0;
	}

	@Override
	public int reportEncoders(int refreshRate) {
		mipUsbDevice.writeAsync(new byte[]{
				UsbCommand.START_BYTE,
				UsbCommand.REPORT_ENCODERS,
				UsbCommand.END_BYTE});
		return 0;
	}


	@Override
	public void listen() {
		
	/*	Log.d(TAG3,"ArduinoCtrlMM listen ok ..." );
		UsbCommunication.addUsbListener(context);
		*/
	}


	@Override
	public void stopListen() {
		/*
		UsbCommunication.removeUsbListener(context);
		*/
	}

}
