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

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ratodigital.simplesms.service.JSONResponse;
import com.ratodigital.simplesms.service.Logar;

/**
 * Main UI for the demo app.
 */
public class RegisterActivity extends Activity {

	public static final String PREFS_EMAIL = "email";
	public static final String PREFS_PASSWORD = "password";
	public static final String PREFS_NAME = "name";
	protected static final String PREFS_LOG = "log";
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME = "onServerExpirationTimeMs";
	/**
	 * Default lifespan (7 days) of a reservation until it is considered
	 * expired.
	 */
	public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;
	/**
	 * You must use your own project ID instead.
	 */
	String SENDER_ID = "539939418841";

	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "GCM - SimpleSMS";
	
	private EditText editPassword;
	private EditText editEmail;
	private Button registrar;
	private EditText editName;
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	Context context;

	String regid;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		checkLogin();

		setContentView(R.layout.main);
		editPassword = (EditText) findViewById(R.id.senha);
		editEmail = (EditText) findViewById(R.id.email);
		editName = (EditText) findViewById(R.id.name);
		registrar = (Button) findViewById(R.id.buttonLimpar);
		
		SharedPreferences localData = getGCMPreferences(context);
		editName.setText(localData.getString(RegisterActivity.PREFS_NAME, null));
		editEmail.setText(localData.getString(RegisterActivity.PREFS_EMAIL, null));
		editPassword.setText(localData.getString(RegisterActivity.PREFS_PASSWORD, null));
		
		registrar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String name = editName.getText().toString();
				String password = editPassword.getText().toString();
				String email = editEmail.getText().toString();
				
				SharedPreferences localData = getGCMPreferences(context);
				SharedPreferences.Editor editor = localData.edit();
				editor.putString(RegisterActivity.PREFS_NAME, name);
				editor.putString(RegisterActivity.PREFS_PASSWORD, password);
				editor.putString(RegisterActivity.PREFS_EMAIL, email);
				editor.commit();
				
				String regId = getRegistrationId(context);

				if (name.trim().length() > 0 && password.trim().length() > 0
						&& email.trim().length() > 0) {
					registerDeviceOnSimpleSmsServer(name,email,regId,password);
				} else {
					Toast.makeText(getApplicationContext(), "Please enter your details", Toast.LENGTH_LONG)
					.show();
				}
			}
		});

		context = getApplicationContext();
		regid = getRegistrationId(context);
		if (regid.length() == 0) {
			registerBackground();
		}
		gcm = GoogleCloudMessaging.getInstance(this);
	}

	private void checkLogin() {
		final SharedPreferences prefs = getGCMPreferences(getApplicationContext());
		String nome = prefs.getString(RegisterActivity.PREFS_NAME, null);
		String email = prefs.getString(RegisterActivity.PREFS_EMAIL, null);
		String regId = prefs.getString(RegisterActivity.PROPERTY_REG_ID, null);
		if(nome!=null && email != null && regId !=null){
	        Intent i = new Intent(this, HomeActivity.class);
	        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        getApplicationContext().startActivity(i);
		}
	}

	/**
	 * Stores the registration id, app versionCode, and expiration time in the
	 * application's {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration id
	 */
	private void setRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.v(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		long expirationTime = System.currentTimeMillis()
				+ REGISTRATION_EXPIRY_TIME_MS;

		Log.v(TAG, "Setting registration expiry time to "
				+ new Timestamp(expirationTime));
		editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
		editor.commit();
	}

	/**
	 * Gets the current registration id for application on GCM service.
	 * <p>
	 * If result is empty, the registration has failed.
	 * 
	 * @return registration id, or empty string if the registration is not
	 *         complete.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.length() == 0) {
			Log.v(TAG, "Registration not found.");
			return "";
		}
		// check if app was updated; if so, it must clear registration id to
		// avoid a race condition if GCM sends a message
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion || isRegistrationExpired()) {
			Log.v(TAG, "App version changed or registration expired.");
			return "";
		}
		return registrationId;
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration id, app versionCode, and expiration time in the
	 * application's shared preferences.
	 */
	private void registerBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration id=" + regid;
					setRegistrationId(context, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Log.v(TAG, msg);
			}
		}.execute(null, null, null);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
		return getSharedPreferences(RegisterActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	/**
	 * Checks if the registration has expired.
	 * 
	 * <p>
	 * To avoid the scenario where the device sends the registration to the
	 * server but the server loses it, the app developer may choose to
	 * re-register after REGISTRATION_EXPIRY_TIME_MS.
	 * 
	 * @return true if the registration has expired.
	 */
	private boolean isRegistrationExpired() {
		final SharedPreferences prefs = getGCMPreferences(context);
		// checks if the information is not stale
		long expirationTime = prefs.getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME,
				-1);
		return System.currentTimeMillis() > expirationTime;
	}

	private void registerDeviceOnSimpleSmsServer(String name, String email, String regId, String password) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(RegisterActivity.PREFS_NAME, name);
		params.put(RegisterActivity.PREFS_EMAIL, email);
		params.put(RegisterActivity.PREFS_PASSWORD, password);
		params.put(RegisterActivity.PROPERTY_REG_ID, regId);
		try {
			@SuppressWarnings("unchecked")
			JSONResponse result = new Logar().execute(params).get();
			Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
			Log.v(TAG, result.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, "EX.: "+e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
}
