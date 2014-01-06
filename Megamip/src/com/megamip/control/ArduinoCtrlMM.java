package com.megamip.control;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import com.megamip.util.UsbCommand;
import com.megamip.voice.MipUsbDevice;
import com.megamip.voice.MipUsbDevice.DeviceType;

public class ArduinoCtrlMM implements ArduinoCtrl{
	
	public static boolean listen =false;
	public static final String TAG3 = "A3";
	
	private MipUsbDevice mipUsbDeviceUno;
	private Context context;
	
	// constructor 
	private ArduinoCtrlMM(){}
	public ArduinoCtrlMM(Context context) {
		super();
		mipUsbDeviceUno = MipUsbDevice.getInstance(context, DeviceType.UNO);
		this.context = context;
	}
	
	
	//  implemented methods 

	@Override
	public int drive(int speedL, int speedR, int distanceL, int distanceR) {


		byte[] packet = new byte[]{
				  UsbCommand.START_BYTE,
				//  UsbCommand.DRIVE,
				  new Integer(speedL).byteValue(),
				  new Integer(speedR).byteValue(),
				  new Integer(speedR).byteValue(),
				  new Integer(speedR).byteValue(),
				  new Integer(speedR).byteValue()};
		mipUsbDeviceUno.writeAsync(packet);
		
		Log.d(TAG3,"ArduinoCtrlMM - send  cmd:"+packet[0]+" "+packet[1]+" "+packet[2]+" "+packet[3]
				+" "+packet[4]+" "+packet[5]+" "+packet[6]);
		
		return 0;
	}

	

	@Override
	public int driveQueue(int speedL, int speedR, int distanceL, int distanceR) {
		mipUsbDeviceUno.writeAsync(new byte[]{
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
		mipUsbDeviceUno.writeAsync(new byte[]{
				UsbCommand.START_BYTE,
				UsbCommand.UPRIGHT,
				UsbCommand.END_BYTE});
		return 0;
	}

	@Override
	public int fallover() {
		mipUsbDeviceUno.writeAsync(new byte[]{
				UsbCommand.START_BYTE,
				UsbCommand.FALLOVER,
				UsbCommand.END_BYTE});
		return 0;
	}

	@Override
	public int stop() {
		mipUsbDeviceUno.writeAsync(new byte[]{
			    UsbCommand.START_BYTE,
				UsbCommand.STOP,
				UsbCommand.END_BYTE});
		return 0;
	}

	@Override
	public int reportEncoders(int refreshRate) {
		mipUsbDeviceUno.writeAsync(new byte[]{
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
