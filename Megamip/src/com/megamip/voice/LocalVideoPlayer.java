package com.megamip.voice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class LocalVideoPlayer extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_video_player);

		Intent intent = getIntent();
		String videoId = intent.getStringExtra("videoId");
		int orientation = intent.getIntExtra("orientation", 0);
		videoId += (orientation == 0) ? ".mp4" : "-flp.mp4";

		VideoView video = (VideoView) findViewById(R.id.videoView1);
		MediaController mediaController = new MediaController(this);
		mediaController.setAnchorView(video);
		video.setMediaController(mediaController);

		video.setKeepScreenOn(true);
		video.setVideoPath("/data/user/video/" + videoId);
		video.start();
		video.requestFocus();
	}
}
