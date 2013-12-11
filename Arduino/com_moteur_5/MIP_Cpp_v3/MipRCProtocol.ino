
// MegaMip protocol -- this file provides an interface between the HiMedia and Arduino to process 
// Remote control through the Telepresence module
// @author Messaoud BENSALEM
// @version 1.0 - Dec 05, 2013 

int turnData = 0;
int deb01 =0, deb02 = 0;
float rcU;
float rcTurn;
// boolean newInput = false;   
// byte buffer[200];           // buffer for the serial input 


void MipRCProtocol(){
  
 
   
   
    if (Serial.available()) 
  {
   int i=0;
   

   
  //  Serial.flush();
  
    while(Serial.available()){
         buffer[i] = Serial.read();
//--------------DEBUG----------------------
#ifdef DEBUG

  Serial.print(" i "); Serial.print(i);
   Serial.print(" buffer[i] "); Serial.println(buffer[i]);
#endif
//-------------DEBUG END -----------------
      /*  if (buffer[0] != 254){  // first byte of our packet
          Serial.flush();
          i=0;
          return;
        }*/
        i++;
        if (i>6){
          Serial.flush();
          i=0;
        //  return;
        }
    }
    Serial.flush();
    mipHandler();
  }
  else{return;} 
}   



void mipHandler(){
   

          // we need the start byte (0xFE ) index,  
          int i = 0;
        //  while(buffer[i] != 0xFE)  i++; 
          byte cmd = buffer[i+1];
          byte arg1 = 0;
          byte arg2 = 0;
          byte arg3 = 0;
          byte arg4 = 0;
          int speedLeft     = 0;
          int speedRight    = 0;
          int distanceLeft  = 0;
          int distanceRight = 0;

     switch(cmd){
      
      case 0x10 : // drive
                    
                    arg1 = buffer [i+2];
                    arg2 = buffer [i+3];
                    arg3 = buffer [i+4];
                    arg4 = buffer [i+5];
                    speedLeft     = (signed char)((speedLeft << 8) + arg1);  // some plumbing 
                    speedRight    = (signed char)((speedRight << 8) + arg2); // to get the signed values from C unsigned byte type. 
                    distanceLeft  = (signed char)((distanceLeft << 8) + arg3); 
                    distanceRight = (signed char)((distanceRight << 8) + arg4); 
                    drive(speedLeft, speedRight, distanceLeft, distanceRight);
                    break;
      case 0x11 : // driveQueue 
                    arg1 = buffer [i+2];
                    arg2 = buffer [i+3];
                    arg3 = buffer [i+4];
                    arg4 = buffer [i+5];
                    // call to driveQueue(arg1, arg2, arg3, arg4)
                   break;
        
      case 0x12 : // upritht  
                    // call to upright()
                   break;
      case 0x13 : // upritht  
                    // call to fallover()
                   break;
      case 0x14 : // upritht  
                    // call to stop()
                   break;
      case 0x15 : // upritht  
                    // call to reportEncoders()
                   break;
     default :  break;
      
      
      
      
      
      }


}

void drive(int speedL, int speedR, int distanceL, int distanceR){
  
//--------------DEBUG----------------------
#ifdef DEBUG

 // Serial.print(" speedL "); Serial.print(speedL);
  // Serial.print(" speedR "); Serial.print(speedR);
   // Serial.print(" distanceL "); Serial.print(distanceL);
    // Serial.print(" speedL "); Serial.println(distanceR);
#endif
//-------------DEBUG END -----------------
   
   float left = (float)speedL / 100;
   float right = (float)speedR / 100;
   float turn = ((float)abs(speedL -speedR)/2)/100; 
   rcU = (left < right) ? (left + turn):(right + turn);
   turn = (left < right) ? -turn :turn;
   rcTurn = turn;
   float time = distanceL * 100;
   
//--------------DEBUG----------------------
#ifdef DEBUG

  //Serial.print("u: "); Serial.print(rcU);
   //Serial.print("t: "); Serial.println(rcTurn);
#endif
//-------------DEBUG END -----------------
 //   RC_Drive(u, turn, time);
}


float get_Forward(){
  return rcU;
}

float get_Turn(){
  return rcTurn;
}


