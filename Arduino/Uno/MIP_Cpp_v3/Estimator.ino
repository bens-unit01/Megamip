// Declaring Variables for Complimentary Filter Estimator
const int theta_mix_Tc = 2;  //time constant 2.6
float SensorData[5];
float accLPOld;
float gyroHPold;
float gyroHP;
float theta;
float thetad;
float phi;
float phid;
float acc;
float accLP;
float Encoderd = 0;
float EncoderOld[3] = {0,0,0};

///////////////////////////////////////////////////////////////////////////////////////
//Initialize Estimator
void Initialize_Estimator(){
  acc = get_acc();
  thetad = get_thetad();
  theta = acc;
  SensorData[0] = acc;
  SensorData[1] = thetad;
  Serial.println("Estimator Engaged!");  
} 

///////////////////////////////////////////////////////////////////////////////////////
//Complementary filter used to translate sensor data into data for controllers and balancing
float Complementary_Filter_Estimator()
  {
  //Reading Accel and Gyro data and putting it into terms of g.
  IMU_Update();  
  //Setting up values from previous run/initial condtions
  accLPOld = SensorData[0]; gyroHPold = SensorData[1]; EncoderOld[0]= SensorData[2]; EncoderOld[1]= SensorData[3]; EncoderOld[2]= SensorData[4];
  //Getting encoder value gathered by external interrupts
  Encoder = Encoder_Update();
  //Weighted moving average for encoder velocity
  Encoderd =( 4*(Encoder-EncoderOld[0])/dt + 2*(Encoder-EncoderOld[1])/(2*dt) + (Encoder-EncoderOld[2])/(3*dt) ) / (7); //using the average of the left and right encoder values in the estimator
  //Setting up the Low-Pass and High-Pass filters. theta_mix_Tc is the time constant
  float HighPassConst = theta_mix_Tc / (theta_mix_Tc + dt);   
  float LowPassConst = dt / (theta_mix_Tc + dt);
  //Filtering Accel Data
  acc = get_acc();  
  accLP = accLPOld + LowPassConst * (acc - accLPOld);
  //Filtering Gyro Data
  thetad = get_gyro_thetad();  //we are trusting gyro for accurate thetadot value
  gyroHP = HighPassConst*(gyroHPold + dt*thetad);
  //Combining Filtered Data
  theta = accLP + gyroHP;
  //Calculating phi using Encoders and calculated theta/thetad
  phi = Encoder + theta;
  phid = Encoderd + thetad;
  //Saving variables for next run. To save memory and what not it is not neccasary to have SensorData variables but it makes it easier to follow the code.
  SensorData[0] = accLP; SensorData[1] = gyroHP; SensorData[2] = Encoder; SensorData[3] = EncoderOld[0]; SensorData[4] = EncoderOld[1];
  }

///////////////////////////////////////////////////////////////////////////////////////
//Functions to get variables used in other functions
float get_theta(){return theta;}
float get_thetad(){return thetad;}
float get_phi(){return phi;}
float get_phid(){return phid;}
float get_Encoderd(){return Encoderd;}

///////////////////////////////////////////////////////////////////////////////////////
// Reset Estimator values for safeties
void reset_Estimator(){
  phi=0;
}

///////////////////////////////////////////////////////////////////////////////////////
//Outputs from Estimator
void Estimator_Outputs(){
   Serial.print("\t");
   Serial.print(theta*180/pi);
}
