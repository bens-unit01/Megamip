///////Program written by Saam Ostovari at the UCSD Coordinated Robotics Lab///////////
///////////////////////////////////NDA SD2013-802//////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////MAIN MIP PROGRAM/////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////

//Attached Libraries
#include <avr/io.h>
#include <avr/interrupt.h>
#include <math.h>
#include <bluetooth.h>

///////////////////////////////////////////////////////////////////////////////////////
// Declaring General Variables
float dt;                              
const float pi = 3.1415926535;     

////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////SETUP//////////////////////////////////////////////
void setup(){
  Serial.begin(115200);
//  Initialize_Digital_IMU();          //Digital IMU. Comment if using Analog IMU
  Initialize_IMU();                    //Analog IMU. Comment if using Digital IMU
  Initialize_Encoders();
  Initialize_Motors(); 
  Initialize_Estimator(); 
  Initialize_Controller();
  //  Initialize_Arduino_Hard_Reset();
  Initialize_Bluetooth();              //Comment if not using bluetooth
  Initialize_Faster_PWM_Freq();        //Micros() command will not display time correctly when this is initalized
//  Initialize_2ms_Timer_Interrupt();  //Set timer interrupt (control loop) to 2ms
  Initialize_4ms_Timer_Interrupt();    //Set timer interrupt (control loop) to 4ms
  MotorOn();                           //Take motors out of standby mode
}

////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////Interrupt Service Routine////////////////////////////////////
//The Interrupt Service Routine gets called everytime the timer interrupt is trigge

ISR(TIMER1_COMPA_vect)
{
  Complementary_Filter_Estimator();     
  Safety_Checks();                      //Comment this line to remove fall and pick up safeties
  SLC_Control();                        //Controller Method: Either SLC Control or LQR Control. Comment out one       
  //  LQR_Control();  
  RC_Drive();                           //Command motors with new inputs from controller
}
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////Main Loop///////////////////////////////////////////
void loop(){
//  MPU_READ();                //Uncomment line when using Digital IMU. Comment when using Analog IMU
  Bluetooth_Update();          //Gets new bluetooth commands  

/////Prints debugging outputs for each of the functions.
//  Bluetooth_Outputs();        
//  Encoder_Outputs();
  IMU_Outputs();
  Serial.println("-");
 // Estimator_Outputs();
//  RC_Outputs();
 // Controller_Outputs();
}

