///////Program written by Saam Ostovari at the UCSD Coordinated Robotics Lab///////////
///////////////////////////////////NDA SD2013-802//////////////////////////////////////
//Attached Libraries
#include <SoftwareSerial.h>


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
  

}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////



void ProcessSwipe()
{  
   switch (GestureData[0]) //swipe right
        {
        
           case  0x01:  //  Serial.print("Right ");
                turnData = -((float)(40 - GestureData[1]) / 40.0);
                Serial.write(0xFE);  //Start Byte
                Serial.write(0x40);  //Command Code
                Serial.write("R"); //Right
                Serial.write(GestureData[1]); //Speed
                Serial.write(0xFF);     //end byte 
                break; 
    
              
            case 0x02: // Serial.print("Left ");
                 turnData = ((float)(40 - GestureData[1]) / 40.0);
                 Serial.write(0xFE);  //Start Byte
                 Serial.write(0x40);  //Command Code
                 Serial.write("L"); //Left
                 Serial.write(GestureData[1]); //Speed
                 Serial.write(0xFF);  //End byte
                 break;
            
            case 0x03: // Serial.print("Hold ");
                 Serial.write(0xFE);  //Start Byte
                 Serial.write(0x40);  //Command Code
                 Serial.write("H"); //Left
                 Serial.write(GestureData[1]); //Speed
                 Serial.write(0xFF);  //End byte
                  break;
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

