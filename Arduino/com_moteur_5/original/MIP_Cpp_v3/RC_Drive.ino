//Defining Pins for Motors
//USER NEEDS TO CHANGE DEPENDING ON ROBOT
#define APWM 5    
#define AIn2 11  
#define AIn1 10  
#define BIn1 7   
#define BIn2 8     
#define BPWM 6   
#define stby 13  

///////////////////////////////////////////////////////////////////////////////////////////////////////
// Declaring Variables for Motors
const float MaxPWM = 216.75;   //Maximum PWM  can be set to 0-255.
float MotorA;
float MotorB;

///////////////////////////////////////////////////////////////////////////////////////////////////////
//Initialize Motors. Motors will be in standby mode 
void Initialize_Motors(){
  //Pin for Motors
  pinMode(APWM,OUTPUT); pinMode(AIn1,OUTPUT); pinMode(AIn2,OUTPUT);
  pinMode(BPWM,OUTPUT); pinMode(BIn1,OUTPUT); pinMode(BIn2,OUTPUT);
  pinMode(stby,OUTPUT);
   // Initialize Motors to off Position
  MotorOff();
  digitalWrite(AIn1,LOW); digitalWrite(AIn2,LOW); digitalWrite(APWM,HIGH);
  digitalWrite(BIn1,LOW); digitalWrite(BIn2,LOW); digitalWrite(BPWM,HIGH);
  Serial.println("Motors Engaged!");    
} 

///////////////////////////////////////////////////////////////////////////////////////////////////////
//Function to turn off and on Standby mode
void MotorOn(){  
  digitalWrite(stby,HIGH);          // Take motors out of Standby mode. 
}

void MotorOff(){  
  digitalWrite(stby,LOW);           // Put motors in Standby mode. 
}

///////////////////////////////////////////////////////////////////////////////////////////////////////
//Command motors to spin depending on controller and RC inputs
void RC_Drive()
  {
    MotorA = get_u() + get_Turn();
    MotorB = get_u() - get_Turn();
    //Preventing input from being greater than 1. Saturating the input
    if(MotorA > 1){MotorA=1;}
    if(MotorB > 1){MotorB=1;}
    if(MotorA<-1){MotorA=-1;}
    if(MotorB<-1){MotorB=-1;}
    //MotorA control Control (right motor when looking at it front)
    if( MotorA >= 0)
    {digitalWrite(AIn1,LOW); digitalWrite(AIn2,HIGH); analogWrite(APWM, MotorA*MaxPWM);}
    else
    {digitalWrite(AIn1,HIGH); digitalWrite(AIn2,LOW); analogWrite(APWM,-MotorA*MaxPWM);}  
    //MotorB control Control (left motor from front)
    if( MotorB >= 0)
    {digitalWrite(BIn1,HIGH); digitalWrite(BIn2,LOW); analogWrite(BPWM, MotorB*MaxPWM);}
    else
    {digitalWrite(BIn1,LOW); digitalWrite(BIn2,HIGH); analogWrite(BPWM,-MotorB*MaxPWM);}    
  }
 
///////////////////////////////////////////////////////////////////////////////////////////////////////
// RC Debugging Outputs
void RC_Outputs(){
   Serial.print("\t");
   Serial.print(MotorA);
}
