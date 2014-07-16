package com.megamip.util;

import com.megamip.view.MxtTouch;
import com.megamip.view.MxtTouch.TouchDownListener;
import com.megamip.view.MxtTouch.TouchEvent;
import com.megamip.view.MxtTouch.TouchUpListener;
import com.megamip.voice.MipUsbDevice;
import com.megamip.voice.R;
import com.megamip.voice.MipUsbDevice.DeviceType;
import com.megamip.voice.MipUsbDevice.UsbListener;
import com.megamip.voice.R.id;
import com.megamip.voice.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

public class LocalVideoPlayer extends Activity {

	public final String TAG = this.getClass().getName();
	protected ImageButton btnBack;
	private Activity mActivity;
	private MipUsbDevice mipUsbDeviceMicro;
	private TouchEvent mTouchEvent;
	private TouchDownListener mTouchDownListener;
	private TouchUpListener mTouchUpListener;

	private int xPrevious = 0, xInit = 0, yInit = 0;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_video_player);

		setListeners();
		handler = new Handler();
		Intent intent = getIntent();
		String videoId = intent.getStringExtra("videoId");
		int orientation = intent.getIntExtra("orientation", 0);
		videoId += (orientation == 0) ? ".mp4" : "-flp.mp4";

		VideoView video = (VideoView) findViewById(R.id.videoView1);
		MediaController mediaController = new MediaController(this);
		mediaController.setAnchorView(video);
		video.setMediaController(mediaController);

		video.setKeepScreenOn(true);
		video.setVideoPath("/data/user/video/" + videoId);
		video.start();
		video.requestFocus();

	}

	private void setListeners() {

		mTouchEvent = (new MxtTouch()).new TouchEvent(this, new Point(0, 0));
		mActivity = this;
		btnBack = (ImageButton) findViewById(R.id.btnBackLVP);
		btnBack.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				Log.d(TAG, "onTouch ...");
				mActivity.finish();
				return false;
			}
		});

		mipUsbDeviceMicro = MipUsbDevice.getInstance(this, DeviceType.MICRO);

		mipUsbDeviceMicro.addUsbListener(new UsbListener() {

			@Override
			public void onNotify(MipUsbDevice.UsbEvent e) {

				byte[] data = e.getData();
				// extracting the touch coordinates

				int arg1 = (int) data[3] & 0xFF; // convert to unsigned byte
				int arg2 = (int) data[4] & 0xFF;
				int arg3 = (int) data[5] & 0xFF;

				// decoding 3 bytes data to x,y coordinates
				int x = ((arg1 << 4) | (arg3 >> 4) & 0xf);
				int y = (arg2 << 4) | ((arg3 & 0xf));

				mTouchEvent.getPosition().x = x;
				mTouchEvent.getPosition().y = y;

				if (data[1] == 0x40) {
					// Log.d(TAG, " touch down x: " + x + " y: " + y);
					mTouchDownListener.onNotify(mTouchEvent);
				}

				if (data[1] == 0x42) {
					// Log.d(TAG, " touch up ex: " + x + " y: " + y);
					mTouchUpListener.onNotify(mTouchEvent);
				}

				Log.d(TAG, " touch down x: " + x + " y: " + y);

			}
		});

		mTouchDownListener = new TouchDownListener() {

			@Override
			public void onNotify(TouchEvent e) {

				// Log.d(TAG, "down x: " + x);

				xPrevious = e.getPosition().x;
				xInit = xPrevious;
				yInit = e.getPosition().y;

				if (xInit > 3800 && yInit > 3800) {
					handler.post(new Runnable() {
						public void run() {

							btnBack.setPressed(true);
							// btnBack.dispatchTouchEvent(MotionEvent.obtain(0,
							// 0,
							// MotionEvent.ACTION_DOWN, 0, 0, 0, 0, 0, 0,
							// 0, 0, 0));

						}
					});

					Log.d(TAG, "xInit > 3800");

				}
			}
		};

		mTouchUpListener = new TouchUpListener() {

			@Override
			public void onNotify(TouchEvent e) {

				final int lastPositionX = e.getPosition().x;
				final int lastPositionY = e.getPosition().y;

				int diffY = lastPositionY - yInit;

				handler.post(new Runnable() {

					@Override
					public void run() {
						btnBack.setPressed(false);
						if (lastPositionX > 3800 && lastPositionY > 3800) {
							btnBack.dispatchTouchEvent(MotionEvent.obtain(0, 0,
									MotionEvent.ACTION_UP, 0, 0, 0, 0, 0, 0, 0,
									0, 0));
						}

					}
				});
			}
		};

	}
}
