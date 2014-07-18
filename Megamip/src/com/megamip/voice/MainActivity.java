package com.megamip.voice;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.megamip.control.ArduinoCtrlMM;
import com.megamip.telepresence.MegamipLSClient;
import com.megamip.telepresence.MegamipLSClient.LsServerEvent;
import com.megamip.util.JettyServer;
import com.megamip.util.JettyServer.JettyListener;
import com.megamip.util.JettyServer.ServerEvent;
import com.megamip.util.LocalVideoPlayer;
import com.megamip.util.MipDeviceManager;
import com.megamip.util.MipTimer;
import com.megamip.util.MipUtils;
import com.megamip.util.MipWemoDevice;
import com.megamip.view.CarouselActivity;
import com.megamip.view.CarouselPhoto;
import com.megamip.view.CarouselVideo;
import com.megamip.view.DroidGap;
import com.megamip.view.MxtTouch;
import com.megamip.view.DroidGap.ScreenOrientation;
import com.megamip.view.MxtTouch.TouchDownListener;
import com.megamip.view.MxtTouch.TouchEvent;
import com.megamip.view.MxtTouch.TouchSwipeDownListener;
import com.megamip.view.MxtTouch.TouchSwipeUpListener;
import com.megamip.view.MxtTouch.TouchUpListener;
import com.megamip.view.NotificationsActivity;
import com.megamip.view.ProgressTask;
import com.megamip.view.ProgressTask.Task;
import com.megamip.view.SpeakNow;
import com.megamip.voice.MipUsbDevice.DeviceType;
import com.megamip.voice.MipUsbDevice.UsbEvent;
import com.megamip.voice.MipUsbDevice.UsbListener;
import com.megamip.telepresence.MegamipLSClient.MegamipLSClientListener;

public class MainActivity extends DroidGap {

	// ---
	public static final int USER_MOBILE = 0;
	public static final int USER_DESKTOP = 1;
	public static final int INACTIVITY_PERIOD = 25; // inactivity period in
													// seconds
	public static final int BLINK_PERIOD = 7;

	public static Context context; // reference vers l'activité MainActivity
	protected static final int RESULT_SPEECH = 1;
	public static final String TAG1 = "A1", TAG2 = "A2", TAG3 = "A3",
			TAG6 = "A6", TAG7 = "A7", TAG8 = "A8";

	public static final String PICTURE_MODE = "picture";
	public static final String VIDEO_MODE = "video";
	public static final String LOCAL_VIDEO_MODE = "local_video";
	public static final int LOCAL_VIDEO_REQUEST_CODE = 123;

	private String mMode = PICTURE_MODE;
	public static ScreenOrientation mScreenOrientation = ScreenOrientation.POSITION_0;
	private WebView webView;
	private Handler handler = null;
	private MipUsbDevice mipUsbDeviceNano;
	private MipUsbDevice mipUsbDeviceMicro;
	private MipUsbDevice mipUsbDeviceUno;
	// private static final String HTML_ROOT = "file:///mnt/sdcard/DCIM/gui/";
	private static final String HTML_ROOT = "file:///android_asset/www/";
	private Command mCommand;
	private Invoker invoker;
	private MipReceiver receiver;
	private SpeakNow mSpeakNowDlg;

	private SpeechRecognizer mSpeechRecognizer;
	private int compteur1 = 0;
	private JettyServer mJettyServer;
	private MegamipLSClient mMegamipLSClient;
	private MipTimer mTrgInactivity;
	private MipTimer mTrgBlink;
	private int mBlinkCounter = 1;
	private Object mLock = new Object();
	private MipDeviceManager mDeviceManager;
	private MediaPlayer mMediaPlayer;
	private MipUtils mMipUtils = new MipUtils();

	// Megamip commands
	private MipCommand.GuiDisplayNotifications2 mGuidDisplayNotifications2;
	private MipCommand.GuiDisplayNotifications mGuidDisplayNotifications;
	private MipCommand.VideoSearch mVideoSearch;
	private MipCommand.PictureSearch mPictureSearch;
	private MipCommand.GuiShow mGuiShow;
	private MipCommand.GuiNext mGuiNext;
	private MipCommand.GuiPrev mGuiPrev;
	private MipCommand.MoveProjectorTo mMoveProjectorTo;
	private MipCommand.GuiShowMessage mGuiShowMessage;
	private MipCommand.VisorMoveUp mVisorMoveUp;
	private MipCommand.VisorMoveDown mVisorMoveDown;
	private MipCommand.GuiHome mGuiHome;
	private MipCommand.GuiBack mGuiBack;
	private MipCommand.GuiBlink mGuiBlink;
	private MipCommand.GuiDisplayNotificationsPanel mGuiDisplayNotificationsPanel;
	private Boolean mUpDownSwipeFlag = true;
	private MipWemoDevice mMipWemoDevice = null;
	private String mLocation = "montreal";
	private ArduinoCtrlMM arduinoCtrl = null;
	// touch handling
	private TouchDownListener mTouchDownListener;
	private TouchUpListener mTouchUpListener;
	private TouchEvent mTouchEvent;
	private int yStart = 0;
	private TouchSwipeDownListener mTouchSwipeDownListener;
	private TouchSwipeUpListener mTouchSwipeUpListener;

