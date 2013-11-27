package com.megamip.view;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.megamip.voice.MainActivity;
import com.megamip.voice.R;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;

public class MipVideoPlayer extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, OnKeyListener {

	public final String DEVELOPER_KEY = "AIzaSyBUpZz3SZw6LGRP7Dfwd3K_Zq6VBXccpHI";
	public static final String TAG3 = "A3";
	private YouTubePlayerView mYoutubePlayerView;
	private YouTubePlayer mYoutubePlayer;
	private String url;
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		this.setContentView(R.layout.mip_video_player);
		Intent intent = getIntent();
		url =  getYoutubeVideoId(intent.getStringExtra("url"));
	
		mYoutubePlayerView = (YouTubePlayerView)findViewById(R.id.youtubeplayer);
		mYoutubePlayerView.setPadding(20, 20, 20, 20);
		mYoutubePlayerView.setFocusable(true);
		mYoutubePlayerView.setOnKeyListener(this);
		mYoutubePlayerView.initialize(DEVELOPER_KEY, this);
		
	}
	public MipVideoPlayer() {
		super();

	}
	
   
	


	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult arg1) {
		Log.d(TAG3,"MipVideoPlayer onInitilizationFailure -- error: "+arg1);
		
	}


	@Override
	public void onInitializationSuccess(Provider arg0, YouTubePlayer player,
			boolean arg2) {
		mYoutubePlayer = player;
		//mYoutubePlayer.loadVideo("9YoGPeP_UBA");
		mYoutubePlayer.loadVideo(url);
		
		
	}

	private static String getYoutubeVideoId(String youtubeUrl)
    {
        String video_id="";
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http"))
        {

            String expression = "^.*((youtu.be"+ "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
            CharSequence input = youtubeUrl;
            Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches())
            {
                String groupIndex1 = matcher.group(7);
                if(groupIndex1!=null && groupIndex1.length()==11)
                    video_id = groupIndex1;
            }
        }
        Log.d(TAG3,"MipVideoPlayer#getYoutubeVideoId video_id: "+video_id);
        return video_id;
    }
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		
		mYoutubePlayer.release();
		Intent i = new Intent(this, MainActivity.class);
	//	i.setAction(Intent.ACTION_MAIN);
	//	i.addCategory(Intent.CATEGORY_LAUNCHER);
		startActivity(i);
		
		 Log.d(TAG3,"MipVideoPlayer#onKeyDown ");
		 this.finish();
		return false;
	}
	
}
