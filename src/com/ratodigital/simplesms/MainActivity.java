package com.ratodigital.simplesms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {

	public static final String SENDER = "539939418841";
	public static final String PREFS_NAME = "SimpleSMS_Data";
	private static final String SERVE_URL_REGISTER = "http://simplesmserver.appspot.com/gcm/register";

	private EditText editPassword;
	private EditText editEmail;
	private Button registrar;
	AlertDialogManager alert = new AlertDialogManager();

	private EditText editName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		setContentView(R.layout.activity_main);

		editPassword = (EditText) findViewById(R.id.senha);
		editEmail = (EditText) findViewById(R.id.email);
		editName = (EditText) findViewById(R.id.name);
		registrar = (Button) findViewById(R.id.registrar);

		SharedPreferences localData = getSharedPreferences(PREFS_NAME, 0);
		editName.setText(localData.getString("name", null));
		editEmail.setText(localData.getString("email", null));
		editPassword.setText(localData.getString("password", null));
		
		if(localData.getString("regId", null)!=null){
			openHomeActivity();
		}

		registrar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String name = editName.getText().toString();
				String password = editPassword.getText().toString();
				String email = editEmail.getText().toString();

				SharedPreferences localData = getSharedPreferences(PREFS_NAME,
						0);
				SharedPreferences.Editor editor = localData.edit();
				editor.putString("name", name);
				editor.putString("password", password);
				editor.putString("email", email);
				editor.commit();

				if (name.trim().length() > 0 && password.trim().length() > 0
						&& email.trim().length() > 0) {
					registerDevice();
				} else {
					alert.showAlertDialog(MainActivity.this,
							"Registration Error!", "Please enter your details",
							false);
				}
			}
		});

	}

	private void registerDevice() {
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			GCMRegistrar.register(this, SENDER);
		} else {
			SharedPreferences localData = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = localData.edit();
			editor.putString("regId", regId);
			editor.commit();

			if (GCMRegistrar.isRegisteredOnServer(this)) {
				Toast.makeText(getApplicationContext(),
						"Already registered with GCM", Toast.LENGTH_LONG)
						.show();

			} else {
				registerOnSimplesmsServer();
			}
		}

	}

	private void registerOnSimplesmsServer() {
		SharedPreferences localData = getSharedPreferences(PREFS_NAME, 0);
		System.out.println(localData.getString("regId", "regiId vazio..."));

		String regId = localData.getString("regId", null);
		String name = localData.getString("name", null);
		String email = localData.getString("email", null);
		String password = localData.getString("password", null);

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(SERVE_URL_REGISTER);

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
			nameValuePairs.add(new BasicNameValuePair("regId", regId));
			nameValuePairs.add(new BasicNameValuePair("name", name));
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			String responseText = null;
			try {
				responseText = EntityUtils.toString(response.getEntity());
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			JSONObject json = new JSONObject(responseText);
			String status = json.getString("status");
			String msg = json.getString("message");
			if ("OK".equals(status.toUpperCase())) {
				// Depois de registrar salvar no utilit‡rio...
				GCMRegistrar.setRegisteredOnServer(this, true);
				openHomeActivity();

			} else {
				GCMRegistrar.setRegisteredOnServer(this, false);
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG)
						.show();
			}

		} catch (ClientProtocolException e) {
			Toast.makeText(getApplicationContext(),
					"Falha ao registrar no servidor", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(),
					"Falha ao registrar no servidor", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(),
					"Falha ao registrar no servidor", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

	}

	private void openHomeActivity() {
		Intent myIntent = new Intent(this, HomeActivity.class);
		startActivityForResult(myIntent, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
