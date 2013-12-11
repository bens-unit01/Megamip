// Declaring Variables for Fall Detection
boolean Fallen = false;

//////////////////////////////////////////////////////////////////////////////
//Resets all variables when MIP ha
void FallDetection()
{
  Fallen=true;
  reset_Encoder();
  reset_Estimator();
  reset_Controller();
 
 //Fallen makes it so that if MIP has fallen then it won't start balancing till it is lifted back near vertical 
  if(get_theta() <= 10*pi/180 && get_theta() >= -10*pi/180){Fallen = false;}
}

//////////////////////////////////////////////////////////////////////////////
//Function to get variables used in other functions
boolean get_Fallen(){
  return Fallen;
}






