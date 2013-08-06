package com.megamip.voice;

import java.util.Arrays;

import android.util.Log;

public class MovementInput extends MipInput {

	private static final String TAG = "A3";
	
	
	// constructors 
	
	public MovementInput() {
		super();
		
	}
	
	public MovementInput(byte[] input) {
		
	try{	
	       action = new Byte(input[2]).toString();
	       
	       Log.i(TAG, "MovementInput constructor input : "+new String(input));
	}catch(ArrayIndexOutOfBoundsException ex){
		
		  action = "82";
		  Log.e(TAG,"MovementInput constructor - error : "+ex.getMessage());
	}
			
		String[] x = {""};
	    args = x;
	}
	
	
	
	
	
	// toString()
	
	@Override
	public String toString() {
		return "MovementInput [action=" + action + ", args="
				+ Arrays.toString(args) + "]";
	}

	
	

}
