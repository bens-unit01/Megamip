/*
  bluetooth.h - Library for easy control of MAE143c myMIP toys - Version 1
  Copyright (c) 2012 Novus Design Solutions.  All right reserved.
  Author: James Strawson

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.
*/

/* Instructions: 

'include <bluetooth.h>' at the beginning of your code.
'Bluetooth bluetooth;' before setup

the refresh command returns a '0' aka FLASE if nothing has changed 
since the last time it was called. Otherwise it returns a '1' or TRUE
after updating its registry with my recent commands.
use as such:

if (Bluetooth.refresh()){
    //your code here
	}
	

	
*/


#include <stdlib.h>
#include <Arduino.h> 
#include "bluetooth.h"

float registry[32];
char buffer1[64];
int numBuffered =0;
char buffer2[16];

bluetooth::bluetooth(){
}


bool bluetooth::refresh() {
	bool isnew = 0;
	Serial.flush();
     
	 while(Serial.available()){	
		buffer1[numBuffered]=Serial.read();
		numBuffered++;
				
		 if(buffer1[numBuffered-1]=='\n' || buffer1[numBuffered-1]=='\r'){
				
			 int digits =0;
			 int i = 0;
			 int reg = 1;
			 
			 char c;
			 
			 while(i<numBuffered){				//clear the serial buffer, may contain multiple commands
				
				c=buffer1[i]; 
				i++;
				if(c=='f' || c=='F'){reg=1;}				//using one of these letters instead of an adress & semicolon indicates
				else if(c=='t' || c=='T'){reg=2;}			//one of the 5 first adresses
				else if(c=='a' || c=='A'){reg=3;}	
				else if(c=='b' || c=='B'){reg=4;}	
				else if(c=='c' || c=='C'){reg=5;}
				else if(c=='d' || c=='D'){reg=6;}
				else if ((c>='0'&& c<='9') || c=='-' || c=='.'){	//if it looks like a number, buffer the whole number
					buffer2[digits] = c;
					
					c=buffer1[i];
					i++;
					
					 while((c>='0'&& c<='9') || c=='.'){ 			//while numbers and decimals are present, fill the buffer
					   digits++;
					   buffer2[digits] = c;
					   c=buffer1[i];
					   i++;
					 }
					 digits=0;
					 
					
					/*if it's gotten here we've found a separator, if the separator is a ':' then we know the previous number 
					should have been a integer indicating the address the user is setting and reg should contain that address.
					if no address was set then assume it was intended for address #1, usually used for forward speed.
					*/
					
					if(c==':'){	
						reg=atoi(buffer2); //executed if the loop has found an address indicator ':' and should record that value
						memset(buffer2, 0, sizeof(buffer2));			
						isnew=1;
					}
					
					else{									
						registry[reg]=atof(buffer2);			//record new floating value
						memset(buffer2, 0, sizeof(buffer2)); //reset buffer
						reg=1;							   // put reg back to one for next command				
						isnew=1;
						i--;
					}
				 } 
			  }
			  memset(buffer1, 0, sizeof(buffer1)); //reset buffer
			numBuffered=0;
		}
	}
	return isnew;
}

float bluetooth::getRegister(int address){
return registry[address];
}

void bluetooth::setRegister(int address, float value){
if(address<0 || address>31){
	Serial.println("Address must be between 0 & 31");
}
registry[address]=value;
}

float bluetooth::getForwardSpeed(){
return registry[1];
}

float bluetooth::getTurnRate() {
return registry[2];
}

float bluetooth::getAux1() {
return registry[3];
}

float bluetooth::getAux2() {
return registry[4];
}

float bluetooth::getAux3() {
return registry[5];
}