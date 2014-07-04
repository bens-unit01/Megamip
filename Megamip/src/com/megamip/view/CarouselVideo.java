package com.megamip.view;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

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
		// TODO Auto-generated method stub
		super.onNotify(e);
		int x = e.getPosition().x;

		Log.d(TAG, "onNotify x: " + x);
		int diffX = Math.abs(xInit - x);
		if (diffX < 50) {
			if (x > 200 && x < 3000) {
				Log.d(TAG, " onNotify - play button pushed");
			}
		}
	}

}
