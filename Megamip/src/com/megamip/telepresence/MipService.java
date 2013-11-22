package com.megamip.telepresence;



import com.megamip.util.JettyServer;
import com.megamip.voice.Command;
import com.megamip.voice.Invoker;
import com.megamip.voice.MipCommand;
import com.megamip.voice.MipReceiver;


import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MipService extends Service{

	
	private MipCommand mc;
	private Invoker invoker;
	private MipReceiver receiver;
	private Handler handler = null;
	private JettyServer  mJettyServer;
	private Command mCommand;
	public static final String TAG1 = "A1", TAG2 = "A2", TAG3 = "A3";
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	  @Override
	  	    public void onCreate() {
		  
		    handler = new Handler();
		    invoker = new Invoker();
	       // receiver = new MipReceiver(handler, null, this);
	        mc = new MipCommand();
	        mJettyServer = new JettyServer();
	        setListeners();
		  
	  	        Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();
	  	 
	  	    }
	  	 
	  	    @Override
	  	    public void onStart(Intent intent, int startId) {
	  	        // For time consuming an long tasks you can launch a new thread here...
	  	        Toast.makeText(this, " Service Started", Toast.LENGTH_LONG).show();
	  	 
	  	    }
	  	 
	  	    @Override
	  	    public void onDestroy() {
	  	        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
	  	 
	  	    }
	  	    
	  	    
	  	    
	  	  private void setListeners() {
	  		mJettyServer.addJettyListener(new JettyServer.JettyListener() {
				
				@Override
				public void onNotify(ServerEvent e) {
					// TODO Auto-generated method stub
					 Log.d(TAG3," onNotify fired - jettyListener ... 0");
		  				jettyHandler(e.getParams());
		  				
		  				 Log.d(TAG3," onNotify fired - jettyListener ... 1");
					
				}

				@Override
				public void onNotify(com.megamip.util.JettyServer.ServerEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
	  	  }
	  	  
	  	  

private void jettyHandler(String params) {

	
			String[] input = params.split(JettyServer.SPLIT_CHAR);
			
			
		Log.d(TAG2, "jettHandler cmd: "+input[1]);	
		
		 if(input[1].equals("moveForward")){
				mCommand = mc.new MipMoveForward(receiver);
			 invoker.launch(mCommand);
			 Log.d(TAG2, "jettHandler triggered moveForward");	
		 	}
		 
		 if(input[1].equals("moveBackward")){
				mCommand = mc.new MipMoveBackward(receiver);
			 invoker.launch(mCommand);
			 Log.d(TAG2, "jettHandler triggered moveBackward");	
			}
			
		 if(input[1].equals("moveLeft")){
				mCommand = mc.new MipMoveLeft(receiver);
			 invoker.launch(mCommand);
			 Log.d(TAG2, "jettHandler triggered moveLeft");	
			}
		 
		 if(input[1].equals("moveRight")){
				mCommand = mc.new MipMoveRight(receiver);
				invoker.launch(mCommand);
			 Log.d(TAG2, "jettHandler triggered moveRight");	
			}
			

}
}
