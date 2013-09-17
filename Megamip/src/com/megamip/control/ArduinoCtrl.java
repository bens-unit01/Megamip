package com.megamip.control;

import android.content.Context;
import android.widget.EditText;

public interface ArduinoCtrl {
	
	
	
	
	/*
	 * Immediately Drive as described
	 * */

public int drive(int speedL, int speedR, int distanceL, int distanceR);

/*
 * Add following motion to queue
 * */

public int driveQueue(int speedL, int speedR, int distanceL, int distanceR);

/*
 * GET UP MIP!
 * */
public int upright();
public int fallover();
public int stop();
public int reportEncoders(int refreshRate);

public void listen();

public void stopListen();






}
