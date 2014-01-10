//Defining Pins for IMU
#define xAccelPin A0
#define zAccelPin A1
#define yGyroPin A2

///////////////////////////////////////////////////////////////////////////////////////
// Declaring Variables for Accel
//USER NEEDS TO CHANGE DEPENDING ON ROBOT
//const float Vref = 5.002;  
const float Vref = 4.91; 
const float AccelSensitivity = 0.8;
float VxAccelZeroG = 1.50; //1.592459016;            //Accel Zero Gravity Voltage in y
float VzAccelZeroG = 1.54; // 1.343147541;            //Accel Zero Gravity Voltage in z
//float VzAccelZeroG = 2.271142697;
const float xAccelDir = -1;                  //Accounts for sensor orientation 1 or -1
const float zAccelDir = 1;                   //Accounts for sensor orientation 1 or -1
float AccelOffset= -1*pi/180;                 //Makes the vertical zero degrees.

float xAccel,zAccel;                         
float xAccelCount = 0;
float zAccelCount = 0;              

// Declaring Variables for Gyro
//USER NEEDS TO CHANGE DEPENDING ON ROBOT
const float GyroSensitivity = 0.002;
float VyGyroZeroG = 1.255; //1.236745152;            //Gyro Zero rotation Voltage in y
const float yGyroDir = 1;                   //Accounts for sensor orientation 1 or -1

float yGyro;                                
float yGyroCount=0;
float GyroOffset;                           //This is to make the vertical zero degrees. Will calibrate in setup()
float acc_theta;
float gyro_thetad;

///////////////////////////////////////////////////////////////////////////////////////
//Initalize and calbriate Analog IMU
void Initialize_IMU()
{
  pinMode(xAccelPin, INPUT); pinMode(zAccelPin, INPUT);
  pinMode(yGyroPin, INPUT);
 
 // Initialize sensors. Note: Accel Data comes as Radians and Gyro Data comes as degrees/s 
  for(int x=0; x < 500; x++)
  { 
    xAccelCount = xAccelCount + xAccelDir*(analogRead(xAccelPin)*Vref/1023 - VxAccelZeroG)/(AccelSensitivity);
    zAccelCount = zAccelCount + zAccelDir*(analogRead(zAccelPin)*Vref/1023 - VzAccelZeroG)/(AccelSensitivity);
    yGyroCount = yGyroCount + yGyroDir*(analogRead(yGyroPin)*Vref/1023 - VyGyroZeroG)/(GyroSensitivity);
  } 
  xAccel = xAccelCount/500;
  zAccel = zAccelCount/500;
  yGyro = yGyroCount/500;
  GyroOffset = -(yGyro);  
  acc_theta = atan2(xAccel,zAccel) + AccelOffset ;
  gyro_thetad = (yGyro + GyroOffset) * pi/180;  //we are trusting gyro for accurate thetadot value
  Serial.println("IMU Engaged!");  
}

///////////////////////////////////////////////////////////////////////////////////////
//Get New Data From IMU
void IMU_Update(){
   xAccel = xAccelDir*(analogRead(xAccelPin)*Vref/1023 - VxAccelZeroG)/(AccelSensitivity);
   zAccel = zAccelDir*(analogRead(zAccelPin)*Vref/1023 - VzAccelZeroG)/(AccelSensitivity);
   yGyro = yGyroDir*(analogRead(yGyroPin)*Vref/1023 - VyGyroZeroG)/(GyroSensitivity);
   
   acc_theta = atan2(xAccel,zAccel) + AccelOffset ;
   gyro_thetad = (yGyro + GyroOffset) * pi/180;  //we are trusting gyro for accurate thetadot value
}

///////////////////////////////////////////////////////////////////////////////////////
//Functions to get variables used in other functions
float get_acc(){
  return acc_theta;
}
float get_gyro_thetad(){
  return gyro_thetad;
}

///////////////////////////////////////////////////////////////////////////////////////
//IMU Debugging Outputs
void IMU_Outputs(){
   Serial.print("\t");
   Serial.print(xAccel);
   Serial.print("\t");
   Serial.print(zAccel);
   Serial.print("\t");
   Serial.print(yGyro);
   Serial.print("\t");
   Serial.print(acc_theta*180/pi);
   Serial.print("\t");
   Serial.print(gyro_thetad*180/pi);
}
