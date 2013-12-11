/*
  bluetoothSpeedControl - program for testing the bluetooth library
  Copyright (c) 2012 Novus Design Solutions.  All right reserved.
  Author: James Strawson

  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.
*/
#define AIN1 8
#define AIN2 7
#define BIN1 10
#define BIN2 11
#define PWMA 6
#define PWMB 5
#define STBY 13

#include <bluetooth.h>
bluetooth Bluetooth; //Creates a new Instance of the bluetooth class
long lastLoopMillis;
int mainLoopPeriod = 20; //50hz main loop -> 20ms period

void setup(){
  Serial.begin(115200); //must always start serial at beginning of your code
  digitalWrite(STBY, HIGH);
}

void loop(){
  
  if(millis() - lastLoopMillis >= mainLoopPeriod){
    if (Bluetooth.refresh()){ //the refresh function returns true if there is new information
      Serial.print(Bluetooth.getForwardSpeed()); //Print new information
      Serial.print("\t");
      Serial.println(Bluetooth.getTurnRate());
      setMotors();
    }
    lastLoopMillis = millis();
  }
}
















void setMotors() {
	float dutyL=Bluetooth.getForwardSpeed()-Bluetooth.getTurnRate();
        float dutyR=Bluetooth.getForwardSpeed()+Bluetooth.getTurnRate();
        
	if (dutyL < 0) {
		digitalWrite(AIN1, HIGH);
		digitalWrite(AIN2, LOW);
		analogWrite(PWMA, -dutyL * 25.5);
	} else {
		digitalWrite(AIN1, LOW);
		digitalWrite(AIN2, HIGH);
		analogWrite(PWMA, dutyL * 25.5);}

	if(dutyR<0) {
		digitalWrite(BIN1, LOW);
		digitalWrite(BIN2, HIGH);
		analogWrite(PWMB, -dutyR * 25.5);
	} else {
		digitalWrite(BIN1, HIGH);
		digitalWrite(BIN2, LOW);
		analogWrite(PWMB, dutyR * 25.5);}
}
