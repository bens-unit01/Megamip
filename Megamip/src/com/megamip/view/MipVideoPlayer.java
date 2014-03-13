package com.megamip.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.megamip.voice.MainActivity;
import com.megamip.voice.R;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;

public class MipVideoPlayer extends YouTubeBaseActivity implements
		YouTubePlayer.OnInitializedListener, OnKeyListener {

	public final String DEVELOPER_KEY = "AIzaSyBUpZz3SZw6LGRP7Dfwd3K_Zq6VBXccpHI";
	public static final String TAG3 = "A3", TAG6 = "A6";
	private YouTubePlayerView mYoutubePlayerView;
	private YouTubePlayer mYoutubePlayer;
	private String url;
	private static MipVideoPlayer mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        mActivity = this;
		this.setContentView(R.layout.mip_video_player);
		Intent intent = getIntent();
		url = getYoutubeVideoId(intent.getStringExtra("url"));

		mYoutubePlayerView = (YouTubePlayerView) findViewById(R.id.youtubeplayer);
		mYoutubePlayerView.setPadding(20, 20, 20, 20);
		mYoutubePlayerView.setFocusable(true);
		mYoutubePlayerView.setOnKeyListener(this);
		mYoutubePlayerView.initialize(DEVELOPER_KEY, this);

	}

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult arg1) {
		Log.d(TAG3, "MipVideoPlayer onInitilizationFailure -- error: " + arg1);

	}

	@Override
	public void onInitializationSuccess(Provider arg0, YouTubePlayer player,
			boolean arg2) {
		mYoutubePlayer = player;
		mYoutubePlayer.setPlayerStateChangeListener(new PlayerStateChangeListener() {
					
					@Override
					public void onVideoStarted() {
						// TODO Auto-generated method stub
						Log.d(TAG3, "videoStarted ...");
						
					}
					
					@Override
					public void onVideoEnded() {
						// TODO Auto-generated method stub
						Log.d(TAG3, "videoEnded ...");
						mActivity.onPause();
						
						
						
					}
					
					@Override
					public void onLoading() {
						// TODO Auto-generated method stub
						Log.d(TAG3, "onLoading ...");
						
					}
					
					@Override
					public void onLoaded(String arg0) {
						// TODO Auto-generated method stub
						Log.d(TAG3, "onLoaded ...");
						
					}
					
					@Override
					public void onError(ErrorReason arg0) {
						// TODO Auto-generated method stub
						Log.d(TAG3, "onError ...error: "+arg0+" retrying again !!");
						
						mYoutubePlayer.loadVideo(url);
						
					}
					
					@Override
					public void onAdStarted() {
						// TODO Auto-generated method stub
						Log.d(TAG3, "onAdStarted...");
						
					}
				});

		mYoutubePlayer.loadVideo(url, 1);
	

	}

	private static String getYoutubeVideoId(String youtubeUrl) {
		String video_id = "";
		if (youtubeUrl != null && youtubeUrl.trim().length() > 0
				&& youtubeUrl.startsWith("http")) {

			String expression = "^.*((youtu.be"
					+ "\\/)"
					+ "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*";    // var
																								// regExp
																								// =
																								// /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
			CharSequence input = youtubeUrl;
			Pattern pattern = Pattern.compile(expression,
					Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(input);
			if (matcher.matches()) {
				String groupIndex1 = matcher.group(7);
				if (groupIndex1 != null && groupIndex1.length() == 11)
					video_id = groupIndex1;
			}
		}
		Log.d(TAG3, "MipVideoPlayer#getYoutubeVideoId video_id: " + video_id);
		return video_id;
	}

	@Override
	protected void onPause() {
		
		super.onPause();
		mYoutubePlayer.release();
		this.finish();
		
		
	}

	// ---------------------------------------------------------------------
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {

		Log.d(TAG6, "MipVideoPlayer#onKeyDown ");
		this.finish();
		return false;
	}

	@Override
	public void onBackPressed() {
		// do something on back.
		mYoutubePlayer.release();
		Intent i = new Intent(this, MainActivity.class);
		// i.setAction(Intent.ACTION_MAIN);
		// i.addCategory(Intent.CATEGORY_LAUNCHER);
		startActivity(i);

		Log.d(TAG6, "MipVideoPlayer#onBackPressed ");
		this.finish();
		return;
	}

}
