////Attached Libraries
//#include "MPU6050dav.h"
//#include <Wire.h>
//
/////////////////////////////////////////////////////////////////////////////////////////
//// Declaring Variables for Accel
////USER NEEDS TO CHANGE DEPENDING ON ROBOT
//const float AccelSensitivity = 16384;       //AFS_SEL=0
////const float AccelSensitivity = 8192;      //AFS_SEL=1
//const float xAccelDir = -1;                 //Accounts for sensor orientation 1 or -1
//const float zAccelDir = 1;                  //Accounts for sensor orientation 1 or -1
//float AccelOffset= -5*pi/180;               //Makes the vertical zero degrees.
//float xAccel,zAccel;                        
//float xAccelPin, zAccelPin;
//float xAccelCount = 0;
//float zAccelCount = 0;              
//
//// Declaring Variables for Gyro
////USER NEEDS TO CHANGE DEPENDING ON ROBOT
//const float GyroSensitivity = 131;         //FS_SEL=0 131 LSB/(º/s) 
////const float GyroSensitivity = 65.5;      //FS_SEL=1 65.5 LSB/(º/s) 
//const float yGyroDir = 1;                  //Accounts for sensor orientation 1 or -1
//float yGyro;           
//float yGyroPin;
//float yGyroCount=0;
//float GyroOffset;                          //This is to make the vertical zero degrees. Will calibrate in setup()
//float acc_theta;
//float gyro_thetad;
//
//
/////////////////////////////////////////////////////////////////////////////////////////
////Initalize and Calbriate Digital IMU
//void Initialize_Digital_IMU()
//{
//  // Initialize the 'Wire' class for the I2C-bus.
//  int error;
//  uint8_t c;
//  Serial.begin(115200);
//  Wire.begin();
////////////////////////////////////////////////////
//  //IMU Settings should happen here default at power-up:
//  //    Gyro= 250 (º/s) , Accel= 2g, Clock = 8MHz, Device is in sleep mode.
//  error = MPU6050_read (MPU6050_WHO_AM_I, &c, 1);
//  // According to the datasheet, the 'sleep' bit should read a '1'. But I read a '0'. That bit has to be cleared, since the sensor
//  // is in sleep mode at power-up. Even if thebit reads '0'.
//  error = MPU6050_read (MPU6050_PWR_MGMT_2, &c, 1);
////  MPU6050_write_reg (MPU6050_ACCEL_CONFIG, MPU6050_AFS_SEL_4G);
////  MPU6050_write_reg (MPU6050_GYRO_CONFIG, MPU6050_FS_SEL1);
//  // Clear the 'sleep' bit to start the sensor.
//  MPU6050_write_reg (MPU6050_PWR_MGMT_1, 0);
////////////////////////////////////////////////////
// // Initialize sensors. Note: Accel Data comes as Radians and Gyro Data comes as degrees/s 
//  for(int x=0; x < 10; x++){ 
//    MPU_READ();
//    xAccelCount = xAccelCount + xAccelDir*(xAccelPin);
//    zAccelCount = zAccelCount + zAccelDir*(zAccelPin);
//    yGyroCount = yGyroCount + yGyroDir*(yGyroPin);
//  } 
//  xAccel = xAccelCount/10;
//  zAccel = zAccelCount/10;
//  yGyro = yGyroCount/10;
//  GyroOffset = -(yGyro);  
//  acc_theta = atan2(xAccel,zAccel) + AccelOffset ;
//  gyro_thetad = (yGyro + GyroOffset) * pi/180;  //we are trusting gyro for accurate thetadot value
//  Serial.println("IMU Engaged!");  
//}
//
/////////////////////////////////////////////////////////////////////////////////////////
////Get New Data From IMU
//void IMU_Update(){
////      MPU_Read();                      //Causes problems with timer interrupt when run within ISR
//   xAccel = xAccelDir*(xAccelPin);
//   zAccel = zAccelDir*(zAccelPin);
//   yGyro = yGyroDir*(yGyroPin);
//   
//   acc_theta = atan2(xAccel,zAccel) + AccelOffset ;
//   gyro_thetad = (yGyro + GyroOffset) * pi/180;  //we are trusting gyro for accurate thetadot value
//}
//
//
/////////////////////////////////////////////////////////////////////////////////////////
////Functions to get variables used in other functions
//float get_acc(){
//  return acc_theta;
//}
//float get_gyro_thetad(){
//  return gyro_thetad;
//}
//
/////////////////////////////////////////////////////////////////////////////////////////
////IMU Debugging Outputs
//void IMU_Outputs(){
//   Serial.print("\t");
//   Serial.print(xAccel);
//   Serial.print("\t");
//   Serial.print(zAccel);
//   Serial.print("\t");
//   Serial.print(yGyro);
//   Serial.print("\t");
//   Serial.print(acc_theta*180/pi);
//   Serial.print("\t");
//   Serial.print(gyro_thetad*180/pi);
//}
//
//
//
//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////Digital IMU Stuff////////////////////////////////////////////////////////////////////////////
//
//typedef union accel_t_gyro_union
//{
//  struct
//  {
//    uint8_t x_accel_h;
//    uint8_t x_accel_l;
//    uint8_t y_accel_h;
//    uint8_t y_accel_l;
//    uint8_t z_accel_h;
//    uint8_t z_accel_l;
//    uint8_t t_h;
//    uint8_t t_l;
//    uint8_t x_gyro_h;
//    uint8_t x_gyro_l;
//    uint8_t y_gyro_h;
//    uint8_t y_gyro_l;
//    uint8_t z_gyro_h;
//    uint8_t z_gyro_l;
//  } reg;
//  struct 
//  {
//    int x_accel;
//    int y_accel;
//    int z_accel;
//    int temperature;
//    int x_gyro;
//    int y_gyro;
//    int z_gyro;
//  } value;
//};
//
//
//// MPU6050_read
////
//// This is a common function to read multiple bytes from an I2C device. It uses the boolean parameter for Wire.endTransMission()to be able to hold or release the I2C-bus. 
//// Only this function is used to read. There is no function for a single byte.
//
//int MPU6050_read(int start, uint8_t *buffer, int size)
//{
//  int i, n, error;
//
//  Wire.beginTransmission(MPU6050_I2C_ADDRESS);
//  n = Wire.write(start);
//  if (n != 1)
//    return (-10);
//
//  n = Wire.endTransmission(false);    // hold the I2C-bus
//  if (n != 0)
//    return (n);
//
//  // Third parameter is true: relase I2C-bus after data is read.
//  Wire.requestFrom(MPU6050_I2C_ADDRESS, size, true);
//  i = 0;
//  while(Wire.available() && i<size)
//  {
//    buffer[i++]=Wire.read();
//  }
//  if ( i != size)
//    return (-11);
//
//  return (0);  // return : no error
//}
//
//
//// --------------------------------------------------------
//// MPU6050_write
////
//// This is a common function to write multiple bytes to an I2C device.
//// If only a single register is written, use the function MPU_6050_write_reg().
//// Parameters:
////   start : Start address, use a define for the register
////   pData : A pointer to the data to write.
////   size  : The number of bytes to write.
////
//// If only a single register is written, a pointer to the data has to be used, and the size is a single byte:   int data = 0; the data to write
////   MPU6050_write (MPU6050_PWR_MGMT_1, &c, 1);
//
//int MPU6050_write(int start, const uint8_t *pData, int size)
//{
//  int n, error;
//
//  Wire.beginTransmission(MPU6050_I2C_ADDRESS);
//  n = Wire.write(start);        // write the start address
//  if (n != 1)
//    return (-20);
//
//  n = Wire.write(pData, size);  // write data bytes
//  if (n != size)
//    return (-21);
//
//  error = Wire.endTransmission(true); // release the I2C-bus
//  if (error != 0)
//    return (error);
//
//  return (0);         // return : no error
//}
//
//// --------------------------------------------------------
//// MPU6050_write_reg: function to write a single register.
//// Just a wrapper around the MPU_6050_write() function
//// Only a convenient function to make it easier to write a single register.
//int MPU6050_write_reg(int reg, uint8_t data)
//{
//  int error;
//  error = MPU6050_write(reg, &data, 1);
//  return (error);
//}
//
//int MPU_READ (void)
//{
//  int error;
//  double dT;
//  accel_t_gyro_union accel_t_gyro;
//
//  // Read the raw values. Read 14 bytes at once, containing acceleration, temperature and gyro. With the default settings of the MPU-6050,
//  // there is no filter enabled, and the values are not very stable.
//  
//  error = MPU6050_read (MPU6050_ACCEL_XOUT_H, (uint8_t *) &accel_t_gyro, sizeof(accel_t_gyro));
//  // Swap all high and low bytes. After this, the registers values are swapped, so the structure name like x_accel_l does no longer contain the lower byte.
//  uint8_t swap;
//  #define SWAP(x,y) swap = x; x = y; y = swap
//
//  SWAP (accel_t_gyro.reg.x_accel_h, accel_t_gyro.reg.x_accel_l);
////  SWAP (accel_t_gyro.reg.y_accel_h, accel_t_gyro.reg.y_accel_l);
//  SWAP (accel_t_gyro.reg.z_accel_h, accel_t_gyro.reg.z_accel_l);
////  SWAP (accel_t_gyro.reg.t_h, accel_t_gyro.reg.t_l);
////  SWAP (accel_t_gyro.reg.x_gyro_h, accel_t_gyro.reg.x_gyro_l);
//  SWAP (accel_t_gyro.reg.y_gyro_h, accel_t_gyro.reg.y_gyro_l);
////  SWAP (accel_t_gyro.reg.z_gyro_h, accel_t_gyro.reg.z_gyro_l);
//  
//  
//  xAccelPin = float(accel_t_gyro.value.x_accel)/AccelSensitivity;
//  zAccelPin = float(accel_t_gyro.value.z_accel)/AccelSensitivity;
//  yGyroPin = accel_t_gyro.value.y_gyro / GyroSensitivity; 
//  return(0); 
//}
