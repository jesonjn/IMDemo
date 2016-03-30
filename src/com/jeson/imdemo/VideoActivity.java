package com.jeson.imdemo;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.jeson.imsdk.AGPBMessage.PB_Text.TextData;
import com.jeson.imsdk.video.VideoManager;
import com.jeson.imsdk.video.VideoManager.VideoSystemListener;
import com.jeson.imsdk.video.VideoStreamsView;

public class VideoActivity extends Activity {

	private VideoStreamsView videoStreams;
	private VideoManager manage;
	VideoSystemListener mListener;
	private String userName;
	LinearLayout llLocalView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_video);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		userName = getIntent().getStringExtra("username");
		videoStreams = (VideoStreamsView) findViewById(R.id.viewinfo);
		llLocalView = (LinearLayout) findViewById(R.id.llLocalView);

		manage = VideoManager.getVideoManagerIntance();
		mListener = new VideoSystemListener() {

			@Override
			public boolean proccess(int i) {
				return false;
			}

			@Override
			public boolean finish(boolean opt) {
				return false;
			}

			@Override
			public boolean receiverOffer(String user, String type,
					TextData data, VideoManager manager) {

				Log.e("TAG", "收到一个offer");
				manager.acceptOffer(data);
				return true;
			}

		};
		manage.addVideoListener(userName, mListener);
		manage.init(this, ((IMApplication) getApplication()).getWebSDKIBinner()
				.getWebSDK(), userName);
		manage.onResumeVideoInit(videoStreams, llLocalView, 0);
		if ("offer".equals(getIntent().getStringExtra("type"))) {
			manage.sendOffer();
			Log.e("TAG", "发送一个请求offer");
		}else if("answer".equals(getIntent().getStringExtra("type"))){
			manage.acceptOffer("offer",getIntent().getStringExtra("fileID"));
		}

	}

	@Override
	protected void onDestroy() {
		manage.onDestory();
		super.onDestroy();

	}

	@Override
	protected void onStop() {
		manage.onStop();
		super.onStop();

	}

	@Override
	protected void onResume() {

		manage.onResume();
		super.onResume();
		manage.addVideoListener(userName, mListener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		manage.removeVideoListener(userName);
	}

}
