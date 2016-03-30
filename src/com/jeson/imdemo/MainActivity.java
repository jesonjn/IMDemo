package com.jeson.imdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jeson.imsdk.WebIMService.IloginListener;

public class MainActivity extends Activity {
	private EditText userNameET;
	private EditText passEt;
	private Button loginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		userNameET = (EditText) findViewById(R.id.main_username_et);
		passEt = (EditText) findViewById(R.id.main_password_et);
		loginButton = (Button) findViewById(R.id.main_login_bt);
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				((IMApplication) getApplication()).getWebSDKIBinner().login(
						userNameET.getText().toString(),
						passEt.getText().toString(), new IloginListener() {

							@Override
							public boolean loginFail(final String error) {
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										Toast.makeText(getApplication(), error,
												1).show();

									}
								});
								return true;
							}

							@Override
							public boolean loginSuccess() {
								Intent intent = new Intent(MainActivity.this,
										ChatActivity.class);
								startActivity(intent);
								return true;
							}

						});
			}
		});

	}
}