	private SoundPool soundPool;
	private int soundId;

	private ImageButton btnMic;

	public enum State {
		STANDBY, NOTIFICATIONS_DISPLAY, SEARCH_DISPLAY, SEARCH_ERROR, SHOW, PROJECTING, STARTING, STARTED
	}

	private State mState = State.STARTING;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);
		context = this;
		handler = new Handler();

		// super.loadUrl("content://jsHybugger.org/file:///android_asset/www/test2.html");
		// setContentView(R.layout.activity_main);
		// super.loadUrl("http://www.accenture.com/ca-en/Pages/index.aspx");
		webView = super.getWebView();
		webView.getSettings().setJavaScriptEnabled(true);

		webView.addJavascriptInterface(this, "megaMipJSInterface");

		btnMic = (ImageButton) findViewById(R.id.btnMic);

		if (MipUsbDevice.isAllUSBConnected(context)) { // we confirm that the
														// Arduino Nano and Uno
														// are
			invoker = new Invoker(); // both connected
			receiver = new MipReceiver(handler, webView, this);
			// mc = new MipCommand();
			mJettyServer = new JettyServer();

			mMegamipLSClient = new MegamipLSClient();
			mSpeakNowDlg = new SpeakNow("Speak now !!", context);
			mDeviceManager = new MipDeviceManager(context);
			mTrgInactivity = new MipTimer(INACTIVITY_PERIOD, 1);

			mTrgBlink = new MipTimer(BLINK_PERIOD, 2);
			mMediaPlayer = MediaPlayer.create(this, R.raw.boing_comical_accent);
			mMipWemoDevice = new MipWemoDevice(this.getApplicationContext());
			Log.d(TAG2, "setting the listeners ... ");
			initCommands();
			setListeners();

			mState = State.STARTED;

		} else {

			Log.d(TAG2,
					"Not all usb devices are connected ... closing the app ");
			Toast warning = Toast.makeText(context,
					"Not all usb connections are plugged in !!",
					Toast.LENGTH_LONG);
			warning.show();
			this.finish();

		}

		// loadPage("index.html");
		// loadPage("test.html");
		Log.d(TAG2, "MainActivity onCreate()--- ");

	}

	public void loadPage(String in) {
		final String url = HTML_ROOT + in;

		Log.d(TAG3, " loadPage url = " + url);
		loadURL(url);

	}

	private void loadURL(final String in) {

		handler.post(new Runnable() {
			public void run() {
				webView.loadUrl(in);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void voiceHandler(VoiceInput command) {

		String[] args = command.getArgs();
		String action = command.getAction();

		String keywords = "";
		String apiSelect = "";

		for (int i = 0; i < args.length; i++) {
			keywords += args[i] + " ";
		}

		mState = State.SEARCH_DISPLAY;

		if (action.equals("video")) {

			// mCommand = mc.new VideoSearch(receiver, keywords);
			mVideoSearch.setKeywords(keywords);
			// mCommand = mVideoSearch;
			mMode = VIDEO_MODE;
			mTrgInactivity.resetTimer();
			showMessage("video search for: " + keywords);
			invoker.launch(mVideoSearch);
		}

		if (action.equals("picture") || action.equals("pictures")) {
			mMode = PICTURE_MODE;

			// mCommand = mc.new PictureSearch(receiver, keywords);
			mPictureSearch.setKeywords(keywords);
			// mCommand = mPictureSearch;
			mTrgInactivity.resetTimer();
			showMessage("picture search for: " + keywords);
			invoker.launch(mPictureSearch);
		}

		if (action.equals("light") || action.equals("lights")) {
			Log.d(TAG6, "light on/off .." + keywords);

			if (keywords.contains("on")) {
				Log.d(TAG6, "light on");
				showMessage("turning the lights on !! ");
				mMipWemoDevice.turnOn();

			} else {
				Log.d(TAG6, "light off");
				showMessage("turning the lights off !! ");
				mMipWemoDevice.turnOff();
			}
		}

		if (action.equals("show") || action.equals("display")) {
			Log.d(TAG6, "show .." + keywords);
			mState = State.NOTIFICATIONS_DISPLAY;
			mTrgInactivity.resetTimer();
			mMode = PICTURE_MODE;
			showMessage("displaying the notifications panel ");
			// invoker.launch(mGuiDisplayNotificationsPanel);
			final int rotationAngle = (mScreenOrientation == ScreenOrientation.POSITION_0) ? 0
					: 180;
			new ProgressTask(context, new Task() {

				@Override
				public void run() {

					Intent intent = new Intent(context,
							NotificationsActivity.class);
					intent.putExtra("rotation", rotationAngle);
					startActivity(intent);
					overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
				}
			}).execute();
		}

		if (action.equals("my")) {
			final int rotationAngle = (mScreenOrientation == ScreenOrientation.POSITION_0) ? 0
					: 180;
			if (keywords.contains("video") || keywords.contains("videos")) {

				mMode = LOCAL_VIDEO_MODE;
				mTrgInactivity.resetTimer();
				new ProgressTask(context, new Task() {

					@Override
					public void run() {

						Intent intent = new Intent(context, CarouselVideo.class);
						intent.putExtra("rotation", rotationAngle);
						startActivity(intent);
						overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
					}
				}).execute();

			} else {
				// loadURL("javascript:showMyPictures()");
				mMode = PICTURE_MODE;
				mTrgInactivity.resetTimer();

				new ProgressTask(context, new Task() {

					@Override
					public void run() {
						Intent intent = new Intent(context, CarouselPhoto.class);
						intent.putExtra("rotation", rotationAngle);
						startActivity(intent);
						overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);

					}
				}).execute();

			}

		}

		if (action.equals("personal")) {

			Log.d(TAG6, "personal assistant ...");
			try {
				Intent mediaplayerIntent = Intent.parseUri("godog://",
						Intent.URI_INTENT_SCHEME);
				// mediaplayerIntent.addCategory(Intent.CATEGORY_BROWSABLE);
				startActivityForResult(mediaplayerIntent, 55);
				overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);

				// } catch (URISyntaxException e) {
			} catch (Exception e) {
				Log.d(TAG2, "block catch - onLaunchVideo ...");
				e.printStackTrace();
			}
		}

		Log.d(TAG6, "MainActivity#voiceHandler - mState: " + mState);
		// invoker.launch(mCommand);

	}

	private void movementHandler(MovementInput movementInput) {

		String action = movementInput.getAction();

		// Gestures handling : Next, Hold, Back

		// **** Next--------------------
		if (action.equals(MovementInput.NEXT_GESTURE)) {
			// if (action.equals("69")) { // keyboard input ...
			mMediaPlayer.start();
			Log.d(TAG6, "onNext --------------");

			onNext();

		}

		// **** Hold--------------------

		if (action.equals(MovementInput.HOLD_GESTURE)) {
			// if (action.equals("81")) {
			mMediaPlayer.start();
			Log.d(TAG6, "onHold --------------");
			onHold();
		}

		// **** Back --------------------
		if (action.equals(MovementInput.BACK_GESTURE)) {
			// if (action.equals("87")) {
			mMediaPlayer.start();
			Log.d(TAG6, "onBack --------------");
			onBack();

		}

		// touch-screen input handling : left_swipe, right_swipe, up_swipe,
		// down_swipe, simple_touch

		if (action.equals(MovementInput.LEFT_SWIPE)) {

			Log.d(TAG6, "left_swipe --------------");
			delayTriggers();
			playSound();
			invoker.launch(mGuiPrev);
		}

		if (action.equals(MovementInput.RIGHT_SWIPE)) {

			Log.d(TAG6, "right_swipe --------------");
			delayTriggers();
			playSound();
			invoker.launch(mGuiNext);
		}

		if (action.equals(MovementInput.UP_SWIPE)) {

			Log.d(TAG6, "up_swipe --------------");
			mScreenOrientation = ScreenOrientation.POSITION_180;
			String params = (mUpDownSwipeFlag) ? "1" : "2";
			mUpDownSwipeFlag = !mUpDownSwipeFlag;

			// adding the text position ( mirrored 1 or not 0 )
			params = params + JettyServer.SPLIT_CHAR + "0";

			mMoveProjectorTo.setParams(params);
			playSound();
			invoker.launch(mMoveProjectorTo);
			super.setScreenOrientation(ScreenOrientation.POSITION_180);
		}

		if (action.equals(MovementInput.DOWN_SWIPE)) {

			mUpDownSwipeFlag = true;
			Log.d(TAG6, "down_swipe --------------");
			mScreenOrientation = ScreenOrientation.POSITION_0;
			mMoveProjectorTo.setParams("3" + JettyServer.SPLIT_CHAR + "1"); // the
																			// text
																			// is
																			// mirrored
			playSound();
			invoker.launch(mMoveProjectorTo);
			super.setScreenOrientation(ScreenOrientation.POSITION_0);
		}

		if (action.equals(MovementInput.SIMPLE_TOUCH)) {

			playSound();
			handleSimpleTouch(movementInput);

			Log.d(TAG6, "simple_touch --------------");
		}

		// Log.d(TAG6, "action:"+action);
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

	private void showMessage(final String msg) {

		// runOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		//
		// String ouputMessage = msg;
		// if (mScreenOrientation == ScreenOrientation.POSITION_0) {
		// StringBuffer stringBuffer = new StringBuffer(msg);
		// ouputMessage = stringBuffer.reverse().toString();
		// }
		//
		// Toast warning = Toast.makeText(context, ouputMessage,
		// Toast.LENGTH_LONG);
		// warning.show();
		//
		// }
		// });
	}

	private void handleSimpleTouch(MovementInput movementInput) {
		String[] args = movementInput.getArgs();

		// calculating the push coordinates
		int arg1 = 0;
		int arg2 = 0;
		int arg3 = 0;

		try {
			arg1 = Integer.parseInt(args[0]);
			arg2 = Integer.parseInt(args[1]);
			arg3 = Integer.parseInt(args[2]);
		} catch (Exception e) {
			Log.d(TAG3, " bloc catch: ex: " + e.getMessage());
		}

		// decoding 3 bytes data to x,y coordinates
		int x = ((arg1 << 4) | (arg3 >> 4) & 0xf);
		int y = (arg2 << 4) | ((arg3 & 0xf));
		Log.d(TAG3, " x: " + x + " y: " + y);

		// mic button
		if (x >= 9 && y >= 3650 && x <= 506 && y <= 4095) {
			Log.d(TAG3, "handleSimpleTouch , speak-now ");
			delayTriggers();
			onSpeak();

		}

		// close button
		if (x >= 3489 && y >= 3773 && x <= 4030 && y <= 4095) {
			// if (x >= 3489) {

			Log.d(TAG3, "handleSimpleTouch , close ");
			// mState = State.STANDBY;
			// delayTriggers();
			// invoker.launch(mGuiHome);
			onBack();
		}

		// video play

		if (x >= 914 && y >= 1317 && x <= 2927 && y <= 2868) {
			mGuiShow.setmMode(mMode);
			// mCommand = mGuiShow;
			mState = State.SHOW;
			delayTriggers();
			invoker.setState(Invoker.State.ACTIVE);
			invoker.launch(mGuiShow);
		}

	}

	private void onNext() {

		switch (mState) {
		case STANDBY:
			mState = State.NOTIFICATIONS_DISPLAY;
			String notifications = mDeviceManager.getNotifications();
			// mCommand = mc.new GuiDisplayNotifications2(receiver,
			// notifications,
			// INACTIVITY_PERIOD);
			mGuidDisplayNotifications2.setNotifications(notifications);
			mGuidDisplayNotifications2.setPeriod(INACTIVITY_PERIOD);
			mCommand = mGuidDisplayNotifications2;
			delayTriggers();
			Log.d(TAG3,
					"MainActivity#mouvementHandler() - GuiDisplayNotifications ");
			invoker.launch(mCommand);
			break;
		case SEARCH_DISPLAY:
			// mCommand = mc.new GuiNext(receiver);
			mCommand = mGuiNext;
			Log.d(TAG3, "MainActivity#mouvementHandler() - GuiNext ");
			delayTriggers();
			invoker.launch(mCommand);
			break;
		default:
			break;
		}

	}

	private void onHold() {

		switch (mState) {
		case STANDBY:
			mState = State.NOTIFICATIONS_DISPLAY;
			String notifications = mDeviceManager.getNotifications();
			// mCommand = mc.new GuiDisplayNotifications2(receiver,
			// notifications,
			// INACTIVITY_PERIOD);
			mGuidDisplayNotifications2.setNotifications(notifications);
			mGuidDisplayNotifications2.setPeriod(INACTIVITY_PERIOD);
			mCommand = mGuidDisplayNotifications2;
			delayTriggers();
			Log.d(TAG3,
					"MainActivity#mouvementHandler() - GuiDisplayNotifications ");
			invoker.launch(mCommand);
			break;
		case NOTIFICATIONS_DISPLAY:
			// mCommand = mc.new Speak(receiver);
			onSpeak();
			Log.d(TAG3, "MainActivity#mouvementHandler() - Speak ");
			delayTriggers();
			// invoker.launch(mCommand);
			break;
		case SEARCH_DISPLAY:
			// mCommand = mc.new GuiShow(receiver, mMode);
			mGuiShow.setmMode(mMode);
			mCommand = mGuiShow;
			Log.d(TAG3, "MainActivity#mouvementHandler() - GuiShow ");
			mState = State.SHOW;
			delayTriggers();
			invoker.setState(Invoker.State.ACTIVE);
			invoker.launch(mCommand);
			break;

		case SHOW:
			// mCommand = mc.new VisorMoveUp(receiver);
			mCommand = mVisorMoveUp;
			Log.d(TAG3, "MainActivity#mouvementHandler() - VisorMoveUp ");
			mState = State.PROJECTING;
			delayTriggers();
			invoker.setState(Invoker.State.ACTIVE);
			invoker.launch(mCommand);
			break;

		default:
			break;
		}

	}

	private void onBack() {

		switch (mState) {
		case STANDBY:
			mState = State.NOTIFICATIONS_DISPLAY;
			String notifications = mDeviceManager.getNotifications();
			// mCommand = mc.new GuiDisplayNotifications2(receiver,
			// notifications,
			// INACTIVITY_PERIOD);
			mGuidDisplayNotifications2.setNotifications(notifications);
			mGuidDisplayNotifications2.setPeriod(INACTIVITY_PERIOD);
			mCommand = mGuidDisplayNotifications2;
			delayTriggers();
			Log.d(TAG3,
					"MainActivity#mouvementHandler() - GuiDisplayNotifications ");
			invoker.launch(mCommand);
			break;
		case SEARCH_DISPLAY:
		case SEARCH_ERROR:
		case NOTIFICATIONS_DISPLAY:
			mState = State.STANDBY;
			// mCommand = mc.new GuiHome(receiver);
			mCommand = mGuiHome;
			Log.d(TAG3, "MainActivity#mouvementHandler() - GuiHome ");
			delayTriggers();
			invoker.launch(mCommand);
			break;

		case SHOW:
			mState = State.SEARCH_DISPLAY;
			invoker.setState(Invoker.State.PROCESSING);
			if (mMode.equals(VIDEO_MODE)) {

				mMipUtils.closeApp("air.air.MipVideoPlayer", context);
				// finishActivity(1);
				Log.d(TAG6, "back event fired");
			}

			if (mMode.equals(PICTURE_MODE)) {
				// mCommand = mc.new GuiBack(receiver);
				mCommand = mGuiBack;
				invoker.launch(mCommand);
			}

			if (mMode.equals(LOCAL_VIDEO_MODE)) {

				finishActivity(LOCAL_VIDEO_REQUEST_CODE);
			}
			Log.d(TAG3, "MainActivity#mouvementHandler() - GuiBack - mState: "
					+ mState);

			break;

		case PROJECTING:
			mState = State.SHOW;
			// mCommand = mc.new VisorMoveDown(receiver);
			mCommand = mVisorMoveDown;
			delayTriggers();
			Log.d(TAG3, "MainActivity#mouvementHandler() - VisorMoveDown ");
			invoker.launch(mCommand);
			break;

		default:
			break;
		}

	}

	private void delayTriggers() {

		synchronized (mLock) {
			invoker.setState(Invoker.State.PROCESSING);
			mTrgInactivity.resetTimer();
		}

	}

	private void jettyHandler(String params) {

		String[] input = params.split(JettyServer.SPLIT_CHAR);

		int speed = MipReceiver.SPEED;
		int turnSpeed = speed - 3;
		int time = MipReceiver.TIME;
		// int turnTime = MipReceiver.TIME / 2;
		int turnTime = MipReceiver.TURN_TIME;
		mipUsbDeviceUno = MipUsbDevice.getInstance(context, DeviceType.UNO);

		// Log.d(TAG2, "jettHandler cmd: " + input[1]);

		if (input[1].equals("moveForward") && mipUsbDeviceUno.isUsbConnected()) {

			arduinoCtrl.drive(speed, speed, time, time);
		}

		if (input[1].equals("moveBackward") && mipUsbDeviceUno.isUsbConnected()) {

			arduinoCtrl.drive(-speed, -speed, time, time);
		}

		if (input[1].equals("moveLeft") && mipUsbDeviceUno.isUsbConnected()) {

			arduinoCtrl.drive(-turnSpeed, turnSpeed, turnTime, turnTime);
		}

		if (input[1].equals("moveRight") && mipUsbDeviceUno.isUsbConnected()) {

			arduinoCtrl.drive(turnSpeed, -turnSpeed, turnTime, turnTime);
		}

		if (input[1].equals("stop") && mipUsbDeviceUno.isUsbConnected()) {

			// mCommand = mc.new MipStop(receiver);
			// invoker.launch(mCommand);
			// Log.d(TAG2, "jettHandler triggered stop");
		}

		if (input[1].equals("toggleProjector")) {
			playSound();
			arduinoCtrl.projectorOnOff();
			Log.d(TAG2, "jettyHandler triggered projector power on/off ");
		}

		if (input[1].equals("moveProjectorTo")) {

			String rotationSettings = input[2];

			// adjusting the screen orientation : landscape or reversed
			// landscape

			if (input[2].equals("1") || input[2].equals("2")) {
				super.setScreenOrientation(ScreenOrientation.POSITION_180);
				rotationSettings += JettyServer.SPLIT_CHAR + "0";
			} else {
				super.setScreenOrientation(ScreenOrientation.POSITION_0);
				rotationSettings += JettyServer.SPLIT_CHAR + "1";
			}
			playSound();
			mMoveProjectorTo.setParams(rotationSettings);
			invoker.launch(mMoveProjectorTo);
			Log.d(TAG2, "jettHandler triggered moveProjectorTo params: "
					+ input[2]);
		}

		if (input[1].equals("changeLocation")) {

			mLocation = input[2];

			loadURL("javascript:changeLocation('" + mLocation + "')");
			// String host = prop.getProperty("host");
			Log.d(TAG2, "jettyHandler triggered changeLocation  city: "
					+ mLocation);
		}

	}

	private void pushServerHandler(String args) {

		String[] params = args.split(MegamipLSClient.CMD_SEPARATOR);
		int rotationAngle = (mScreenOrientation == ScreenOrientation.POSITION_0) ? 0
				: 180;

		if (MegamipLSClient.CMD_LAUNCH.equals(params[0])) {
			String p2pID = params[1];
			Log.d(TAG3,
					"pushServerHandler - launching Telepresence module - p2pID:"
							+ p2pID);

			Intent intent;
			try {
				intent = Intent.parseUri("telepresenceapp://" + p2pID + "//"
						+ rotationAngle, Intent.URI_INTENT_SCHEME);
				// intent.addCategory(Intent.CATEGORY_BROWSABLE);
				// intent.setComponent(null);
				// intent.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				startActivity(intent);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

		}

		Log.d(TAG3, " pushServerHandler args: " + params[0]);

	}

	private void initCommands() {
		MipCommand mc = new MipCommand();

		mGuidDisplayNotifications2 = mc.new GuiDisplayNotifications2(receiver);
		mGuidDisplayNotifications = mc.new GuiDisplayNotifications(receiver);
		mVideoSearch = mc.new VideoSearch(receiver);
		mPictureSearch = mc.new PictureSearch(receiver);

		mGuiShow = mc.new GuiShow(receiver);
		mGuiNext = mc.new GuiNext(receiver);
		mGuiPrev = mc.new GuiPrev(receiver);
		mMoveProjectorTo = mc.new MoveProjectorTo(receiver);
		mGuiShowMessage = mc.new GuiShowMessage(receiver);

		mVisorMoveUp = mc.new VisorMoveUp(receiver);
		mVisorMoveDown = mc.new VisorMoveDown(receiver);
		mGuiHome = mc.new GuiHome(receiver);
		mGuiBack = mc.new GuiBack(receiver);

		mGuiBlink = mc.new GuiBlink(receiver);
		mGuiDisplayNotificationsPanel = mc.new GuiDisplayNotificationsPanel(
				receiver);

		// initialisation of the projector

		mScreenOrientation = ScreenOrientation.POSITION_0;
		mMoveProjectorTo.setParams("3" + JettyServer.SPLIT_CHAR + "1"); // the
																		// text
																		// is
		// projector initialisation // mirrored
		invoker.launch(mMoveProjectorTo);
		super.setScreenOrientation(ScreenOrientation.POSITION_0);

		// settings initialisation

		loadURL("javascript:changeLocation(" + mLocation + ")");
		Log.d(TAG2, "current location: " + mLocation);

		// reference to usb connections to the arduino nano and uno
		arduinoCtrl = new ArduinoCtrlMM(this);
		mipUsbDeviceUno = MipUsbDevice.getInstance(context, DeviceType.UNO);

		// touch events init

		mTouchEvent = new MxtTouch().new TouchEvent(this, new Point(0, 0));

		// sound initialization
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundId = soundPool.load(this, R.raw.button_31, 1);

	}

	private void setListeners() {

		// Listener for the usb devices
		// the arduino Nano controls the gesture sensor and the 2 projector
		// servos ( focus and rotation )

		// mipUsbDeviceNano = MipUsbDevice.getInstance(context,
		// DeviceType.NANO);

		// mipUsbDeviceNano.addUsbListener(new UsbListener() {
		//
		// @Override
		// public void onNotify(UsbEvent e) {
		// byte[] data = e.getData();
		// movementHandler(new MovementInput(data));
		// Log.d(TAG7, "> mipUsbDeviceNano " + (new String(data)));
		// }
		//
		// });

		// the Sparkfun pro micro controls the touch-screen
		mipUsbDeviceMicro = MipUsbDevice.getInstance(context, DeviceType.MICRO);

		// touch listeners
		mTouchDownListener = new TouchDownListener() {

			@Override
			public void onNotify(TouchEvent e) {
				yStart = e.getPosition().y;
				playSound();

				int x = e.getPosition().x;
				int y = e.getPosition().y;
				// mic button
				if (x >= 9 && y >= 3650 && x <= 506 && y <= 4095) {

					handler.post(new Runnable() {
						public void run() {
							btnMic.setPressed(true);
						}
					});

				}

			}
		};

		mTouchUpListener = new TouchUpListener() {

			@Override
			public void onNotify(TouchEvent e) {

				handler.post(new Runnable() {

					@Override
					public void run() {

						btnMic.setPressed(false);
					}
				});

				int x = e.getPosition().x;
				int y = e.getPosition().y;
				Log.d(TAG3, "------------------ x: " + x + " y: " + y);
				// up/down swipe
				int diffY = y - yStart;

				if (Math.abs(diffY) > 300) {
					if (diffY > 0) {
						mTouchSwipeDownListener.onNotify();
					} else {
						mTouchSwipeUpListener.onNotify();
					}
				}

				// simple touch

				// mic button
				if ((x >= 9 && y >= 3650 && x <= 506 )
						|| (x >= 1646 && y >= 3911 && x <= 2662 )) {
					delayTriggers();

					onSpeak();

				}

			}
		};

		mTouchSwipeDownListener = new TouchSwipeDownListener() {

			@Override
			public void onNotify() {
				Log.d(TAG3, "swipe down ...");
				mUpDownSwipeFlag = true;

				mScreenOrientation = ScreenOrientation.POSITION_0;
				mMoveProjectorTo.setParams("3" + JettyServer.SPLIT_CHAR + "1"); // the
																				// text
																				// is
																				// mirrored
				playSound();
				invoker.launch(mMoveProjectorTo);
				MainActivity.super
						.setScreenOrientation(ScreenOrientation.POSITION_0);

			}
		};

		mTouchSwipeUpListener = new TouchSwipeUpListener() {

			@Override
			public void onNotify() {
				Log.d(TAG3, "swipe up ...");

				mScreenOrientation = ScreenOrientation.POSITION_180;
				String params = (mUpDownSwipeFlag) ? "1" : "2";
				mUpDownSwipeFlag = !mUpDownSwipeFlag;

				// adding the text position ( mirrored 1 or not 0 )
				params = params + JettyServer.SPLIT_CHAR + "0";

				mMoveProjectorTo.setParams(params);
				playSound();
				invoker.launch(mMoveProjectorTo);
				MainActivity.super
						.setScreenOrientation(ScreenOrientation.POSITION_180);

			}
		};

		// Listener for the mic device

		mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
		mSpeechRecognizer.setRecognitionListener(new SpeechListener());

		// listener for the jetty server

		mJettyServer.addJettyListener(new JettyListener() {

			@Override
			public void onNotify(ServerEvent e) {
				Log.d(TAG3, " onNotify fired - jettyListener ... ");

				delayTriggers();
				final String params = e.getParams();

				new Thread(new Runnable() {

					@Override
					public void run() {
						jettyHandler(params);

					}
				}).start();

				// Log.d(TAG3, " onNotify fired - jettyListener ... 1");
			}

		});

		// listener for the push server ( Lightstreamer server )

		mMegamipLSClient
				.addMegamipLSClientListener(new MegamipLSClientListener() {

					@Override
					public void onNotify(LsServerEvent e) {

						pushServerHandler(e.getParams());

					}

					@Override
					public void onError(LsServerEvent e) {
						int error = Integer.parseInt(e.getParams());
						Log.d(TAG2,
								"mMegamipLSClient onError - MainActivity ... error: "
										+ error);
						if (error == MegamipLSClient.ERROR
								|| error == MegamipLSClient.CONNECTION_ERROR
								|| error == MegamipLSClient.SERVER_ERROR) {

							// we retry to reconnect after 30 seconds
							new Thread(new Runnable() {
								public void run() {
									try {
										Thread.sleep(30000);
										mMegamipLSClient.onResume();
									} catch (InterruptedException e) {

										e.printStackTrace();
									}
								}
							}).start();
						}
					}
				});

		// triggers ---------------------------------------------------

		mTrgInactivity.addEventListener(new MipTimer.MipTimerListener() {

			@Override
			public void update() {

				if (invoker.getState() != Invoker.State.ACTIVE) {
					if (mState != State.STANDBY
							&& mState != State.NOTIFICATIONS_DISPLAY) {
						invoker.launch(mGuiHome);
					}
					mState = State.STANDBY;
					synchronized (mLock) {
						invoker.setState(Invoker.State.IDLE);
					}

				}

				Log.d(TAG1,
						"MainActivity - mTrgInactivity#update  -- action = "
								+ " invoker.state: " + invoker.getState()
								+ " mState: " + mState);

			}
		});

		mTrgBlink.addEventListener(new MipTimer.MipTimerListener() {

			@Override
			public void update() {
				if (Invoker.State.IDLE == invoker.getState()) {
					if ((mBlinkCounter % 3) != 0) {

						new Thread(new Runnable() {
							@Override
							public void run() {
								// invoker.launch(mc.new GuiBlink(receiver));
								invoker.launch(mGuiBlink);

							}
						}).start();

						mBlinkCounter++;
					} else {

						new Thread(new Runnable() {

							@Override
							public void run() {
								String notifications = mDeviceManager
										.getNotifications();
								mGuidDisplayNotifications
										.setNotifications(notifications);
								invoker.launch(mGuidDisplayNotifications);

							}
						}).start();
						mBlinkCounter++;

						if (mBlinkCounter > 1000) {
							mBlinkCounter = 1;
						}
					}
				}

			}
		});

	}

	// onSpeak is called through the javascript interface : megaMipJSInterface
	public void onSpeak() {

		handler.post(new Runnable() {
			public void run() {

				Intent intent = new Intent(
						RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
						RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
						"voice.recognition.test");
				intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

				mSpeechRecognizer.startListening(intent);
				Log.i("A2", "MainActivity -- onSpeak() ");

			}
		});
	}

	// inner class to implement the RecognitionListener interface

	class SpeechListener implements RecognitionListener {
		public void onReadyForSpeech(Bundle params) {

			mSpeakNowDlg.show();
			mSpeakNowDlg.showListening();

			Log.d(TAG2, "onReadyForSpeech");
		}

		public void onBeginningOfSpeech() {
			Log.d(TAG2, "onBeginningOfSpeech");
		}

		public void onRmsChanged(float rmsdB) {

			mSpeakNowDlg.updateVoiceMeter(rmsdB);
			// Log.d(TAG, "onRmsChanged");
		}

		public void onBufferReceived(byte[] buffer) {
			if (0 == compteur1) {
				Log.d(TAG2, "onBufferReceived");
				compteur1 = 1;
			}
		}

		public void onEndOfSpeech() {
			mSpeakNowDlg.dismiss();

			// mCommand = mc.new GuiHideMic(receiver); // we hide the mic
			// animation on the GUI
			// invoker.launch(mCommand);
			Log.d(TAG2, "onEndofSpeech");
		}

		public void onError(int error) {
			compteur1 = 0;
			mSpeakNowDlg.dismiss();
			// mCommand = mc.new GuiHideMic(receiver); // we hide the mic
			// animation on the GUI
			// invoker.launch(mCommand);
			Log.d(TAG2, "onError error no = " + error);
			// mText.setText("error " + error);

			String message = "";

			switch (error) {

			case SpeechRecognizer.ERROR_AUDIO:
				message = "Recording error, please try again ...";
				break;
			case SpeechRecognizer.ERROR_CLIENT:
				message = "Server issue, error no 05";
				break;
			case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
				message = "Server issue, error no 09";
				break;
			case SpeechRecognizer.ERROR_NETWORK:
				message = "Network issue, please try again ...";
				break;
			case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
				message = "Connection to the server timed out.";
				break;
			case SpeechRecognizer.ERROR_NO_MATCH:
				message = "Recognition problem : the system is not able to recognize this word";
				break;
			case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
				message = "Server is busy, please try again later ...";
				break;
			case SpeechRecognizer.ERROR_SERVER:
				message = "Server error";
				break;
			case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
				message = "No speech input";
				break;

			default:
				message = "Unknown error";
				break;

			}

			mState = State.SEARCH_ERROR;
			// mCommand = mc.new GuiShowMessage(receiver, message);
			showMessage(message);
			mGuiShowMessage.setMessage(message);
			mCommand = mGuiShowMessage;
			invoker.launch(mCommand);

		}

		public void onResults(Bundle results) {
			compteur1 = 0;
			String str = new String();

			Log.d(TAG2, "onResults " + results);
			ArrayList data = results
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			for (int i = 0; i < data.size(); i++) {
				Log.d("A9", "result " + data.get(i));
				str += data.get(i);
			}

			VoiceInput voiceCommand = new VoiceInput(str);
			Log.d("A9", "onResults -----vc= " + voiceCommand);
			voiceHandler(voiceCommand);

			// mText.setText("results: "+String.valueOf(data.size()));
			// refDialog.dismiss();

		}

		public void onPartialResults(Bundle partialResults) {
			Log.d(TAG2, "onPartialResults");
		}

		public void onEvent(int eventType, Bundle params) {
			Log.d(TAG2, "onEvent " + eventType);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mState != State.STARTING) {
			mMegamipLSClient.onResume();

			mTrgInactivity.resetTimer();
			mTrgBlink.resetTimer();

		}

		setScreenOrientation(mScreenOrientation);
		mipUsbDeviceMicro.addUsbListener(new UsbListener() {

			@Override
			public void onNotify(UsbEvent e) {

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

				//

				if (data[1] == 0x42) {
					// Log.d(TAG, " touch up ex: " + x + " y: " + y);
					mTouchUpListener.onNotify(mTouchEvent);
				}
			}

		});

		Log.d(TAG2, "MainActivity onResume()--- mState: " + mState);
	}

	@Override
	public void onPause() {

		super.onPause();
		if (mSpeakNowDlg.isShowing()) {
			mSpeakNowDlg.dismiss();
			Log.d(TAG3, "MainActivity onPause() block if");
		}

		mMegamipLSClient.onPause(); // we disconnect from the Lightstreamer
									// server
		mTrgInactivity.stopTimer();
		mTrgBlink.stopTimer();
		Log.d(TAG2, "MainActivity onPause()");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG2, "MainActivity onStop()...");
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		Log.d("A3", "onNewIntent ...");

	}

	public void getNotifications() {

		// the notifications are formated on a string
		// "new emails":"wifi percentage":"battery percentage"

		MipDeviceManager deviceManager = new MipDeviceManager(context);
		try {
			String notifications = deviceManager.getNotifications();
			mGuidDisplayNotifications.setNotifications(notifications);
			invoker.launch(mGuidDisplayNotifications);
			Log.d(TAG3,
					"MaintActivity - getNotifications - bloc try - notifications: "
							+ notifications);
		} catch (Exception e) {

			Log.d(TAG3, "getNewEmails - bloc catch e: " + e.getMessage());
		}

	}

	// ----------------- tests - ajout module clavier

	public void onKeyBoard(String keyCode) {

		Log.d(TAG3, "onKeyBoard -- " + keyCode);

		int key = Integer.parseInt(keyCode);
		byte[] input = { 0, 0, Byte.valueOf(keyCode) };
		movementHandler(new MovementInput(input));

	}

	public void onLaunchVideo(String url, int mode) { // mode == 0 for youtube
														// videos, mode == 1 for
														// local videos

		if (mode == 0) { // it's a youtube video
			String videoId = mMipUtils.getYoutubeVideoId(url);
			int rotationAngle = (mScreenOrientation == ScreenOrientation.POSITION_0) ? 0
					: 180;

			try {
				Intent mediaplayerIntent = Intent
						.parseUri("mipvideoplayerapp://" + videoId + "//"
								+ rotationAngle, Intent.URI_INTENT_SCHEME);
				mediaplayerIntent.addCategory(Intent.CATEGORY_BROWSABLE);
				startActivityForResult(mediaplayerIntent, 1);

			} catch (URISyntaxException e) {
				Log.d(TAG2, "block catch - onLaunchVideo ...");
				e.printStackTrace();
			}
			// mediaplayerIntent.putExtra("id", videoId);
			// mediaplayerIntent.putExtra("pos", position);

		} else { // it's a local video

			int orientation = (mScreenOrientation == ScreenOrientation.POSITION_0) ? 0
					: 180;
			Intent intent = new Intent(this, LocalVideoPlayer.class);
			intent.putExtra("videoId", url);
			intent.putExtra("orientation", orientation);
			startActivityForResult(intent, LOCAL_VIDEO_REQUEST_CODE);

		}

	}

}
