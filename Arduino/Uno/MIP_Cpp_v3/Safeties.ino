//Initialize Arduino Hard Reset. Will be used for Uprighing when added
void Initialize_Arduino_Hard_Reset(){
  #define Reset 12
  pinMode(Reset,OUTPUT);
  digitalWrite(Reset,HIGH);
  Serial.println("Arduino Hard Reset Engaged!");  
}

///////////////////////////////////////////////////////////////////////////////////////////////////////
// Function that calls all the safeties to check for. You can remove individual safeties by commenting out parts
void Safety_Checks(){
  //  PickUp_Safety();
    Fall_Safety();
}
///////////////////////////////////////////////////////////////////////////////////////////////////////
//Safety for when MIP is picked up
void PickUp_Safety(){
   Encoderd = get_Encoderd();
   if (Encoderd>=20 || Encoderd<=-20) {FallDetection();}
}

///////////////////////////////////////////////////////////////////////////////////////////////////////
//Safety for when MIP has fallen or when it will fall
void Fall_Safety(){
    if(get_theta() >= 40*pi/180 || get_theta() <= -40*pi/180) {FallDetection();}
    else if( get_Fallen() == true) {FallDetection();}
}

///////////////////////////////////////////////////////////////////////////////////////////////////////
//Safety for Tracking Encoders
void Track_Encoders_Safety(){
  reset_Encoder();
  reset_Estimator();
  reset_refPhi();
}


