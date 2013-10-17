package com.megamip.util;




/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

//We moved everything to CordovaActivity


import org.apache.cordova.CordovaActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;


public class DroidGap extends CordovaActivity {

	public static final int USER_MOBILE  = 0;
	public static final int USER_DESKTOP = 1;
		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			super.loadUrl("file:///android_asset/www/index.html");
			WebSettings settings = appView.getSettings();
			settings.setUserAgent(USER_DESKTOP);
			//settings.setPluginState(PluginState.ON); 
		}
}

