package com.jeson.imdemo;


import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

public class MIAudioRecord
{
	public String mNewAudioName;
	private IMAudioRecorder mAudioRecorder;
	private MediaPlayer m_player;

	public void creatAudioRecord()
	{
		// 获取类的实例
		mAudioRecorder = IMAudioRecorder.getInstanse(false); // 未压缩的录音
	}

	public void startRecord(Context context, String filePath, String user) throws IllegalStateException
	{
		mNewAudioName = filePath;
		// 获取录音文件
		// 设置输出文件
		if (mAudioRecorder == null)
		{
			creatAudioRecord();
		}
		mAudioRecorder.setOutputFile(mNewAudioName);
		mAudioRecorder.prepare();
		// 开始录音
		mAudioRecorder.start();
	}

	public String stopRecord()
	{
		close();
		return mNewAudioName;
	}

	public int getLength(Context context)
	{
		MediaPlayer mediaPlayer = MediaPlayer.create(context, Uri.parse(mNewAudioName));
		if (mediaPlayer != null)
		{
			float second = mediaPlayer.getDuration() * 1.0f / 1000;
			mediaPlayer.release();
			int time = (int) Math.round(second);
			return time;
		}
		return 0;
	}

	public void startPlay(String fileName) throws Exception
	{
		if (m_player == null)
		{
			m_player = new MediaPlayer();
			m_player.setDataSource(fileName);
			m_player.prepare();
			Log.e(getClass().getName(), "语音播放！");
			m_player.start();
		}
		else
		{
			Log.e(getClass().getName(), "语音停止播放！");
			stopPlay();
		}
	}

	public void stopPlay()
	{
		if (m_player != null)
		{
			m_player.stop();
			m_player.reset();
			m_player.release();
			m_player = null;
			Log.e(getClass().getName(), "m_player已设置为空");
		}
	}

	private void close()
	{
		if (mAudioRecorder != null)
		{
			mAudioRecorder.stop();
			mAudioRecorder.reset();
			mAudioRecorder.release();
			mAudioRecorder = null;
		}
	}

}
