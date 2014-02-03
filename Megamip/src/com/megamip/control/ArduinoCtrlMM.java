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
		/*
		 * byte turn = 0x00; turn = (speedL > speedR) ? (byte) 0x5A : (byte)
		 * 0xA6;
		 * 
		 * if (speedL == speedR){ turn = (byte) 0x00; if (speedL > 0) { //
		 * forward speedL = (byte) 0x2B; } else { // backward speedL = (byte)
		 * 0xD8; } }
		 * 
		 * byte[] packet = new byte[] { UsbCommand.START_BYTE, //
		 * UsbCommand.DRIVE, turn, new Integer(sl).byteValue(), new
		 * Integer(sl).byteValue(), new Integer(sl).byteValue(), new
		 * Integer(sl).byteValue() }; mipUsbDeviceUno.writeAsync(packet);
		 * mipUsbDeviceUno.writeAsync(packet);
		 * mipUsbDeviceUno.writeAsync(packet);
		 * mipUsbDeviceUno.writeAsync(packet);
		 */

		new Thread(new Runnable() {

			@Override
			public void run() {

				byte turn = 0x00;
				turn = (sl > sr) ? (byte) 0x5A : (byte) 0xA6;
				int speed = 0;
				if (sl == sr) {
					turn = (byte) 0x00;
					if (sl > 0) { // forward
						speed = (byte) 0x2B;
					} else { // backward
						speed = (byte) 0xD8;
					}
				}
				byte[] packet = new byte[] {
						UsbCommand.START_BYTE,
						// UsbCommand.DRIVE,
						turn, new Integer(speed).byteValue(),
						new Integer(speed).byteValue(),
						new Integer(speed).byteValue(),
						new Integer(speed).byteValue() };
				for (int i = 0; i < 4; i++) {
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

		byte[] packet = new byte[] { UsbCommand.START_BYTE, 0, 0, 0, 0, 0 };
		mipUsbDeviceUno.writeAsync(packet);
		mipUsbDeviceUno.writeAsync(packet);
		mipUsbDeviceUno.writeAsync(packet);
		mipUsbDeviceUno.writeAsync(packet);

		new Thread(new Runnable() {

			@Override
			public void run() {
				byte[] packet = new byte[] { UsbCommand.START_BYTE, 0, 0, 0, 0,
						0 };
				for (int i = 0; i < 4; i++) {
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
