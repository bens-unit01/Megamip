package com.megamip.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.megamip.util.LocalVideoPlayer;
import com.megamip.view.DroidGap.ScreenOrientation;
import com.megamip.view.MxtTouch.TouchEvent;
import com.megamip.view.MxtTouch.TouchUpListener;
import com.megamip.voice.R;

public class CarouselVideo extends CarouselActivity {

	private final String TAG = this.getClass().getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mTouchUpListener = this;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {

		super.onPostCreate(savedInstanceState);
		super.onPostCreate(R.array.vids_array);
	}

	@Override
	public void onNotify(TouchEvent e) {
		
		super.onNotify(e);
		int x = e.getPosition().x;
		int y = e.getPosition().y;
		Log.d(TAG, "onNotify x: " + x);
		int diffX = Math.abs(xInit - x);
		int diffY = Math.abs(yInit - y);

		if (diffX < 50 && diffY < 50) {
			if (x > 200 && x < 3000) {

				int orientation = (mScreenOrientation == ScreenOrientation.POSITION_0) ? 0
						: 180;
				Intent intent = new Intent(this, LocalVideoPlayer.class);
				intent.putExtra("videoId", "vid0" + index);
				intent.putExtra("orientation", orientation);
				Log.d(TAG, " onNotify - play button pushed index: " + index
						+ " orientation: " + orientation);
				startActivityForResult(intent, 123);
			}
		}
	}

}
