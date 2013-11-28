package com.megamip.voice;

import java.net.URISyntaxException;
import java.util.ArrayList;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Credentials;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.youtube.player.YouTubePlayer;
import com.megamip.telepresence.MegamipLSClient;
import com.megamip.telepresence.MegamipLSClient.LsServerEvent;
import com.megamip.util.DroidGap;
import com.megamip.util.JettyServer;
import com.megamip.util.JettyServer.JettyListener;
import com.megamip.util.JettyServer.ServerEvent;
import com.megamip.util.MipDeviceManager;
import com.megamip.util.MipTimer;
import com.megamip.view.MipVideoPlayer;
import com.megamip.view.SpeakNow;
import com.megamip.voice.MipUsbDevice.UsbEvent;
import com.megamip.voice.MipUsbDevice.UsbListener;
import com.megamip.telepresence.MegamipLSClient;
import com.megamip.telepresence.MegamipLSClient.LsServerEvent;
import com.megamip.telepresence.MegamipLSClient.MegamipLSClientListener;

public class MainActivity extends DroidGap {

	public static final int USER_MOBILE = 0;
	public static final int USER_DESKTOP = 1;
	public static final int INACTIVITY_PERIOD = 21;
	public static final int BLINK_PERIOD = 7;

	public static Context context; // reference vers l'activit� MainActivity
	protected static final int RESULT_SPEECH = 1;
	public static final String TAG1 = "A1", TAG2 = "A2", TAG3 = "A3";
	public static final String PICTURE_MODE = "picture";
	public static final String VIDEO_MODE = "video";
	private String mMode = PICTURE_MODE;
	private ImageButton btnSpeak;
	private TextView textView;
	private EditText editText1;
	private WebView webView;
	private String accountType = null;
	private String accountName = null;
	private Handler handler = null;
	private MipUsbDevice mipUsbDevice;
	// private static final String HTML_ROOT = "file:///mnt/sdcard/DCIM/gui/";
	private static final String HTML_ROOT = "file:///android_asset/www/";
	private Command mCommand;
	private Invoker invoker;
	private MipReceiver receiver;
	private MipCommand mc;
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

	public enum State {
		STANDBY, NOTIFICATIONS_DISPLAY, SEARCH_DISPLAY, SEARCH_ERROR, SHOW
	}

	private State mState = State.STANDBY;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// setContentView(R.layout.activity_main);

		context = this;
		handler = new Handler();

		// super.loadUrl("content://jsHybugger.org/file:///android_asset/www/test2.html");
		super.onCreate(savedInstanceState);
		// super.loadUrl("file:///android_asset/www/index.html");

		webView = super.appView;
		webView.addJavascriptInterface(this, "megaMipJSInterface");
		// Log.d(TAG1, " User agent: "
		// + webView.getSettings().getUserAgentString());
		// Log.d(TAG1, " getSettings: " + webView.getSettings().toString());
		// attach web view to debugging service
		// DebugServiceClient dbgClient =
		// DebugServiceClient.attachWebView(webView, this);

		invoker = new Invoker();
		receiver = new MipReceiver(handler, webView, this);
		mc = new MipCommand();

		mJettyServer = new JettyServer();
		mMegamipLSClient = new MegamipLSClient();

		mSpeakNowDlg = new SpeakNow("Speak now !!", context);

		mDeviceManager = new MipDeviceManager(context);
		mTrgInactivity = new MipTimer(INACTIVITY_PERIOD, 1);
		mTrgBlink = new MipTimer(BLINK_PERIOD, 2);
		setListeners();

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

		Log.d(TAG3, "WebViewActivity launch - call of callJsFunction ");

		// if(action.equals("picture"))
		// mCommand = mc.new GuiShow(receiver);
		mState = State.SEARCH_DISPLAY;

		if (action.equals("video")) {
			mCommand = mc.new VideoSearch(receiver, keywords);
			mMode = VIDEO_MODE;
			mTrgInactivity.resetTimer();
		} else {

			// (action.equals("picture"))
			mMode = PICTURE_MODE;
			mCommand = mc.new PictureSearch(receiver, keywords);
			mTrgInactivity.resetTimer();
		}

