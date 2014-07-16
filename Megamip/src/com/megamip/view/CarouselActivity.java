package com.megamip.view;

import com.megamip.util.JettyServer;
import com.megamip.view.DroidGap.ScreenOrientation;
import com.megamip.view.MxtTouch.TouchDownListener;
import com.megamip.view.MxtTouch.TouchEvent;
import com.megamip.view.MxtTouch.TouchSwipeDownListener;
import com.megamip.view.MxtTouch.TouchSwipeListener;
import com.megamip.view.MxtTouch.TouchSwipeUpListener;
import com.megamip.view.MxtTouch.TouchUpListener;
import com.megamip.voice.MainActivity;
import com.megamip.voice.MipCommand;
import com.megamip.voice.MipReceiver;
import com.megamip.voice.MipUsbDevice;
import com.megamip.voice.MipCommand.MoveProjectorTo;
import com.megamip.voice.MipUsbDevice.DeviceType;
import com.megamip.voice.MipUsbDevice.UsbListener;
import com.megamip.voice.R;
import com.megamip.voice.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CarouselActivity extends Activity implements TouchUpListener {

	private static final float INITIAL_ITEMS_COUNT = 1.0F;
	private static final String TAG = CarouselActivity.class.getName();
	private static final int MIN_SWIPE_DISTANCE = 250;

	/**
	 * Carousel container layout
	 */
	protected LinearLayout mCarouselContainer;
	protected HorizontalScrollView hsv;
	private MipUsbDevice mipUsbDeviceMicro;

	protected TouchDownListener mTouchDownListener;
	protected TouchUpListener mTouchUpListener;
	protected TouchSwipeListener mTouchSwipeListener;
	protected TouchSwipeUpListener mTouchSwipeUpListener;
	protected TouchSwipeDownListener mTouchSwipeDownListener;

	protected Handler handler;
	private Activity mActivity;

	protected int xPrevious = 0, xInit = 0, yInit = 0, scrollInit = 0,
			index = 0;
	public final int STEP = 20, SCALE_FACTOR = 3;
	protected ImageButton btnBack;
	private MxtTouch mMxtTouch;
	private TouchEvent mTouchEvent;
	private MipReceiver receiver;
	private MoveProjectorTo mMoveProjectorTo;
	protected ScreenOrientation mScreenOrientation;
	private Boolean mUpDownSwipeFlag = true;
    private CarouselGui carouselGui;
    private SoundPool soundPool;
    private int soundId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		//
		// // Set title
		// this.setTitle(R.string.KPetShop);

		// Set content layout
		carouselGui = new CarouselGui(this);
		setContentView(carouselGui);
		handler = new Handler();
		Intent i = getIntent();
		int rotation = i.getIntExtra("rotation", 0);
		mScreenOrientation = (rotation == 0) ? ScreenOrientation.POSITION_0
				: ScreenOrientation.POSITION_180;
		setScreenOrientation(rotation);

		mActivity = this;
		mMxtTouch = new MxtTouch();
		mTouchEvent = mMxtTouch.new TouchEvent(this, new Point(0, 0));

		receiver = new MipReceiver(this);
		mMoveProjectorTo = (new MipCommand()).new MoveProjectorTo(receiver);

		// sound initialization 
				soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
				soundId = soundPool.load(this, R.raw.button_31, 1);
				
		// Get reference to carousel container
		mCarouselContainer = (LinearLayout) findViewById(R.id.carousel);
		hsv = (HorizontalScrollView) findViewById(R.id.horizontal_carousel);

		mTouchUpListener = this;

		btnBack = (ImageButton) findViewById(R.id.btnBack);

		btnBack.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				Log.d(TAG, "onTouch ...");
				
				MainActivity.mScreenOrientation = mScreenOrientation;
				mActivity.finish();
				return false;
			}
		});

		// swipe up/down handlers
		mTouchSwipeUpListener = new TouchSwipeUpListener() {

			@Override
			public void onNotify() {

				Log.d(TAG, "swipe up ...");

				mScreenOrientation = ScreenOrientation.POSITION_180;
				int position = (mUpDownSwipeFlag) ? 1 : 2;
				mUpDownSwipeFlag = !mUpDownSwipeFlag;

				// playSound();
				// invoker.launch(mMoveProjectorTo);
				receiver.moveProjectorTo(position);
				setScreenOrientation(180);

			}
		};

		mTouchSwipeDownListener = new TouchSwipeDownListener() {

			@Override
			public void onNotify() {
				Log.d(TAG, "swipe down ...");

				mUpDownSwipeFlag = true;
				mScreenOrientation = ScreenOrientation.POSITION_0;
				receiver.moveProjectorTo(3);
				// playSound();

				setScreenOrientation(0);

			}
		};

		// ----------

		mTouchDownListener = new TouchDownListener() {

			@Override
			public void onNotify(TouchEvent e) {

				// Log.d(TAG, "down x: " + x);

				xPrevious = e.getPosition().x;
				xInit = xPrevious;
				yInit = e.getPosition().y;
				scrollInit = hsv.getScrollX();

				if (xInit > 3800 && yInit > 3800) {
					handler.post(new Runnable() {
						public void run() {

							btnBack.setPressed(true);
							playSound();
							

						}
					});

					Log.d(TAG, "xInit > 3800");

				}
			}
		};

		mTouchSwipeListener = new TouchSwipeListener() {

			@Override
			public void onNotify(TouchEvent e) {
				int xCurrent = e.getPosition().x;
				final int direction = xPrevious - xCurrent;
				xPrevious = xCurrent;

				// Log.d(TAG, "swipe x: " + x);

				handler.postDelayed(new Runnable() {

					@Override
					public void run() {

						Log.d(TAG, " getScrollX 1: " + hsv.getScrollX());
						// int absDiff = Math.abs(xPrevious - x);
						// int swipeX = (absDiff / 10) + (absDiff % 10);
						int dir = direction;
//						if(mScreenOrientation == ScreenOrientation.POSITION_0) dir = - direction;
						if (dir > 0) {
							hsv.smoothScrollTo((int) hsv.getScrollX() + STEP,
									(int) hsv.getScrollY());
						} else {
							hsv.smoothScrollTo((int) hsv.getScrollX() - STEP,
									(int) hsv.getScrollY());
						}

						Log.d(TAG, " direction " + direction + " getScrollX: "
								+ hsv.getScrollX());
					}
				}, 30L);

			}
		};



	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		int rotation = (MainActivity.mScreenOrientation == ScreenOrientation.POSITION_0)? 0 : 180 ;
		setScreenOrientation(rotation);
		mipUsbDeviceMicro = MipUsbDevice.getInstance(this, DeviceType.MICRO);

		mipUsbDeviceMicro.addUsbListener(new UsbListener() {

			@Override
			public void onNotify(MipUsbDevice.UsbEvent e) {

				byte[] data = new byte[10];
						data = e.getData();
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

				if (data[1] == 0x41) {
					// Log.d(TAG, " touch swipe ex: " + x + " y: " + y);
					mTouchSwipeListener.onNotify(mTouchEvent);
				}

				if (data[1] == 0x42) {
					// Log.d(TAG, " touch up ex: " + x + " y: " + y);
					mTouchUpListener.onNotify(mTouchEvent);
				}

			}
		});
		
		
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Compute the width of a carousel item based on the screen width and
		// number of initial items.

	}

	protected void onPostCreate(int arrayId) {

		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		final int imageWidth = displayMetrics.widthPixels;
		final int imageHeight = displayMetrics.heightPixels;
		float density = getResources().getDisplayMetrics().density;
		Log.d(TAG, " density: " + density + " imageWidth: " + imageWidth);
		// Get the array of puppy resources

		final TypedArray puppyResourcesTypedArray = getResources()
				.obtainTypedArray(arrayId);

         // we add the first item 
		ImageView firstItem = new  ImageView(this);
		firstItem.setBackgroundResource(R.drawable.shadow);
		firstItem.setImageResource(R.raw.empty);
		LinearLayout.LayoutParams flp = new LinearLayout.LayoutParams(
				50, imageHeight);
		flp.setMargins(0, 0, 15, 0); // lp.setMargins(left, top, right,
									// bottom);

		firstItem.setLayoutParams(flp);
		mCarouselContainer.addView(firstItem);
		
		// Populate the carousel with items
		ImageView imageItem;
		for (int i = 0; i < puppyResourcesTypedArray.length(); ++i) {
			// Create new ImageView
			imageItem = new ImageView(this);

			// Set the shadow background
			imageItem.setBackgroundResource(R.drawable.shadow);

			// Set the image view resource
			imageItem.setImageResource(puppyResourcesTypedArray.getResourceId(
					i, -1));

			// Set the size of the image view to the previously computed value
			// imageItem.setLayoutParams(new
			// LinearLayout.LayoutParams(imageWidth,
			// imageHeight));
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					imageWidth, imageHeight);
			lp.setMargins(0, 0, 15, 0); // lp.setMargins(left, top, right,
										// bottom);

			imageItem.setLayoutParams(lp);
			// / Add image view to the carousel container
			mCarouselContainer.addView(imageItem);

		}

	}

	public void setScreenOrientation(final int rotation) {
		
	

			handler.post(new Runnable() {

				@Override
				public void run() {

					carouselGui.setRotation(rotation);
					
					if(rotation == 0){
						carouselGui.setScaleX(-1);
						carouselGui.setScaleY(1);
					 }else{
						 carouselGui.setScaleX(1);
						 carouselGui.setScaleY(1);
					 }

				}
			});

  }


	// mTouchUp notify method
	@Override
	public void onNotify(TouchEvent e) {

		final int lastPositionX = e.getPosition().x;
		final int lastPositionY = e.getPosition().y;

		final int swipeDistance = (xInit - lastPositionX) / SCALE_FACTOR;

		int diffY = lastPositionY - yInit;

		handler.post(new Runnable() {

			@Override
			public void run() {
				btnBack.setPressed(false);
				if (lastPositionX > 3800 && lastPositionY > 3800) {
					btnBack.dispatchTouchEvent(MotionEvent.obtain(0, 0,
							MotionEvent.ACTION_UP, 0, 0, 0, 0, 0, 0, 0, 0, 0));
				}

			}
		});

		// up/down swipe
		if (Math.abs(diffY) > MIN_SWIPE_DISTANCE
				&& Math.abs(swipeDistance) < MIN_SWIPE_DISTANCE) {
			if (diffY > 0) {
				mTouchSwipeDownListener.onNotify();
			} else {
				mTouchSwipeUpListener.onNotify();
			}
		} else {

			// left/right swipe
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {

					int minDistance = hsv.getWidth() / 2;
					int ds = hsv.getWidth() - 20;
//                    if(mScreenOrientation == ScreenOrientation.POSITION_0) ds = -ds;
					
					if ((swipeDistance > 0) // right to left swipe
							&& (Math.abs(swipeDistance) > minDistance)) {

//						hsv.smoothScrollTo(
//								(int) hsv.getScrollX() + ds,
//								hsv.getScrollY());
						hsv.smoothScrollTo(
								scrollInit+ ds,
								hsv.getScrollY());
						index++;
					} else { // left to right swipe

						if ((swipeDistance <= 0)
								&& (Math.abs(swipeDistance) > minDistance)) {
//							hsv.smoothScrollTo(
//									(int) hsv.getScrollX() - ds,
//									hsv.getScrollY());
							hsv.smoothScrollTo(
									scrollInit - ds,
									hsv.getScrollY());
							index--;
						} else {
							hsv.smoothScrollTo(scrollInit, hsv.getScrollY());
						}

					}

					Log.d(TAG, "swipeDistance: " + swipeDistance
							+ " minDistance: " + minDistance + " scrollInit: "
							+ scrollInit + " ds: "+ ds  + " getScrollX: "+  hsv.getScrollX());

				}

			}, 100L);

		}

	}
	
	
	private void playSound() {

		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		float actualVolume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = (float) audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = actualVolume / maxVolume;
		
		soundPool.play(soundId, volume, volume, 1, 0, 1f);

	}

}
