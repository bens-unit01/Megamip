///////Program written by Saam Ostovari at the UCSD Coordinated Robotics Lab///////////
///////////////////////////////////NDA SD2013-802//////////////////////////////////////
//Attached Libraries
#include <SoftwareSerial.h>

// Defining Pins for Motor Control (Timer1 Interrupt kills pins 9 and 10 pwm)
#define APWM 5    
#define AIn2 11  
#define AIn1 10  
#define BIn1 7   
#define BIn2 8   
#define BPWM 6   
#define stby 13

#define DEBUG 12

// SoftwareSerial mySerial(10, 11); // RX, TX
SoftwareSerial mySerial(10, DEBUG); // RX, TX
int GestureData[6];
int turnData = 0;
int deb01 =0, deb02 = 0;
boolean newInput = false;   
byte buffer[200];           // buffer for the serial input 

/////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////SETUP///////////////////////////////////////////////////
void setup()
{
  
  int error;
  uint8_t c;

  Serial.begin(115200);


 // set the data rate for the SoftwareSerial port
  mySerial.begin(115200);
  
  //Debug pin for timing analysis
  pinMode(A0,OUTPUT); 
  digitalWrite(A0,LOW); 
  
  //Prepare motors
   ///////////////////////////////////////////////////////////////////////////////
  // Setting Pin Mode for Sensors

  
  //Pin Mode for Motors
  pinMode(APWM,OUTPUT); pinMode(AIn1,OUTPUT); pinMode(AIn2,OUTPUT);
  pinMode(BPWM,OUTPUT); pinMode(BIn1,OUTPUT); pinMode(BIn2,OUTPUT);
  pinMode(stby,OUTPUT);
    
///////////////////////////////////////////////////////////////////////////////
  // Initialize Motors to off Position
  digitalWrite(stby,LOW);
  digitalWrite(AIn1,LOW); digitalWrite(AIn2,LOW); digitalWrite(APWM,HIGH);
  digitalWrite(BIn1,LOW); digitalWrite(BIn2,LOW); digitalWrite(BPWM,HIGH); 
  
///////////////////////////////////////////////////////////////////////////////
 
 // Turn motor drivers off from standby
 digitalWrite(stby,LOW); 
 
 
 //////////////////////////////

 
}


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////Main Loop///////////////////////////////////////////////////////////  
void loop()
{
  // Serial.print("hi");
  int i=0;
  
// gesture reading  
    while (mySerial.available())
    {
     // Serial.write(mySerial.read());
      
          if (mySerial.read() == 0xFC)
          {
            while (mySerial.available())
            {
              GestureData[i] = float(mySerial.read());
              i++;
      
              if (i>1)
              {
               // ProcessSwipe();                               // commented to use the soft serial for debugging -------------------------------------------
    
                i=0;
                return;
              }
            }   
          }
     
}


// input handling ---------------------------------------
     
    if(newInput){
          mipHandler();
          newInput = false;
         
    }    
     
   
   
//---------------DEBUG--------------------------------
#ifdef DEBUG
      deb01++;
      if((deb01 % 30000) == 0) {
        deb01 = 0; deb02++;
        
         mySerial.print(deb02,DEC);
         mySerial.println(" : debug  - loop");
         
      }
      
#endif
//---------------------------------------------------
 
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////



void ProcessSwipe()
{  
//      Serial.print(GestureData[0]); 
//      Serial.println(GestureData[1]); 
      
        
        if (GestureData[0] == 0x01) //swipe right
        {
  
          turnData = -((float)(40 - GestureData[1]) / 40.0);
          Serial.write(0xFE);  //Start Byte
          Serial.write(0x40);  //Command Code
          Serial.write("R"); //Right
          Serial.write(GestureData[1]); //Speed
          Serial.write(0xFF);     //end byte     
          
        } else if (GestureData[0] == 0x02) { //swipe left
        
          turnData = ((float)(40 - GestureData[1]) / 40.0);
          Serial.write(0xFE);  //Start Byte
          Serial.write(0x40);  //Command Code
          Serial.write("L"); //Left
          Serial.write(GestureData[1]); //Speed
          Serial.write(0xFF);  //End byte        
          
        }
      
}
   




void DebugOutput()
{

// //Control Loop timing. Note due to change of arduino's prescalers this will not display correctly
// Serial.print(time-timeold);
// Serial.print("\t");
   
// //Data comming in from second arduino and RC controller
// Serial.print(rpData);
// Serial.print("\t");
// Serial.print(turnData);
// Serial.print("\t");

 //Variables from Uprighting Sensors
// Serial.print(IRA);
// Serial.print("\t");
// Serial.print(IRB);
// Serial.print("\t");
// Serial.print(sound);
// Serial.print("\t");
//Serial.print("encoderLcount: ");
//Serial.print(encoderLcount);
//Serial.print("encoderRcount: ");
//Serial.print(encoderRcount);
//   
 //Variables from Complimentary Filter
// Serial.print(xAccel);
// Serial.print("\t");
// Serial.print(zAccel);
// Serial.print("\t");
// Serial.print(yGyro);	
//Serial.print("\t");
//Serial.print("Encoderd: ");
//Serial.print(Encoderd);
//Serial.print("\t");
   
//Serial.print("Acc*180/pi): ");   
//   Serial.print(acc*180/pi);
// Serial.print("\t");
// Serial.print((gyroHPold + dt*thetad)*180/pi);
//     Serial.print("\t");
//     Serial.print(theta*180/pi);
//   
// //Motor Input
//     Serial.print("\t");
//     Serial.println(u);
//   
//   LoopCount = false;
}





// param 1 u    : vitesse -1 to 1
// param 2 turn : vitesse differentiel  param1 - param2 = mot guach, param1 + param2 = mot droite  
// param 3 time : milliseconde 
void RC_Drive(float u, float turn, float time)
  {
    
    float MotorA = u + turn;    // turn/1.5 --> I'm just scaling the turn value coming from the RC Reciever
    float MotorB = u - turn;    // turn/1.5 --> I'm just scaling the turn value coming from the RC Reciever
    //Preventing input from being greater than 1. Saturating the input
    if(MotorA > 1){MotorA=1;}
    if(MotorB > 1){MotorB=1;}
    if(MotorA<-1){MotorA=-1;}
    if(MotorB<-1){MotorB=-1;}
    
    pinMode(APWM,OUTPUT);
    pinMode(BPWM,OUTPUT);
	
    //MotorA control Control (right motor when looking at it front)
    if( MotorA >= 0)
    {digitalWrite(AIn1,LOW); digitalWrite(AIn2,HIGH); analogWrite(APWM, MotorA*229.5);}
    else
    {digitalWrite(AIn1,HIGH); digitalWrite(AIn2,LOW); analogWrite(APWM,-MotorA*229.5);}  
    //MotorB control Control (left motor when looking at it from front)
    if( MotorB >= 0)
    {digitalWrite(BIn1,HIGH); digitalWrite(BIn2,LOW); analogWrite(BPWM, MotorB*229.5);}
    else
    {digitalWrite(BIn1,LOW); digitalWrite(BIn2,HIGH); analogWrite(BPWM,-MotorB*229.5);}  

    digitalWrite(stby,HIGH);
    
    delay (time);
    
    digitalWrite(stby,LOW);
    

  }
  

void serialEvent() {
  
  
  
//---------------DEBUG-------------------
#ifdef DEBUG
   mySerial.println("debug  - serialEvent 0");
#endif
//-----------------------------------------  
   
      

          int i= 0;
          // reading  from the serial port buffer
          while (Serial.available() && i < sizeof(buffer)) {
            // get the new byte:
            buffer[i] = Serial.read();
            i++;
            
          }
          newInput = true;
       
  
}

void mipHandler(){

  
 
//---------------DEBUG-------------------
#ifdef DEBUG
   mySerial.println("debug  - mipHandler 0");
#endif
//-----------------------------------------  
     
   // decoding the data 
          
          // we need the start byte (0xFE ) index,  
          int i = 0;
          while(buffer[i] != 0xFE)  i++; 
          byte cmd = buffer[i+1];
          byte arg1 = 0;
          byte arg2 = 0;
          byte arg3 = 0;
          byte arg4 = 0;

     switch(cmd){
      
      case 0x10 : // drive
                    
                    arg1 = buffer [i+2];
                    arg2 = buffer [i+3];
                    arg3 = buffer [i+4];
                    arg4 = buffer [i+5];
                    drive(arg1, arg2, arg3, arg4);
                    delay(500);
//---------------DEBUG-----------------------
#ifdef DEBUG
                  
                    mySerial.println("debug - serialEvent  1");
                    mySerial.print(" cmd: ");
                    mySerial.print(cmd, DEC);
                    mySerial.print(" args: ");
                    mySerial.print(arg1, DEC);
                    mySerial.print("\t");
                    mySerial.print(arg2, DEC);
                    mySerial.print("\t");
                    mySerial.print(arg3, DEC);
                    mySerial.print("\t");
                    mySerial.println(arg4, DEC);
                
#endif
//-----------------------------------------------
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
  
  
  
   float u = (float)speedL / 100;
   float turn = 0.5; 
   float time = distanceL * 100;
   
   
//---------------DEBUG-----------------------
#ifdef DEBUG
                    mySerial.println("debug - drive");
                    mySerial.print(" u: ");
                    mySerial.print(u, 6);
                    mySerial.print(" turn: ");
                    mySerial.print(turn, 6);
                    mySerial.print("\t");
                    mySerial.print(" time:");
                    mySerial.println(time, 6);                
#endif
//-----------------------------------------------
    RC_Drive(u, turn, time);
}
