package com.megamip.control;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import com.megamip.util.UsbCommand;
import com.megamip.voice.MipUsbDevice;
import com.megamip.voice.MipUsbDevice.DeviceType;

public class ArduinoCtrlMM implements ArduinoCtrl {

	public static boolean listen = false;
	public static final String TAG3 = "A3";

	private MipUsbDevice mipUsbDeviceUno;
	private MipUsbDevice mipUsbDeviceNano;
	private Context context;

	// constructor
	private ArduinoCtrlMM() {
	}

	public ArduinoCtrlMM(Context context) {
		super();
		mipUsbDeviceUno = MipUsbDevice.getInstance(context, DeviceType.UNO);
		mipUsbDeviceNano = MipUsbDevice.getInstance(context, DeviceType.NANO);
		this.context = context;
	}

	// implemented methods

	@Override
	public int drive(int speedL, int speedR, int distanceL, int distanceR) {

		final int sl = speedL;
		final int sr = speedR;
	
		new Thread(new Runnable() {

			@Override
			public void run() {

				byte turn = 0x00;
				turn = (sl > sr) ? (byte) 0x5A : (byte) 0xA6;
				int speed = (byte) 0x2B;
				int initSpeed = 0;
				if (sl == sr) {
					turn = (byte) 0x00;
					if (sl > 0) { // forward
						//speed = (byte) 0x2B;
						initSpeed = (byte) 0x01;
					} else { // backward
						speed = (byte) 0xD8;
						initSpeed = (byte) 0xFF;
					}
				}
				byte[] packet = new byte[] {
						0x7D,
						// UsbCommand.DRIVE,
						turn, new Integer(initSpeed).byteValue(),
						new Integer(initSpeed).byteValue(),
						new Integer(initSpeed).byteValue(),
						new Integer(initSpeed).byteValue() };
				for (int i = 0; i < 50; i++) {
					mipUsbDeviceUno.writeAsync(packet);
					try {
						Thread.sleep(10);
						int j = (initSpeed >0)? initSpeed++: initSpeed--;
						Log.d(TAG3, " initSpeed: "+initSpeed+" speed: "+speed);
						if (Math.abs(initSpeed) >= Math.abs(speed)) {
							break;
						}
						packet =new byte[] {
								0x7D,
								// UsbCommand.DRIVE,
								turn, new Integer(initSpeed).byteValue(),
								new Integer(initSpeed).byteValue(),
								new Integer(initSpeed).byteValue(),
								new Integer(initSpeed).byteValue() };
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				Log.d(TAG3, "ArduinoCtrlMM - send  cmd:" + packet[0] + " "
						+ packet[1] + " " + packet[2] + " " + packet[3] + " "
						+ packet[4] + " " + packet[5]);

			}
		}).start();

		return 0;
	}

	@Override
	public int driveQueue(int speedL, int speedR, int distanceL, int distanceR) {
		mipUsbDeviceUno.writeAsync(new byte[] { UsbCommand.START_BYTE,
				UsbCommand.DRIVE_QUEUE, new Integer(speedL).byteValue(),
				new Integer(speedR).byteValue(),
				new Integer(distanceL).byteValue(),
				new Integer(distanceR).byteValue(), UsbCommand.END_BYTE });

		return 0;
	}

	@Override
	public int upright() {
		mipUsbDeviceUno.writeAsync(new byte[] { UsbCommand.START_BYTE,
				UsbCommand.UPRIGHT, UsbCommand.END_BYTE });
		return 0;
	}

	@Override
	public int fallover() {
		mipUsbDeviceUno.writeAsync(new byte[] { UsbCommand.START_BYTE,
				UsbCommand.FALLOVER, UsbCommand.END_BYTE });
		return 0;
	}

	@Override
	public int stop() {

		

		new Thread(new Runnable() {

			@Override
			public void run() {
				byte[] packet = new byte[] { 0x7D, 0, 0, 0, 0,
						0 };
				for (int i = 0; i < 9; i++) {
					mipUsbDeviceUno.writeAsync(packet);
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				Log.d(TAG3, "ArduinoCtrlMM - send  cmd:" + packet[0] + " "
						+ packet[1] + " " + packet[2] + " " + packet[3] + " "
						+ packet[4] + " " + packet[5]);

			}
		}).start();
		return 0;
	}

	@Override
	public int reportEncoders(int refreshRate) {
		mipUsbDeviceUno.writeAsync(new byte[] { UsbCommand.START_BYTE,
				UsbCommand.REPORT_ENCODERS, UsbCommand.END_BYTE });
		return 0;
	}

	@Override
	public void listen() {

		/*
		 * Log.d(TAG3,"ArduinoCtrlMM listen ok ..." );
		 * UsbCommunication.addUsbListener(context);
		 */
	}

	@Override
	public void stopListen() {
		/*
		 * UsbCommunication.removeUsbListener(context);
		 */
	}

	@Override
	public void engageVisor() {
		mipUsbDeviceNano.writeAsync(new byte[] { UsbCommand.START_BYTE,
				UsbCommand.ENGAGE_VISOR, UsbCommand.END_BYTE });

	}

	@Override
	public void disengageVisor() {
		mipUsbDeviceNano.writeAsync(new byte[] { UsbCommand.START_BYTE,
				UsbCommand.DISENGAGE_VISOR, UsbCommand.END_BYTE });

	}

}
