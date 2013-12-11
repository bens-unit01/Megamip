//Defining Pins for Encoders
//USER NEEDS TO CHANGE DEPENDING ON ROBOT
#define EncoderLPinA 2        // Left Encoder Pin A
#define EncoderLPinB 4        // Left Encoder Pin B
#define EncoderRPinA 3        // Right Encoder Pin A
#define EncoderRPinB 9        // Right Encoder Pin B

///////////////////////////////////////////////////////////////////////////////////////
// Declaring Encoder Variables
//USER NEEDS TO CHANGE DEPENDING ON ROBOT
const float CountsPerRev = 200;              //Encoder Count per every 1 revolution of the wheel
const float CountsPerTick = 2;               //Depends if external interrupt is counting falling,rising or both. CHANGE =2,FALLING=1, RISING=1
const float GearRatio = 1;                   // Equals 1 if encoder is on placed after gear reduction from motor. Equals the motors gear ratio if on Motor's output shaft before gear reduction
float Ticks2Rads =2*pi/(CountsPerRev*CountsPerTick*GearRatio);  

float encoderLcount = 0;
float encoderRcount = 0;
float Encoder = 0;

///////////////////////////////////////////////////////////////////////////////////////
//Initialize Encoders
void Initialize_Encoders(){
  pinMode(EncoderLPinA, INPUT); pinMode(EncoderLPinB, INPUT);   // Will use External Interrupt 0 (pin 2) 
  pinMode(EncoderRPinA, INPUT); pinMode(EncoderRPinB, INPUT);   // Will use External Interrupt 1 (pin 3)
  digitalWrite(EncoderLPinA, LOW); digitalWrite(EncoderLPinB, LOW);
  digitalWrite(EncoderRPinA, LOW); digitalWrite(EncoderRPinB, LOW);
  attachInterrupt(0, DecoderLeft, CHANGE);     // encoder trips interrupt on change in state of pin 2
  attachInterrupt(1, DecoderRight, CHANGE);     // encoder trips interrupt on change in state of pin 3 
  Serial.println("Encoders Engaged!");  
}

///////////////////////////////////////////////////////////////////////////////////////
//Functions for external interrupts and counting encoder ticks
void DecoderLeft() //Note: not accounting for integer overload. Left Wheel when looking at Mip from Front
{
  //if Encoder channels read the same then count up. If they are not the same, count down
  if (digitalRead(EncoderLPinA)== digitalRead(EncoderLPinB))
    {encoderLcount -= Ticks2Rads;}   
  else{encoderLcount += Ticks2Rads;}      
}
void DecoderRight()  //Note: not accounting for integer overload.
{
  //if Encoder channels read the same then count down. If they are not the same, count up
  if (digitalRead(EncoderRPinA)== digitalRead(EncoderRPinB))
    {encoderRcount -= Ticks2Rads;}   
  else{encoderRcount += Ticks2Rads;}      
}

///////////////////////////////////////////////////////////////////////////////////////
//Update encoder variable with new encoder values 
float Encoder_Update(){
  Encoder = (encoderLcount+encoderRcount)/2;  //using the average of the left and right encoder values in the estimator
  return Encoder;
}

///////////////////////////////////////////////////////////////////////////////////////
//Reset Encoder variables for safeties
void reset_Encoder(){
    encoderLcount=0; encoderRcount=0; Encoder = 0;
}

///////////////////////////////////////////////////////////////////////////////////////
//Encoder Debugging Outputs
void Encoder_Outputs(){
   Serial.print(encoderLcount);
   Serial.print("\t");
   Serial.print(encoderRcount);
   Serial.print("\t");
   Serial.print(Encoder);
}

