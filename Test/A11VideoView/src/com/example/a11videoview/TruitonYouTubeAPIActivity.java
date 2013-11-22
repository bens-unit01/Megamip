package com.example.a11videoview;

import java.util.List;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.Toast;

public class TruitonYouTubeAPIActivity extends YouTubeBaseActivity implements  

YouTubePlayer.OnInitializedListener{


private
 
YouTubePlayer YPlayer;
    
private static final String YoutubeDeveloperKey = "AIzaSyDnzEMrqVRm5TZBCuB28--1bz-3-IZbn3E";
    
private static final int RECOVERY_DIALOG_REQUEST = 1;
 
    
@Override
protected void onCreate (Bundle savedInstanceState)
 {
        super.onCreate(savedInstanceState);
        
		setContentView(R.layout.activity_you_tube_api);
		 
		        
		YouTubePlayerView  youTubeView =(YouTubePlayerView)findViewById(R.id.youtube_view);
		        
		youTubeView.initialize(YoutubeDeveloperKey,this);
    
}
 
    
@Override public boolean onCreateOptionsMenu( Menu menu)
{
        // Inflate the menu; this adds items to the action bar if it is present.
        
	getMenuInflater().inflate(R.menu.you_tube_api,menu);
        
return true;
    
}
 
    
@Override
    
public void onInitializationFailure(YouTubePlayer.Provider provider,YouTubeInitializationResult errorReason)
 {
        if(errorReason.isUserRecoverableError())
 
{
            
errorReason.getErrorDialog(this,RECOVERY_DIALOG_REQUEST).show();
        
}
 
else
 
{
            
String errorMessage=String.format("There was an error initializing the YouTubePlayer",errorReason.toString());
            Toast.makeText(this,errorMessage,Toast.LENGTH_LONG).show();
        
}
    
}
 
    
@Override
    
protected void onActivityResult(int requestCode,int resultCode,Intent data)
{
        if(requestCode == RECOVERY_DIALOG_REQUEST){
            
// Retry initialization if user performed a recovery action
            
        	getYouTubePlayerProvider().initialize(YoutubeDeveloperKey,this);
        
}
    
}
 
    
protected YouTubePlayer.Provider getYouTubePlayerProvider()
 {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    
}
 
    
@Override
    
public void onInitializationSuccess(Provider provider,YouTubePlayer player,boolean wasRestored)
 {
      if(!wasRestored){
            
    	  YPlayer=player;
            
		/*
		             * Now that this variable YPlayer is global you can access it
		             * throughout the activity, and perform all the player actions like
		             * play, pause and seeking to a position by code.
		             */
		            
		YPlayer.cueVideo("2zNSgSzhBfM");
        //bBH0ele7qdw   2zNSgSzhBfM  sKeslrZ-i6k  2zNSgSzhBfM
}
    
}
 
	
		




	
}
