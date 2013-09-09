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

SoftwareSerial mySerial(10, 11); // RX, TX
  int GestureData[6];
int turnData = 0;

/////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////SETUP///////////////////////////////////////////////////
void setup()
{
  
  int error;
  uint8_t c;

  Serial.begin(115200);


 // set the data rate for the SoftwareSerial port
  mySerial.begin(9600);
  
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
 
}


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////Main Loop///////////////////////////////////////////////////////////  
void loop()
{
  //Serial.print("hi");
  int i=0;

 
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
            ProcessSwipe();

            i=0;
            return;
          }
        }   
      }
       
      
     
      
}
  
    RC_Drive(0.0, 0.5, 100);
    
    
    
    // param 1 : vitesse -1 to 1
    // param 2 : vitesse differentiel  param1 - param2 = mot guach, param1 + param2 = mot droite  
    // temps : miliseconde 

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
  

