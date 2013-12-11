//Defining Pins for Serial data
#define txPin 1
#define rxPin 0

///////////////////////////////////////////////////////////////////////////////
//Create Object for the RC Controls and declaring variables
  char BluetoothData[6] ;
  float BlueIntData[6];
  const int BlueLoopMax = 6;
  long BlueLoop = 0;
  float turn=0;
  float forward=0;
  float balance;
  float forwardData=0;
  int i =0;
  float scaleTurn = 1500;
  float scaleForward=5000;

///////////////////////////////////////////////////////////////////////////////
//Initialize Bluetooth
void Initialize_Bluetooth(){
//  Serial.begin(115200);    // HC-07 BT modules in lab are set to 115200baud
}

///////////////////////////////////////////////////////////////////////////////
//Function to update Bluetooth values with new one coming in from device
void Bluetooth_Update(){
//Bluetooth Control

  if (Serial.available() ) 
  {
 // buffer initialization 
 for(int i = 0; i < 7; i++){
   buffer[i] = 0;
 }
 Serial.println(" if --");
    i=0;
    int j = 0; 
    byte buffer2[200];
    Serial.flush();
  boolean found = false;
    while(Serial.available() && !found){
       // BluetoothData[i] = Serial.read();
        buffer[i] = Serial.read();
  
 
    //    BlueIntData[i] = float(BluetoothData[i]);
      /*  if (BluetoothData[0] != 254){
          Serial.flush();
          i=0;
          return;
        }*/
        if(buffer[i] != 0){
          buffer2[j] = buffer[i];
          j++;
        }
        if(buffer[i] == 255){
          found = true;
        }
        i++;
        if (i>6){
          Serial.flush();
          i=0;
          //return;
        }
        
    }
    

    //-------------------------------
    
     for(int i = 0; i < 7; i++){
      Serial.print(i);Serial.print("- ");Serial.println(buffer2[i]);
     }
    
    //-------------------------------
    mipHandler();
  }
  else{return;}
}

//////////////////////////////////////////////////////////////////////////////
//Translating Bluetooth signal into turn command 
/*float get_Turn(){
  //turn = BlueIntData[1]/scaleTurn;
  return turn;
}
*/
///////////////////////////////////////////////////////////////////////////////
//Translating Bluetooth signal into driving forward and backward command 
/*float get_Forward(){
//  forward = BlueIntData[2]/scaleForward;
  return forward;
}
*/
///////////////////////////////////////////////////////////////////////////////
//Bluetooth Debugging Outputs
void Bluetooth_Outputs(){
 //  Serial.print(BlueIntData[1]);  //Check channel, should be 125 if running
   Serial.print("\t");
   Serial.print(turn);  // Turn Channel which is x direction on tablet
   Serial.print("\t");
  // Serial.print( BlueIntData[2]);  // Forward Channel which is y direction on tablet
   Serial.print("\t");
   Serial.println(forward);  // Trim on bottom of tablet
//   Serial.print("\t");
//   Serial.print(BlueIntData[4]);  // Trim on top of tablet
//   Serial.print("\t");
//   Serial.println(BlueIntData[5]);
}
