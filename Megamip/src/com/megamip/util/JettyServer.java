package com.megamip.util;
//  the code of this class is partially inspired from :http://codersapprentice.blogspot.ca/2011/09/android-integrate-jetty-server-in-my.html
/**
 * 
 * @author Messaoud BENSALEM
 * @version 1.0 09/12/13 
 * 
 * This class provide a listener to a i-Jetty web server through
 * the JettyListener interface according to the Observer design pattern
 * 
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EventObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import android.util.Log;

import com.megamip.voice.MipCommand.MipMoveForward;
import com.megamip.voice.MipUsbDevice.UsbEvent;
import com.megamip.voice.MipUsbDevice.UsbListener;

public class JettyServer {
	
	public static final String TAG1 = "A1", TAG2 = "A2", TAG3 = "A3";
	public static final int SERVERPORT = 8080;
	public static final String SPLIT_CHAR = "/";
	private ArrayList<JettyListener> 	mListeListeners = new ArrayList<JettyListener>();
	
    private Handler jettyHandler = new AbstractHandler()
    {	
    	//@Override
		public void handle(String target, Request request, HttpServletRequest MainRequestObject,
				HttpServletResponse response) throws IOException, ServletException
		{
			try
			{
				//How to get Query String/
				Log.i(TAG3, "Query string: "+target);
				

				
				for (JettyListener b : mListeListeners) {
					
					b.onNotify(new ServerEvent(this, target));
					Log.d(TAG3, "JettyServer  onNotify   ");
				}
//				
				
				
				//URI format
				//http://127.0.0.1:1234/Function/para1/para2
				
				//Http Request Type: GET/POST/PUT/DELETE
				Log.i(TAG3, "HTTP Verb: "+MainRequestObject.getMethod());
				
				BufferedReader in = new BufferedReader(new InputStreamReader(MainRequestObject.getInputStream()));
				String line = null;
				                   
				StringBuilder PostedData = new StringBuilder();
				
				while ((line = in.readLine()) != null)
				{    							
					Log.i(TAG3,"Received Message Line by Line"+line);
					PostedData.append(line);					
				}				
				
				//Http Request Data Type
				Log.i(TAG3,"Posted Data Type"+ MainRequestObject.getContentType());
				
				//Http Request Type: GET/POST/PUT/DELETE
				Log.i("Posted Data", PostedData.toString());
				
				//How To Send Responce Back
				response.setContentType("text/html");
	            response.setStatus(HttpServletResponse.SC_OK);
	            response.getWriter().println("<h1>Hello</h1>");
	            ((Request)MainRequestObject).setHandled(true);				
			}
        	catch (Exception ex)
        	{
        		Log.i(TAG3,"Error"+ex.getMessage());
			}
		}	
		
		
		
    };

	
	

  
    // constructor 
    
    public JettyServer() {
		super();
		
		
		Server server = new Server(SERVERPORT);
		server.setHandler(jettyHandler);
		try {
			server.start();
			Log.i(TAG3,"server started ...");
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG3,"block catch ...ex: "+e.getMessage());
		}
	}
    
    
    
    public interface JettyListener {
    	
    	public void onNotify(ServerEvent e);
    }
    
    
    
    public class ServerEvent extends EventObject{

		public ServerEvent(Object source, String params) {
			super(source);
			this.params = params;
		}

		/**
		 * 
		 * 
		 * 
		 */
		
		private static final long serialVersionUID = 1L;
		private String params; // forward slash separated values including the method called, expel : /function/param1/param2 ...
		
		
		public String getParams() {
			return params;
		}
		public void setParams(String params) {
			this.params = params;
		}

    	
    }
    
    
    
    public void addJettyListener(JettyListener listener) {
		mListeListeners.add(listener);
		Log.d(TAG3, "new jetty listener added ...");

	}
    
    
}
