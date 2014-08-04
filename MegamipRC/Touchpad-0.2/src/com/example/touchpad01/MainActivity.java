package com.example.touchpad01;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private String mHost = null, mIp;
	private Button btnLeft, btnRight, btnUp, btnDown, btnPos1, btnPos2,
			btnPos3, btnPower, btnSync, btnTelepresence;
	public final String TAG7 = "A7";
	public final int REFRESH_RATE_FORWARD = 250, REFRESH_RATE_TURN = 100,
			MAXIMUM_STEPS = 150;
	private Thread threadForward, threadBackward, threadLeft, threadRight,
			threadMoveProjectorTo, threadStop;
	private boolean flagForward = false, flagBackward = false,
			flagLeft = false, flagRight = false;

	private static final int RESULT_SETTINGS = 1;
	private Spinner spinner;
	private String location = "montreal";
	private SeekBar mSeekBar;
	private TextView mTxtSeekBar;
	private int mSpeed;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
 
		btnLeft = (Button) findViewById(R.id.btnLeft);
		btnRight = (Button) findViewById(R.id.btnRight);
		btnUp = (Button) findViewById(R.id.btnTop);
		btnDown = (Button) findViewById(R.id.btnDown);
		btnPos1 = (Button) findViewById(R.id.btnPos1);
		btnPos2 = (Button) findViewById(R.id.btnPos2);
		btnPos3 = (Button) findViewById(R.id.btnPos3);
		btnPower = (Button) findViewById(R.id.btnToggleLight);
		btnSync = (Button)findViewById(R.id.btnSync);
		btnTelepresence = (Button)findViewById(R.id.btnTelepresence);
		mSeekBar = (SeekBar)findViewById(R.id.seekBar1);
		mSeekBar.setProgress(13);
		mSpeed = 13;
		mTxtSeekBar = (TextView)findViewById(R.id.txtSeekBar);
	//	

		initSettings();
		addItemsOnSpinner();
		showSettings();
		setListeners();
		
		


	}
	
	
	private void setListeners(){
		
		
		btnTelepresence.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
				
					Intent i = Intent.parseUri("telepresenceapp://"+mIp+"//"+mSpeed, Intent.URI_INTENT_SCHEME);
					startActivity(i);
					
				} catch (URISyntaxException e) {
					
					e.printStackTrace();
				}
				
			}
		});
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mTxtSeekBar.setText("" + progress);
				mSpeed = progress;
				
			}
		});
	       btnSync.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {


					changeLocation(location);
						Log.d(TAG7, " onLongClick - Up released");
					}
					return false;
				}
			} );
			
			
			btnPower.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {


						powerOnOffProjector();
						Log.d(TAG7, " onLongClick - Up released");
					}
					return false;
				}
			} );
			
			
			btnUp.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						flagForward = false;
						// mipStop();
						Log.d(TAG7, " onLongClick - Up released");
					}
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						flagForward = true;
						threadForward = new Thread(new MoveForwardRunnable());
						threadForward.start();
						Log.d(TAG7, " onLongClick - Up");

					}
					return false;
				}
			});

			btnDown.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						flagBackward = false;
						// mipStop();
						Log.d(TAG7, " onLongClick - Down released");
					}

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						flagBackward = true;
						threadBackward = new Thread(new MoveBackwardRunnable());
						threadBackward.start();
						Log.d(TAG7, " onLongClick - Down");
					}
					return false;
				}
			});

			btnLeft.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						flagLeft = false;
						// mipStop();
						Log.d(TAG7, " onLongClick - Left released");
					}

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						flagLeft = true;
						threadLeft = new Thread(new MoveLeftRunnable());
						threadLeft.start();
						Log.d(TAG7, " onLongClick - Left");
					}
					return false;
				}
			});

			btnRight.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						flagRight = false;
						// mipStop();
						Log.d(TAG7, " onLongClick - Right released");
					}

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						flagRight = true;
						threadRight = new Thread(new MoveRightRunnable());
						threadRight.start();
						Log.d(TAG7, " onLongClick - Right");
					}
					return false;
				}
			});

			btnPos1.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						moveProjectorTo(1);
						Log.d(TAG7, " onLongClick - pos 1");
					}
					return false;
				}
			});

			btnPos2.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						moveProjectorTo(2);
						Log.d(TAG7, " onLongClick - pos 1");
					}
					return false;
				}
			});

			btnPos3.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						moveProjectorTo(3);
						Log.d(TAG7, " onLongClick - pos 1");
					}
					return false;
				}
			});
	}

	private void addItemsOnSpinner() {
		
		spinner = (Spinner) findViewById(R.id.spinner1);
		List<String> list = new ArrayList<String>();
		list.add("Montreal");
		list.add("San Francisco");
		list.add("Tokyo");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
			
				String selectedItem =  parent.getItemAtPosition(pos).toString();
			
				if(selectedItem.equals("San Francisco")) {
					location = "san_francisco";
				}else{
					
					location = selectedItem.toLowerCase();
				}
				
				
				Log.d("A3",location);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	private void initSettings() {
//		 PropertyReader pReader = new PropertyReader(this);
//		 Properties prop = pReader.getProperties("params.properties");
//		 host = prop.getProperty("host");
		 
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		mIp = sharedPrefs.getString("prefHostname", "10.10.250.115");
		mHost = "http://"+ mIp + ":8080/";
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	

	private void powerOnOffProjector() {
		threadMoveProjectorTo = new Thread(new Runnable() {

			@Override
			public void run() {

				String url = mHost + "toggleProjector/";

				URL obj = null;
				HttpURLConnection con = null;
				Log.d(TAG7, "thread toggleProjector ...  " );
				try {
					obj = new URL(url);

				} catch (IOException e1) {
					Log.d(TAG7, "thread ... block catch !!");
					e1.printStackTrace();
				}

				try {
					con = (HttpURLConnection) obj.openConnection();
					con.setRequestMethod("GET");
					con.setRequestProperty("User-Agent", "Mozilla/5.0");
					int responseCode = con.getResponseCode();

				} catch (IOException e1) {
					Log.d(TAG7, "thread ... block catch !!");
					e1.printStackTrace();
				}

			}
		});

		threadMoveProjectorTo.start();

	}
	
	private void changeLocation(final String newLocation) {
		threadMoveProjectorTo = new Thread(new Runnable() {

			@Override
			public void run() {

				String url = mHost + "changeLocation/"+newLocation;

				URL obj = null;
				HttpURLConnection con = null;
				Log.d(TAG7, "thread changeLocation ...  " );
				try {
					obj = new URL(url);

				} catch (IOException e1) {
					Log.d(TAG7, "thread ... block catch !!");
					e1.printStackTrace();
				}

				try {
					con = (HttpURLConnection) obj.openConnection();
					con.setRequestMethod("GET");
					con.setRequestProperty("User-Agent", "Mozilla/5.0");
					int responseCode = con.getResponseCode();

				} catch (IOException e1) {
					Log.d(TAG7, "thread ... block catch !!");
					e1.printStackTrace();
				}

			}
		});

		threadMoveProjectorTo.start();

	}


	public class MoveForwardRunnable implements Runnable {

		@Override
		public void run() {

			String url = mHost + "moveForward/"+mSpeed+"/5";

			URL obj = null;
			HttpURLConnection con = null;
			int max = 0;

			try {
				obj = new URL(url);

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			while (flagForward && (max <= MAXIMUM_STEPS)) {
				Log.d(TAG7, "thread forward...");

				try {
					con = (HttpURLConnection) obj.openConnection();
					con.setRequestMethod("GET");
					con.setRequestProperty("User-Agent", "Mozilla/5.0");
					int responseCode = con.getResponseCode();
					Thread.sleep(REFRESH_RATE_FORWARD);

				} catch (InterruptedException e1) {
					Log.d(TAG7, "thread ... block catch !!");
					e1.printStackTrace();

				} catch (SocketException e2) {
					Log.d(TAG7, "thread ... block catch !!");
					e2.printStackTrace();
				} catch (IOException e3) {
					Log.d(TAG7, "thread ... block catch !!");
					e3.printStackTrace();
				}

				max++;
			}

		}

	}

	public class MoveBackwardRunnable implements Runnable {

		@Override
		public void run() {

			String url = mHost + "moveBackward/"+mSpeed+"/5";
			int max = 0;
			URL obj = null;
			HttpURLConnection con = null;

			try {
				obj = new URL(url);

			} catch (IOException e1) {
				Log.d(TAG7, "thread ... block catch !!");
				e1.printStackTrace();
			}

			while (flagBackward && (max <= MAXIMUM_STEPS)) {
				Log.d(TAG7, "thread backward...");

				try {
					con = (HttpURLConnection) obj.openConnection();
					con.setRequestMethod("GET");
					con.setRequestProperty("User-Agent", "Mozilla/5.0");
					int responseCode = con.getResponseCode();
					Thread.sleep(REFRESH_RATE_FORWARD);

				} catch (InterruptedException e) {
					Log.d(TAG7, "thread ... block catch !!");
					e.printStackTrace();
				} catch (SocketException e2) {
					Log.d(TAG7, "thread ... block catch !!");
					e2.printStackTrace();

				} catch (IOException e1) {
					Log.d(TAG7, "thread ... block catch !!");
					e1.printStackTrace();
				}
				max++;
			}

		}

	}

	public class MoveLeftRunnable implements Runnable {

		@Override
		public void run() {

			String url = mHost + "moveLeft/"+mSpeed+"/5";
			int max = 0;
			URL obj = null;
			HttpURLConnection con = null;

			try {
				obj = new URL(url);

			} catch (IOException e1) {
				Log.d(TAG7, "thread ... block catch !!");
				e1.printStackTrace();
			}

			while (flagLeft && (max <= MAXIMUM_STEPS)) {
				Log.d(TAG7, "thread Left...");

				try {
					con = (HttpURLConnection) obj.openConnection();
					con.setRequestMethod("GET");
					con.setRequestProperty("User-Agent", "Mozilla/5.0");
					int responseCode = con.getResponseCode();
					Thread.sleep(REFRESH_RATE_TURN);

				} catch (InterruptedException e) {
					Log.d(TAG7, "thread ... block catch !!");
					e.printStackTrace();
				} catch (SocketException e2) {
					Log.d(TAG7, "thread ... block catch !!"); 
					e2.printStackTrace();

				} catch (IOException e1) {
					Log.d(TAG7, "thread ... block catch !!");
					e1.printStackTrace();
				}
				max++;
			}

		}

	}

	public class MoveRightRunnable implements Runnable {

		@Override
		public void run() {

			String url = mHost + "moveRight/"+mSpeed+"/5";
			int max = 0;
			URL obj = null;
			HttpURLConnection con = null;
			Log.d(TAG7, "url: " + url);
			try {
				obj = new URL(url);

			} catch (IOException e1) {
				Log.d(TAG7, "thread ... block catch !!" + url);
				e1.printStackTrace();
			}

			while (flagRight && (max <= MAXIMUM_STEPS)) {
				Log.d(TAG7, "thread right...");

				try {
					con = (HttpURLConnection) obj.openConnection();
					con.setRequestMethod("GET");
					con.setRequestProperty("User-Agent", "Mozilla/5.0");
					int responseCode = con.getResponseCode();
					Thread.sleep(REFRESH_RATE_TURN);

				} catch (InterruptedException e) {
					Log.d(TAG7, "thread ... block catch 1 !! url: ");
					e.printStackTrace();
				} catch (SocketException e2) {
					Log.d(TAG7, "thread ... block catch 2!!");
					e2.printStackTrace();

				} catch (IOException e1) {
					Log.d(TAG7, "thread ... block catch 3!!");
					e1.printStackTrace();
				}
				max++;
			}

		}

	}

	private void moveProjectorTo(final int pos) {
		threadMoveProjectorTo = new Thread(new Runnable() {

			@Override
			public void run() {

				String url = mHost + "moveProjectorTo/" + pos;

				URL obj = null;
				HttpURLConnection con = null;
				Log.d(TAG7, "thread moveProjectorTo ... pos: " + pos);
				try {
					obj = new URL(url);

				} catch (IOException e1) {
					Log.d(TAG7, "thread ... block catch !!");
					e1.printStackTrace();
				}

				try {
					con = (HttpURLConnection) obj.openConnection();
					con.setRequestMethod("GET");
					con.setRequestProperty("User-Agent", "Mozilla/5.0");
					int responseCode = con.getResponseCode();

				} catch (IOException e1) {
					Log.d(TAG7, "thread ... block catch !!");
					e1.printStackTrace();
				}

			}
		});

		threadMoveProjectorTo.start();

	}

	private void mipStop() {

		threadStop = new Thread(new Runnable() {

			@Override
			public void run() {

				String url = mHost + "stop/13/5";

				URL obj = null;
				HttpURLConnection con = null;
				Log.d(TAG7, "thread stop ...");
				try {
					obj = new URL(url);

				} catch (IOException e1) {
					Log.d(TAG7, "thread ... block catch !!");
					e1.printStackTrace();
				}

				try {
					con = (HttpURLConnection) obj.openConnection();
					con.setRequestMethod("GET");
					con.setRequestProperty("User-Agent", "Mozilla/5.0");
					int responseCode = con.getResponseCode();

				} catch (IOException e1) {
					Log.d(TAG7, "thread ... block catch !!");
					e1.printStackTrace();
				}

			}
		});

		threadStop.start();

	}

	// ---------- Prefs handling

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent i = new Intent(this, UserSettingActivity.class);
			startActivityForResult(i, RESULT_SETTINGS);
			break;

		}
		return true;
	}

	private void showSettings() {

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		StringBuilder builder = new StringBuilder();
		builder.append("\n \t Host IP:\t\t \t"
				+ sharedPrefs.getString("prefHostname", "NULL"));
		builder.append("\n \t Username :\t"
				+ sharedPrefs.getString("prefUsername", "NULL"));
		TextView settings = (TextView) findViewById(R.id.txtSettings);
		settings.setText(builder);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SETTINGS:
			showSettings();
			SharedPreferences sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			mIp = sharedPrefs.getString("prefHostname", "10.10.250.115");
			mHost = "http://" + mIp + ":8080/";

			break;

		}
	}

}
