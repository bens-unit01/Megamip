/*
  bluetooth.h -Library for easy control of MAE143c myMIP toys - Version 1
  Copyright (c) 2012 Novus Design Solutions.  All right reserved.
  Author: James Strawson

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.
  
*/
#ifndef bluetooth_h
#define bluetooth_h

#include "Arduino.h"

class bluetooth{

public:
  bluetooth();
  bool refresh();
  float getRegister(int address);
  void setRegister(int address, float value);
  float getForwardSpeed();
  float getTurnRate();
  float getAux1();
  float getAux2();
  float getAux3();
};

#endif