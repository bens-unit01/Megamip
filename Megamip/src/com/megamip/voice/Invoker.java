package com.megamip.voice;

public class Invoker {

	
	// constructor 
	
	 public Invoker(){}
	 
	 
	 // public methods 
	 
	 public void launch(Command command){
		 
		 command.execute();
	 }
}
