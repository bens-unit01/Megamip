package com.megamip.view;

/*
 * 
 * This class uses "Sergey Tarasevich" universal image loader ( link: https://github.com/nostra13/Android-Universal-Image-Loader ) 
 * 
 * Some code of this class is inspired by the sample example  
 * 
 * */
import com.megamip.util.JettyServer;
import com.megamip.util.MipTimer;
import com.megamip.util.MipTimer.MipTimerListener;
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
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
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
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

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

	protected Handler mHandler;
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

	private MipTimer mLazyLoadTimer;
	private ProgressBar progressBar;
	private int progressBarStatus = 0;

	protected ImageLoader imageLoader;
	protected DisplayImageOptions options;

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

		// image loader initialisation -----------

		initImageLoader(this);
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error)
				.resetViewBeforeLoading(true).cacheOnDisk(false)
				.imageScaleType(ImageScaleType.EXACTLY)
				// .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.ALPHA_8).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		imageLoader = ImageLoader.getInstance();

		mHandler = new Handler();
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

		setListeners();

	}

	private void setListeners() {
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
				MainActivity.mScreenOrientation = mScreenOrientation;
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
				MainActivity.mScreenOrientation = mScreenOrientation;
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
					mHandler.post(new Runnable() {
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

				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {

						Log.d(TAG, " getScrollX 1: " + hsv.getScrollX());
						// int absDiff = Math.abs(xPrevious - x);
						// int swipeX = (absDiff / 10) + (absDiff % 10);
						int dir = direction;
						// if(mScreenOrientation ==
						// ScreenOrientation.POSITION_0) dir = - direction;
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
		int rotation = (MainActivity.mScreenOrientation == ScreenOrientation.POSITION_0) ? 0
				: 180;
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
		// float d = getResources().getDisplayMetrics().density;
		// Log.d(TAG, "density : " + d);
		final int imageWidth = displayMetrics.widthPixels - 80;
		final int imageHeight = displayMetrics.heightPixels;

		// we add the first item to provide a margin for the first element
		ImageView firstItem = new ImageView(this);
		firstItem.setBackgroundResource(R.drawable.shadow);
		firstItem.setImageResource(R.raw.empty);
		LinearLayout.LayoutParams flp = new LinearLayout.LayoutParams(23,
				imageHeight);
		flp.setMargins(0, 0, 15, 0); // lp.setMargins(left, top, right,
										// bottom);

		firstItem.setLayoutParams(flp);
		mCarouselContainer.addView(firstItem);

		ImageView imageItem;
		// int i = 1;
		TypedArray resourcesTypedArray = getResources().obtainTypedArray(
				arrayId);

		int spinnerPositionX = imageWidth / 2 - 50;
		int spinnerPositionY = imageHeight / 2 - 50;
		LayoutParams lpImage = new LinearLayout.LayoutParams(imageWidth,
				imageHeight);
		// RelativeLayout.LayoutParams imageContainer = new
		// RelativeLayout.LayoutParams(
		// spinnerPositionX, spinnerPositionY);
		RelativeLayout.LayoutParams spinnerContainer = new RelativeLayout.LayoutParams(
				imageWidth, imageHeight);
		RelativeLayout.LayoutParams spinnerSize = new RelativeLayout.LayoutParams(
				80, 80);

		Log.d(TAG, "widht: " + imageWidth + " height: " + imageHeight);

		for (int i = 0; i < resourcesTypedArray.length(); ++i) {
			// Create new ImageView
			imageItem = new ImageView(this);

			// Set the shadow background
			imageItem.setBackgroundResource(R.drawable.shadow);
			FrameLayout fl = new FrameLayout(this);
			imageItem.setLayoutParams(lpImage);
			fl.addView(imageItem);
			RelativeLayout rl = new RelativeLayout(this);

			ProgressBar spinner = new ProgressBar(this);
			spinnerSize.addRule(RelativeLayout.CENTER_IN_PARENT);
			spinner.setLayoutParams(spinnerSize);
			rl.addView(spinner);
			fl.addView(rl, spinnerContainer);
			mCarouselContainer.addView(fl);

			loadImage(imageItem, i, resourcesTypedArray, spinner, options);
		}

		// we add the last item
		ImageView lastItem = new ImageView(mActivity);
		lastItem.setBackgroundResource(R.drawable.shadow);
		lastItem.setImageResource(R.raw.empty);
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(30,
				imageHeight);
		llp.setMargins(0, 15, 15, 0);
		lastItem.setLayoutParams(llp);
		mCarouselContainer.addView(lastItem);

	}

	private static void initImageLoader(Context context) {

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.MAX_PRIORITY)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				// 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	private void loadImage(ImageView imageView, int resourceIndex,
			TypedArray resourcesTypedArray, ProgressBar loading,
			DisplayImageOptions options) {

		final ProgressBar spinner = loading;

		String url = resourcesTypedArray.getString(resourceIndex);
		imageLoader.displayImage(url, imageView, options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						spinner.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						String message = null;
						switch (failReason.getType()) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
						}

						spinner.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						spinner.setVisibility(View.GONE);
					}
				});
	}

	public void setScreenOrientation(final int rotation) {

		mHandler.post(new Runnable() {

			@Override
			public void run() {

				carouselGui.setRotation(rotation);

				if (rotation == 0) {
					carouselGui.setScaleX(-1);
					carouselGui.setScaleY(1);
				} else {
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
		Log.d(TAG, "------------------ x: " + lastPositionX + " y: "
				+ lastPositionY);
		final int swipeDistance = (xInit - lastPositionX) / SCALE_FACTOR;

		int diffY = lastPositionY - yInit;

		mHandler.post(new Runnable() {

			@Override
			public void run() {
				btnBack.setPressed(false);
				if ((lastPositionX > 3800 && lastPositionY > 3800)
						|| (lastPositionX >= 3661 && lastPositionY <= 434)) {
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
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {

					int minDistance = hsv.getWidth() / 2;
					int ds = hsv.getWidth() - 65;
					// if(mScreenOrientation == ScreenOrientation.POSITION_0) ds
					// = -ds;

					if ((swipeDistance > 0) // right to left swipe
							&& (Math.abs(swipeDistance) > minDistance)) {

						// hsv.smoothScrollTo(
						// (int) hsv.getScrollX() + ds,
						// hsv.getScrollY());
						hsv.smoothScrollTo(scrollInit + ds, hsv.getScrollY());
						index++;
					} else { // left to right swipe

						if ((swipeDistance <= 0)
								&& (Math.abs(swipeDistance) > minDistance)) {
							// hsv.smoothScrollTo(
							// (int) hsv.getScrollX() - ds,
							// hsv.getScrollY());
							hsv.smoothScrollTo(scrollInit - ds,
									hsv.getScrollY());
							index--;
						} else {
							hsv.smoothScrollTo(scrollInit, hsv.getScrollY());
						}

					}

					Log.d(TAG,
							"swipeDistance: " + swipeDistance
									+ " minDistance: " + minDistance
									+ " scrollInit: " + scrollInit + " ds: "
									+ ds + " getScrollX: " + hsv.getScrollX());

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
