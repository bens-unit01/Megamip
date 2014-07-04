package com.megamip.view;

import com.megamip.view.MxtTouch.TouchEvent;
import com.megamip.view.MxtTouch.TouchUpListener;
import com.megamip.voice.R;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class CarouselPhoto extends CarouselActivity  {

	private final String TAG = this.getClass().getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		super.onPostCreate(R.array.pics_array);
	}

	

}
