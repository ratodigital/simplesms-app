package com.ratodigital.simplesms;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {
	
	public static final String SENDER = "539939418841";

	public static EditText editPassword;
	public static EditText editEmail;
	private Button registrar;
	AlertDialogManager alert = new AlertDialogManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		editPassword = (EditText) findViewById(R.id.senha);
		editEmail = (EditText) findViewById(R.id.email);
		registrar = (Button) findViewById(R.id.registrar);

		registrar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String password = editPassword.getText().toString();
				String email = editEmail.getText().toString();
				if (password.trim().length() > 0 && email.trim().length() > 0) {
					registerDevice(email, password);
				} else {
					alert.showAlertDialog(MainActivity.this,
							"Registration Error!", "Please enter your details",
							false);
				}
			}

		});

	}

	private void registerDevice(String email, String password) {
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			GCMRegistrar.register(this, SENDER);
		}else {
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				Toast.makeText(getApplicationContext(),
						"Already registered with GCM", Toast.LENGTH_LONG)
						.show();
				
			} else {
				registerOnSimplesmsServer(regId,email,password);
			}
		}

	}

	private void registerOnSimplesmsServer(String regId,String email, String password) {
		alert.showAlertDialog(MainActivity.this,
				"TODO", "Aqui seria o momento de submeter as informações ao server.",
				false);
		
		//Depois de registrar salvar no utilitário...
		GCMRegistrar.setRegisteredOnServer(this, true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
