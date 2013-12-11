// Declaring Variables for Successive Loop Closure
float ep[2]={0,0};
float et[3]={0,0,0};
float rp=0;
float rt[2] ={0,0};
float u[3] ={0,0,0};

///////////////////////////////////////////////////////////////////////////////////////////////////////
//Initialize Controller for Balancing
void Initialize_Controller(){
  ep[1]=0;
  et[1]=-get_theta();
  et[2]=-get_theta();
  Serial.println("Controller Engaged!");  
} 

///////////////////////////////////////////////////////////////////////////////////////////////////////
//Succesive Loop Closure Control Update for Balancing
//USER NEEDS TO CHANGE DEPENDING ON ROBOT
void SLC_Control(){
 if (dt == 0.002){
    rp = rp + get_Forward();                    //Driving forward by changing phi's equilibrium position 
    float p= 1/1.18; //prescaler
    ep[0] = rp-phi;
    rt[0] = (0.24454)*ep[0] + (-0.24431)*ep[1] - (-0.97916)*rt[1];
    et[0] = p*rt[0]-theta;
    u[0] = (-88.3823)*et[0] + (175.3592)*et[1] + (-86.9791)*et[2] - (-1.8065)*u[1] - (0.80645)*u[2];
    ep[1]=ep[0];  et[2]=et[1];  et[1]=et[0];  rt[1]=rt[0];  u[2]=u[1];  u[1]=u[0];   //Saving variables for next run through controls
  }
  else if (dt == 0.004){
    rp = rp + get_Forward();
    float p= 1/1.18; //prescaler
    ep[0] = rp-phi;
    rt[0] = (0.24223)*ep[0] + (-0.24178)*ep[1] - (-0.95876)*rt[1];
    et[0] = p*rt[0]-theta;
    u[0]  = (-81.2181)*et[0] + (159.8679)*et[1] + (-78.6576)*et[2] - (-1.647)*u[1] - (0.64683)*u[2];
    ep[1]=ep[0];  et[2]=et[1];  et[1]=et[0];  rt[1]=rt[0];  u[2]=u[1];  u[1]=u[0];   //Saving variables for next run through controls
  }
  else if (dt == 0.005){
  //No Controller design for 5ms yet
  } 
}

///////////////////////////////////////////////////////////////////////////////////////////////////////
//LQR Controller for Balancing
//USER NEEDS TO CHANGE DEPENDING ON ROBOT
void LQR_Control()
{
  if (dt == 0.002){
    float K1 = 0.069776; float K2 = 3.3979; float K3 = 0.090504; float K4 = 0.70583; 
    u[0]= (K1*phi+K2*theta+K3*phid+K4*thetad);
  }
  else if (dt == 0.004){
     //No Controller design for 4ms yet
    }
  else if (dt == 0.005){
    //No Controller design for 5ms yet
  }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////
//Functions to get variables used in other functions
float get_u(){
  return u[0];
}

///////////////////////////////////////////////////////////////////////////////////////////////////////
//Reset Controller Variables for Safeties
void reset_Controller(){
  u[0]=0;
  u[1]=0;
  u[2]=0;
  rp=0;
};

///////////////////////////////////////////////////////////////////////////////////////////////////////
//Reset Phi variable for safeties
void reset_refPhi(){
  rp = 0;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////
// Controller Debudding Outputs
void Controller_Outputs(){
//   Serial.print();
//   Serial.print("\t");
//   Serial.print();
//----------  DEBUG BEGIN--------------
#ifdef DEBUG
  Serial.print(" Controller_Outputs u[0]: ");
#endif
//------------DEBUG END-------------  
   Serial.print("\t");
   Serial.println(u[0]);
}
