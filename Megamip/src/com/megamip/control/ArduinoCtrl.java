package com.megamip.control;

import android.content.Context;
import android.widget.EditText;

public interface ArduinoCtrl{
	
	
public static enum Position {
	PROJECTOR_POSITION_1,
	PROJECTOR_POSITION_2,
	PROJECTOR_POSITION_3
};	
	
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
public void engageVisor();
public void disengageVisor();
public void moveProjectorTo(Position pos);





}
