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
		final int time = distanceL;

		new Thread(new Runnable() {

			@Override
			public void run() {

				byte[] packet = new byte[] { UsbCommand.START_BYTE,
						UsbCommand.DRIVE, new Integer(sl).byteValue(),
						new Integer(sr).byteValue(),
						new Integer(time).byteValue(),
						new Integer(time).byteValue(), UsbCommand.END_BYTE };
				mipUsbDeviceUno.writeAsync(packet);

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

		/*new Thread(new Runnable() {

			@Override
			public void run() {
				byte[] packet = new byte[] { 0x7D, 0, 0, 0, 0, 0 };
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
		
		*/
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

	@Override
	public void moveProjectorTo(Position pos) {

		int position;
		switch (pos) {
		case PROJECTOR_POSITION_WALL:
			position = 0xA0;
			break;
		case PROJECTOR_POSITION_CEILING:
			position = 0x5A;
			break;
		case PROJECTOR_POSITION_SCREEN:
			position = 0x31;
			break;

		default:
			position = 0;
			break;
		}

		byte[] packet = new byte[] { UsbCommand.START_BYTE,
				UsbCommand.ENGAGE_VISOR, new Integer(position).byteValue(),
				UsbCommand.END_BYTE };
		mipUsbDeviceUno.writeAsync(packet);

	}

}
