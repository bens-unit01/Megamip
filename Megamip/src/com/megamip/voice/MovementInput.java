package com.megamip.voice;

import java.util.Arrays;

public class MovementInput extends MipInput {

	
	
	
	// constructors 
	
	public MovementInput() {
		super();
		
	}
	
	public MovementInput(byte[] input) {
		
	 action = new Byte(input[2]).toString();
		
		
	}
	
	
	
	
	
	// toString()
	
	@Override
	public String toString() {
		return "MovementInput [action=" + action + ", args="
				+ Arrays.toString(args) + "]";
	}

	
	

}
