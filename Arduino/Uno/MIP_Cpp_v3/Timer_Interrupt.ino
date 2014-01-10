//Setting Timer Interrupt/ Control loop to 2milliseconds
void Initialize_2ms_Timer_Interrupt(){ 
 // Initialize Timer1 Interrupt for 2ms. Change prescaler to 256 and OCR1A to 499 for 8ms
  dt = 0.002;
  cli();          // disable global interrupts
  TCCR1A = 0;     // set entire TCCR1A register to 0
  TCCR1B = 0;     // set entire TCCR1A register to 0
  OCR1A = 499;  // set compare match register to desired timer count:[ (clock_speed / Prescaler_value) * Desired_time_in_Seconds ] - 1
  TCCR1B |= (1 << WGM12);   // turn on CTC mode:
  TCCR1B |= (1 << CS10);    // Set CS12 bits for 64 prescaler:
  TCCR1B |= (1 << CS11);    // Set CS12 bits for 64 prescaler:
//  TCCR1B |= (1 << CS12);    // Set CS12 bits for 256 prescaler:
  TIMSK1 |= (1 << OCIE1A);  // enable timer compare interrupt:
 
// enable global interrupts:
 sei();
 Serial.println("Timer Interrupt Engaged!");  
}

///////////////////////////////////////////////////////////////////////////////////////////////////////
//Setting Timer Interrupt/ Control loop to 4milliseconds
void Initialize_4ms_Timer_Interrupt(){ 
 dt = 0.004;
 // Initialize Timer1 Interrupt for 2ms. Change prescaler to 256 and OCR1A to 499 for 8ms
  cli();          // disable global interrupts
  TCCR1A = 0;     // set entire TCCR1A register to 0
  TCCR1B = 0;     // set entire TCCR1A register to 0
  OCR1A = 999;  // set compare match register to desired timer count:[ (clock_speed / Prescaler_value) * Desired_time_in_Seconds ] - 1
  TCCR1B |= (1 << WGM12);   // turn on CTC mode:
  TCCR1B |= (1 << CS10);    // Set CS12 bits for 64 prescaler:
  TCCR1B |= (1 << CS11);    // Set CS12 bits for 64 prescaler:
//  TCCR1B |= (1 << CS12);    // Set CS12 bits for 256 prescaler:
  TIMSK1 |= (1 << OCIE1A);  // enable timer compare interrupt:
 
// enable global interrupts:
 sei();
 Serial.println("Timer Interrupt Engaged!");  
}

///////////////////////////////////////////////////////////////////////////////////////////////////////
//Setting Timer Interrupt/ Control loop to 5milliseconds
void Initialize_5ms_Timer_Interrupt(){ 
 dt = 0.005;
 // Initialize Timer1 Interrupt for 2ms. Change prescaler to 256 and OCR1A to 499 for 8ms
  cli();          // disable global interrupts
  TCCR1A = 0;     // set entire TCCR1A register to 0
  TCCR1B = 0;     // set entire TCCR1A register to 0
  OCR1A = 1249;  // set compare match register to desired timer count:[ (clock_speed / Prescaler_value) * Desired_time_in_Seconds ] - 1
  TCCR1B |= (1 << WGM12);   // turn on CTC mode:
  TCCR1B |= (1 << CS10);    // Set CS12 bits for 64 prescaler:
  TCCR1B |= (1 << CS11);    // Set CS12 bits for 64 prescaler:
//  TCCR1B |= (1 << CS12);    // Set CS12 bits for 256 prescaler:
  TIMSK1 |= (1 << OCIE1A);  // enable timer compare interrupt:
 
// enable global interrupts:
 sei();
 Serial.println("Timer Interrupt Engaged!");  

}

///////////////////////////////////////////////////////////////////////////////////////////////////////
//Initialize Faster PWM Frequency to Quiet Motors
void Initialize_Faster_PWM_Freq(){
  //Change PWM Freq to 31.25khz to quiet motors. Note: Time will not display correctly 
  TCCR0A = _BV(COM0A1) | _BV(COM0B1) | _BV(WGM00); 
  TCCR0B = _BV(CS00); 
}

///////////////////////////////////////////////////////////////////////////////////////////////////////
//Functions to get variables used in other functions
float get_dt(){
  return dt;
}
