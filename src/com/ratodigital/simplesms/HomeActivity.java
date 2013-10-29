/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ratodigital.simplesms;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Main UI for the demo app.
 */
public class HomeActivity extends Activity {

	static final String TAG = "GCM - SimpleSMS";
	private TextView name;
	private TextView email;
	private TextView log;
	private Button limpar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.home);
		
		name = (TextView) findViewById(R.id.textViewNome);
		email = (TextView) findViewById(R.id.TextViewEmail);
		log = (TextView) findViewById(R.id.textViewLog);
		limpar = (Button) findViewById(R.id.buttonLimpar);
		
		SharedPreferences localData = getSharedPreferences(RegisterActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
		name.setText(localData.getString(RegisterActivity.PREFS_NAME, ""));
		email.setText(localData.getString(RegisterActivity.PREFS_EMAIL, ""));
		
		localData.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences prefs, String name) {
				if(name.equals(RegisterActivity.PREFS_LOG)){
					log.setText(prefs.getString(RegisterActivity.PREFS_LOG, ""));
				}
			}
		});
		
		
		limpar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SharedPreferences localData = getSharedPreferences(RegisterActivity.class.getSimpleName(),
						Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = localData.edit();
				editor.putString(RegisterActivity.PREFS_LOG, "...\n");
				editor.commit();
				log.setText("");
			}
		});
	}

	

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	
}
