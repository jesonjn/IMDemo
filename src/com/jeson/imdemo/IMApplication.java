package com.jeson.imdemo;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.jeson.imsdk.WebIMService;
import com.jeson.imsdk.WebIMService.WebSDKIBinner;

public class IMApplication extends Application {
	WebIMService.WebSDKIBinner binner;

	@Override
	public void onCreate() {
		super.onCreate();
		Intent service = new Intent(WebIMService.action);
		service.setPackage(getPackageName());
		startService(service);

		bindService(service, new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				binner = (WebSDKIBinner) service;

				Log.i("tag--", "绑定成功-------------");
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				binner = null;
			}

		}, 0);
		
	}

	public WebSDKIBinner getWebSDKIBinner() {
		return binner;
	}

}