		Log.d(TAG1, "VoiceHandler -- mState: " + mState);
		invoker.launch(mCommand);

	}

	private void movementHandler(MovementInput movementInput) {

		String action = movementInput.getAction();

		// **** Next--------------------
		if (action.equals("69")) {
			onNext();

		}

		// **** Hold--------------------

		if (action.equals("81")) {
			onHold();
		}

		// **** Back --------------------
		if (action.equals("87")) {
			onBack();

		}

		Log.d(TAG1, "MainActivity - movementHandler -- action = " + action
				+ " invoker.state: " + invoker.getState() + " mState: "
				+ mState + " mMode: " + mMode);

	}

	private void onNext() {

		switch (mState) {
		case STANDBY:
			mState = State.NOTIFICATIONS_DISPLAY;
			String notifications = mDeviceManager.getNotifications();
			mCommand = mc.new GuiDisplayNotifications2(receiver, notifications, 13);
			delayTriggers();
			Log.d(TAG3,
					"MainActivity#mouvementHandler() - GuiDisplayNotifications ");
			invoker.launch(mCommand);
			break;
		case SEARCH_DISPLAY:
			mCommand = mc.new GuiNext(receiver);
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
			mCommand = mc.new GuiDisplayNotifications2(receiver, notifications, 13);
			delayTriggers();
			Log.d(TAG3,
					"MainActivity#mouvementHandler() - GuiDisplayNotifications ");
			invoker.launch(mCommand);
			break;
		case NOTIFICATIONS_DISPLAY:
			//mCommand = mc.new Speak(receiver);
			onSpeak();
			Log.d(TAG3, "MainActivity#mouvementHandler() - Speak ");
			delayTriggers();
			//invoker.launch(mCommand);
			break;
		case SEARCH_DISPLAY:
			mCommand = mc.new GuiShow(receiver, mMode);
			Log.d(TAG3, "MainActivity#mouvementHandler() - GuiShow ");
			mState = State.SHOW;
			delayTriggers();
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
			mCommand = mc.new GuiDisplayNotifications2(receiver, notifications, 13);
			delayTriggers();
			Log.d(TAG3,
					"MainActivity#mouvementHandler() - GuiDisplayNotifications ");
			invoker.launch(mCommand);
			break;
		case SEARCH_DISPLAY:
		case SEARCH_ERROR:
			mState = State.STANDBY;
			mCommand = mc.new GuiHome(receiver);
			Log.d(TAG3, "MainActivity#mouvementHandler() - GuiHome ");
			delayTriggers();
			invoker.launch(mCommand);
			break;

		case SHOW:
			mState = State.SEARCH_DISPLAY;
			mCommand = mc.new GuiBack(receiver);
			Log.d(TAG3, "MainActivity#mouvementHandler() - GuiBack ");
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

		Log.d(TAG2, "jettHandler cmd: " + input[1]);

		if (input[1].equals("moveForward")) {
			mCommand = mc.new MipMoveForward(receiver);
			invoker.launch(mCommand);
			Log.d(TAG2, "jettHandler triggered moveForward");
		}

		if (input[1].equals("moveBackward")) {
			mCommand = mc.new MipMoveBackward(receiver);
			invoker.launch(mCommand);
			Log.d(TAG2, "jettHandler triggered moveBackward");
		}

		if (input[1].equals("moveLeft")) {
			mCommand = mc.new MipMoveLeft(receiver);
			invoker.launch(mCommand);
			Log.d(TAG2, "jettHandler triggered moveLeft");
		}

		if (input[1].equals("moveRight")) {
			mCommand = mc.new MipMoveRight(receiver);
			invoker.launch(mCommand);
			Log.d(TAG2, "jettHandler triggered moveRight");
		}

	}

	private void pushServerHandler(String args) {

		String[] params = args.split(MegamipLSClient.CMD_SEPARATOR);

		if (MegamipLSClient.CMD_LAUNCH.equals(params[0])) {
			String p2pID = params[1];
			Log.d(TAG3,
					"pushServerHandler - launching Telepresence module - p2pID:"
							+ p2pID);

			Intent intent;
			try {
				intent = Intent.parseUri("telepresenceapp://" + p2pID,
						Intent.URI_INTENT_SCHEME);
				intent.addCategory(Intent.CATEGORY_BROWSABLE);
				intent.setComponent(null);
				intent.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				startActivity(intent);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void setListeners() {

		// Listener for the usb device

		// mipUsbDevice = MipUsbDevice.getInstance(context);
		//
		// mipUsbDevice.addUsbListener(new UsbListener() {
		//
		// @Override
		// public void onNotify(UsbEvent e) {
		// byte[] data = e.getData();
		// movementHandler(new MovementInput(data));
		//
		// }
		//
		// });

		// Listener for the mic device

		mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
		mSpeechRecognizer.setRecognitionListener(new SpeechListener());

		// listener for the jetty server

		mJettyServer.addJettyListener(new JettyListener() {

			@Override
			public void onNotify(ServerEvent e) {
				// TODO Auto-generated method stub
				Log.d(TAG3, " onNotify fired - jettyListener ... 0");
				jettyHandler(e.getParams());

				Log.d(TAG3, " onNotify fired - jettyListener ... 1");
			}

		});

		// listener for the push server ( Lightstreamer server )

		mMegamipLSClient
				.addMegamipLSClientListener(new MegamipLSClientListener() {

					@Override
					public void onNotify(LsServerEvent e) {

						pushServerHandler(e.getParams());

					}
				});

		// triggers

		mTrgInactivity.addEventListener(new MipTimer.MipTimerListener() {

			@Override
			public void update() {

				if (invoker.getState() != Invoker.State.ACTIVE) {

					invoker.launch(mc.new GuiHome(receiver));
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

						invoker.launch(mc.new GuiBlink(receiver));
						mBlinkCounter++;
					} else {

						String notifications = mDeviceManager
								.getNotifications();
						invoker.launch(mc.new GuiDisplayNotifications(receiver,
								notifications));
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
		// TODO Auto-generated method stub
		/*
		 * Intent intent = new Intent(
		 * RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		 * 
		 * intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
		 * 
		 * try { startActivityForResult(intent, RESULT_SPEECH);
		 * 
		 * } catch (ActivityNotFoundException a) { Toast t =
		 * Toast.makeText(getApplicationContext(),
		 * "Ops! Your device doesn't support Speech to Text",
		 * Toast.LENGTH_SHORT); t.show(); }
		 */

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

			/*
			 * WindowManager.LayoutParams lWindowParams = new
			 * WindowManager.LayoutParams();
			 * lWindowParams.copyFrom(getDialog().getWindow.getAttributes());
			 */
			mSpeakNowDlg.show();
			mSpeakNowDlg.showListening();
			// mCommand = mc.new GuiShowMic(receiver); // we launch the mic
			// animation on the GUI
			// invoker.launch(mCommand);
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
			mCommand = mc.new GuiShowMessage(receiver, message);
			invoker.launch(mCommand);

		}

		public void onResults(Bundle results) {
			compteur1 = 0;
			String str = new String();

			Log.d(TAG2, "onResults " + results);
			ArrayList data = results
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			for (int i = 0; i < data.size(); i++) {
				Log.d(TAG2, "result " + data.get(i));
				str += data.get(i);
			}

			VoiceInput voiceCommand = new VoiceInput(str);
			Log.d(TAG2, "onResults -----vc= " + voiceCommand);
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
		// TODO Auto-generated method stub
		super.onResume();
		mMegamipLSClient.onResume();
		Log.d(TAG2, "MainActivity onResume()--- ");

		mTrgInactivity.resetTimer();
		mTrgBlink.resetTimer();

		if (State.SHOW == mState) {
			mState = State.SEARCH_DISPLAY;
		} else {
			mState = State.STANDBY;
		}
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

	public void getNotifications() {

		// the notifications are formated on a string
		// "new emails":"wifi percentage":"battery percentage"

		MipDeviceManager deviceManager = new MipDeviceManager(context);
		try {
			String notifications = deviceManager.getNotifications();
			mCommand = mc.new GuiDisplayNotifications(receiver, notifications);
			invoker.launch(mCommand);
			Log.d(TAG3,
					"MaintActivity - getNotifications - bloc try - notifications: "
							+ notifications);
		} catch (Exception e) {
			// TODO Auto-generated catch block

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

	public void onLaunchVideo(String url) {

		Intent intent = new Intent(this, MipVideoPlayer.class);
		intent.setFlags(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
		intent.putExtra("url", url);
		startActivity(intent);

	}

}
