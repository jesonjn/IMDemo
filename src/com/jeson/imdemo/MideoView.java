/**
 * 
 */
package com.jeson.imdemo;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.jeson.xutils.util.LogUtils;

/**
 * @author jiang
 * 
 */
@SuppressLint("ShowToast")
public class MideoView extends ImageButton {
	private MessageEntity mChatMessage;
	private MIAudioRecord mIAudioRecodord;
	private ImageView iv_unread;
	private String mUser;

	// private AnimationDrawable send_audio_animation;
	// private AnimationDrawable receive_audio_animation;

	public String getmUser() {
		return mUser;
	}

	public void setmUser(String mUser) {
		this.mUser = mUser;
	}

	public ImageView getIv_unread() {
		return iv_unread;
	}

	public void setIv_unread(ImageView iv_unread) {
		this.iv_unread = iv_unread;
	}

	private int audioPlayingTime;
	private boolean isPlaying;

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public MIAudioRecord getmIAudioRecodord() {
		return mIAudioRecodord;
	}

	public void setmIAudioRecodord(MIAudioRecord mIAudioRecodord) {
		this.mIAudioRecodord = mIAudioRecodord;
	}

	private int calcPlayingTimeInterval;

	private PlayingAudioTask playingAudioTask;
	private Handler handler;

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public long getAudioPlayingTime() {
		return audioPlayingTime;
	}

	public void setAudioPlayingTime(int audioPlayingTime) {
		this.audioPlayingTime = audioPlayingTime;
	}

	/**
	 * @param context
	 */
	public MideoView(Context context) {
		super(context);
		initView();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public MideoView(Context context, AttributeSet attrs) {

		super(context, attrs);
		initView();
	}

	private void initView() {
		// send_audio_animation = (AnimationDrawable)
		// getResources().getDrawable(
		// R.drawable.clear_screen);
		// receive_audio_animation = (AnimationDrawable) getResources()
		// .getDrawable(R.drawable.clear_screen);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public MideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private class PlayingAudioTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			isPlaying = true;
			LogUtils.d("--------------->" + isPlaying);
			calcPlayingTimeInterval = 0;
		}

		@Override
		protected void onCancelled() {
			Log.e(getClass().getName(), "播放录音任务取消了");
			cancelPlayingAudio();
		}

		@Override
		protected Void doInBackground(Void... params) {
			while (true) {
				if (audioPlayingTime <= calcPlayingTimeInterval) {
					return null;
				}
				if (isCancelled()) {
					return null;
				}
				SystemClock.sleep(100);
				calcPlayingTimeInterval++;
			}
		}

		protected void onPostExecute(Void result) {
			if (null != mIAudioRecodord) {
				mIAudioRecodord.stopPlay();

				// handler.sendEmptyMessage(350);
			}
			cancelPlayingAudio();
		};

	}

	private void cancelPlayingAudio() {
		if (mChatMessage.getDirection() == MessageTypeCst.MESSAGE_FROM) {
			// if (receive_audio_animation.isRunning()) {
			// receive_audio_animation.stop();
			// }
			setBackgroundResource(R.drawable.clear_screen);
		} else {
			// if (send_audio_animation.isRunning()) {
			// send_audio_animation.stop();
			// }
			setBackgroundResource(R.drawable.clear_screen);
		}
		isPlaying = false;
		LogUtils.d("--------------->" + isPlaying);
	}

	@SuppressLint("NewApi")
	public void startAnimation() {
		if (mChatMessage.getDirection() == MessageTypeCst.MESSAGE_FROM) {
			// setBackground(receive_audio_animation);
			// receive_audio_animation.stop();
			// receive_audio_animation.start();
		} else {
			// setBackground(send_audio_animation);
			// send_audio_animation.stop();
			// send_audio_animation.start();
		}
	}

	public void setChatMessage(MessageEntity chatMessage, final int position) {
		mChatMessage = chatMessage;
		((View) getParent()).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				handlerOnClick();
			}
		});
		((View) getParent()).setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return true;
			}
		});
	}

	private void handlerOnClick() {

		if (mChatMessage != null) {
			if (mChatMessage.getDirection() == MessageTypeCst.MESSAGE_FROM
					&& (mChatMessage.getState() != MessageTypeCst.MESSAGE_SUCCESS && mChatMessage
							.getState() != MessageTypeCst.MESSAGE_TARGET_READ)) {
				return;
			}
			if (mChatMessage.getPath() != null) {
				if (mChatMessage.getType() == MessageTypeCst.MESSAGE_TYPE_AUDIO) {
					LogUtils.d("--------------->" + isPlaying);
					if (!isPlaying) {
						if (null != iv_unread
								&& iv_unread.getVisibility() == View.VISIBLE) {
							iv_unread.setVisibility(View.INVISIBLE);
							if (mChatMessage.getState() != 3) {
								mChatMessage.setState(3);
								mChatMessage.setStatus(1);
								if (mChatMessage instanceof ChatMessage) {
									try {
										// EMChatManager.getInstance().ackMessageRead(
										// ((ChatMessage)
										// mChatMessage).getNid(),
										// mChatMessage.getId());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}

						Log.e(getClass().getName(), "开始播放语音！");
						if (mIAudioRecodord == null) {
							mIAudioRecodord = new MIAudioRecord();

						}

						try {
							mIAudioRecodord.startPlay(mChatMessage.getPath());
						} catch (Exception e) {
							e.printStackTrace();
							try {
								// mIAudioRecodord
								// .startPlay(decrypVoiceFile(mChatMessage
								// .getPath()));
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}

						Message msg = handler.obtainMessage(250);

						msg.obj = MideoView.this;

						handler.sendMessage(msg);

						startAnimation();

						playingAudioTask = new PlayingAudioTask();

						playingAudioTask.execute();
					} else if (isPlaying) {
						playingAudioTask.cancel(true);

						if (null != mIAudioRecodord) {
							mIAudioRecodord.stopPlay();

							// handler.sendEmptyMessage(350);
						}
					}

				} else if (mChatMessage.getType() == MessageTypeCst.MESSAGE_TYPE_IMGE
						|| mChatMessage.getType() == MessageTypeCst.MESSAGE_TYPE_PUBLIC_IMG) {
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					intent.setDataAndType(
							Uri.fromFile(new File(mChatMessage.getPath())),
							"image/*");
				}
			} else {
			}
		}

	}

}
