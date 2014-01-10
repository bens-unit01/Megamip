
// MegaMip protocol -- this file provides an interface between the HiMedia and Arduino to process 
// Remote control through the Telepresence module
// @author Messaoud BENSALEM
// @version 1.0 - Dec 05, 2013 

int turnData = 0;
int deb01 =0, deb02 = 0;
float rcU, old_rcU;
float rcTurn;
int turnCounter = 1, forwardCounter = 1, startCounter = 1, stopCounter = 1;
const int STEP = 70;
boolean step1 = true, step2 = false, step3 = false;
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
          while(buffer[i] != 0xFE && i<55)  i++; 
         // byte cmd = buffer[i+1];
          byte arg1 = 0;
          byte arg2 = 0;
          byte arg3 = 0;
          byte arg4 = 0;
          int speedLeft     = 0;
          int speedRight    = 0;
          int distanceLeft  = 1;
          int distanceRight = 1;

   
                    
                    arg1 = buffer [i+1];
                    arg2 = buffer [i+2];
                  
                //    arg3 = buffer [i+4];
                //    arg4 = buffer [i+5];
                    speedLeft     = (signed char)((speedLeft << 8) + arg1);  // some plumbing 
                    speedRight    = (signed char)((speedRight << 8) + arg2); // to get the signed values from C unsigned byte type. 
                //    distanceLeft  = (signed char)((distanceLeft << 8) + arg3); 
                //    distanceRight = (signed char)((distanceRight << 8) + arg4); 
                    drive(speedLeft, speedRight, distanceLeft, distanceRight);
                
      
      
      
     


}

void drive(int speedL, int speedR, int distanceL, int distanceR){
  

   
   float left = (float)speedL / 100;
   float right = (float)speedR / 100;
   float turn = ((float)abs(speedL -speedR)/2)/100; 
  // rcU = (left < right) ? (left + turn):(right + turn);

  rcU = (speedL > 0)? 0.00018: -0.00018;
  
   turn = (left < right) ? - 0.1 : 0.1;
   turn = (left == right) ? 0.0 : turn; 
   rcTurn = turn;
   float time = distanceL * 100;
    forwardCounter = 0;

}


float get_Forward(){
 
    if( rcU != 0){
      if(step1){
        startCounter++;
        if( startCounter % STEP == 0){
          step1 = false; 
          step2 = true;
          startCounter =1;
        }else{
          rcU = (rcU > 0)? (rcU + 0.00018) : (rcU - 0.00018);
        }
      }else if(step2){
         startCounter++;
        if( startCounter % STEP == 0){
         step2 =false;
         step3 = true; 
         startCounter = 1;
        }
      
      }else if(step3){
         startCounter++;
        if( startCounter % STEP == 0){
          step1 = true;
          step3 =false;
          startCounter = 1; 
          rcU = 0.0;
          
        }else{
          
          rcU = (rcU > 0)? (rcU - 0.00018) : (rcU + 0.00018);
        }
      
      }
    }
  
  test1 = rcU;
  return rcU;
}

float get_Turn(){
   turnCounter++;
  if(step3){
   rcTurn = 0.0;
   turnCounter = 1;
  }
  test2 = rcTurn;
  return rcTurn;
}


