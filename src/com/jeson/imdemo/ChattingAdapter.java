package com.jeson.imdemo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChattingAdapter extends BaseAdapter {
	private ChatActivity chatActivity;
	private Handler handler;
	private String mUser;

	private List<ChatMessage> list = new ArrayList<ChatMessage>();

	public boolean isLongClick;

	public ChattingAdapter(ChatActivity context, Handler handler, String user) {
		super();
		this.chatActivity = context;
		this.handler = handler;
		mUser = user;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void addChatMessage(ChatMessage chat) {
		list.add(chat);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		final ChatMessage chatMessage = list.get(position);
		final int direction = chatMessage.getDirection();

		if (convertView == null
				|| (holder = (ViewHolder) convertView.getTag()).flag != direction) {

			holder = new ViewHolder();
			if (direction == MessageTypeCst.MESSAGE_FROM) {
				holder.flag = MessageTypeCst.MESSAGE_FROM;
				convertView = LayoutInflater.from(chatActivity).inflate(
						R.layout.chatting_item_from, null);
				holder.iv_unread_audio = (ImageView) convertView
						.findViewById(R.id.iv_unread_audio);
			} else {
				holder.flag = MessageTypeCst.MESSAGE_TO;
				convertView = LayoutInflater.from(chatActivity).inflate(
						R.layout.chatting_item_to, null);
			}

			holder.tv_content = (TextView) convertView
					.findViewById(R.id.msg_content_txt);
			holder.tv_chatting_date = (TextView) convertView
					.findViewById(R.id.chatting_time_tv);
			holder.head_image = (ImageView) convertView
					.findViewById(R.id.chatting_iv);
			holder.img_image = (MideoView) convertView
					.findViewById(R.id.file_iv);
			holder.ll_picture = (LinearLayout) convertView
					.findViewById(R.id.ll_picture);
			holder.audio_linearLayout = (LinearLayout) convertView
					.findViewById(R.id.ll_chatting_audio);
			holder.mideoview_audio_linear = (LinearLayout) convertView
					.findViewById(R.id.medeoview_audio_linear);
			holder.audio_image = (MideoView) convertView
					.findViewById(R.id.mv_audio);
			holder.audio_time = (TextView) convertView
					.findViewById(R.id.tv_audio_time);
			holder.img_state = (ImageView) convertView
					.findViewById(R.id.img_status);
			holder.ll_vcard = (LinearLayout) convertView
					.findViewById(R.id.ll_vcard);
			holder.iv_vcard = (ImageView) convertView
					.findViewById(R.id.iv_vcard);
			holder.tv_vcard_name = (TextView) convertView
					.findViewById(R.id.tv_vcard_name);
			holder.tv_vcard_dept = (TextView) convertView
					.findViewById(R.id.tv_vcard_dept);
			holder.tv_vcard_job = (TextView) convertView
					.findViewById(R.id.tv_vcard_job);

			holder.ll_location = (LinearLayout) convertView
					.findViewById(R.id.ll_location);
			holder.tv_location = (TextView) convertView
					.findViewById(R.id.location_content_itv);

			holder.rl_send_file = (RelativeLayout) convertView
					.findViewById(R.id.rl_send_file);
			holder.iv_send_file = (ImageView) convertView
					.findViewById(R.id.iv_sendfile);
			holder.tv_file_name = (TextView) convertView
					.findViewById(R.id.tv_file_name);
			holder.tv_file_size = (TextView) convertView
					.findViewById(R.id.tv_file_size);
			holder.pb_file = (ProgressBar) convertView
					.findViewById(R.id.pb_file);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.audio_time.setVisibility(View.GONE);
		// 先清空文字消息中的所欲子view
		if ("".equals(chatMessage.getDateSign())) {
			// 让顶部日期先隐藏
			holder.tv_chatting_date.setVisibility(View.GONE);
		} else {
			holder.tv_chatting_date.setText(chatMessage.getDateSign());
			holder.tv_chatting_date.setVisibility(View.VISIBLE);
		}

		if (direction == MessageTypeCst.MESSAGE_FROM) {
			// 头像设置监听
			holder.head_image.setOnClickListener(new MyClickListener(
					chatMessage));
		} else {
			holder.img_state.clearAnimation();
			switch (chatMessage.getState()) {
			default:
				holder.img_state.setVisibility(View.GONE);
				break;
			}

			holder.img_state.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			});
		}

		switch (chatMessage.getType()) {
		case MessageTypeCst.MESSAGE_TYPE_TEXT:
			handleTextMessage(chatMessage, position, holder);
			break;
		case MessageTypeCst.MESSAGE_TYPE_VCARD:
			handleVcardMessage(chatMessage, position, holder);
			break;
		case MessageTypeCst.MESSAGE_TYPE_IMGE:
			handleImageMessage(chatMessage, position, holder);
			break;
		case MessageTypeCst.MESSAGE_TYPE_AUDIO:
			handleVioceMessage(chatMessage, position, holder);
			break;
		case MessageTypeCst.MESSAGE_TYPE_LOCATION:
			handleLocationMessage(chatMessage, position, holder);
			break;
		case MessageTypeCst.MESSAGE_TYPE_FILE:
			handleFileMessage(chatMessage, position, holder);
			break;
		case MessageTypeCst.MESSAGE_TYPE_VIDEO:
			handleVideoMessage(chatMessage, position, holder);
			break;
		default:
			break;
		}

		return convertView;
	}

	private void setVedio(final ChatMessage message, MideoView mideoView,
			TextView timeTextView, LinearLayout audio_linearLayout,
			LinearLayout medeoview_audio_linear, String filePath,
			final int position) {
		File file = new File(filePath);
		if (file.exists()) {
			timeTextView.setTextColor(Color.parseColor("#999999"));
			int time = message.getVoiceLength();
			if (time > 0) {
				if (time > 60) {
					timeTextView.setText("60″");
					time = 60;
				} else {
					timeTextView.setText(time + "″");
				}
				LayoutParams layoutParams = new LinearLayout.LayoutParams(
						caculateAudioWidth(time),
						ScreanDPUtil.convertDpToPixel(chatActivity, 35));
				medeoview_audio_linear.setLayoutParams(layoutParams);
				if (message.getDirection() == MessageTypeCst.MESSAGE_FROM) {
					medeoview_audio_linear.setGravity(Gravity.LEFT
							| Gravity.CENTER_VERTICAL);
				} else {
					medeoview_audio_linear.setGravity(Gravity.RIGHT
							| Gravity.CENTER_VERTICAL);
				}

				mideoView.setAudioPlayingTime(time * 10);
			}
		} else {
			timeTextView.setTextColor(Color.parseColor("#86b6f0"));
			if (message.getDirection() == MessageTypeCst.MESSAGE_TO) {
				timeTextView.setText("文件不存在");
			} else if (message.getDirection() == MessageTypeCst.MESSAGE_FROM
					&& message.getState() == MessageTypeCst.MESSAGE_FAIL) {
				repeatRecieverVedio(message, timeTextView, mideoView,
						audio_linearLayout);
			}
		}
	}

	private int caculateAudioWidth(int second) {
		int initialWidth = ScreanDPUtil.convertDpToPixel(chatActivity, 50);
		int maxWidth = ScreanDPUtil.convertDpToPixel(chatActivity, 135);
		return initialWidth + second * maxWidth / 60;
	}

	/**
	 * 重新接收语音
	 * 
	 * @param message
	 * @param timeTextView
	 */
	private void repeatRecieverVedio(final ChatMessage message,
			TextView timeTextView, MideoView audio_image,
			LinearLayout audio_linearLayout) {
		// timeTextView.setText(chatActivity.getString(R.string.retry_receive));
		// timeTextView.setTextColor(Color.parseColor("#86b6f0"));
		// // audio_image.setBackgroundResource(R.drawable.audio_receive_fail);
		// audio_linearLayout.setOnClickListener(new OnClickListener()
		// {
		//
		// @Override
		// public void onClick(View v)
		// {
		// Toast.makeText(chatActivity.getApplication(),
		// chatActivity.getString(R.string.restart_receive), 1)
		// .show();
		// mIMServiceBinder.repeatDownLoadFile(message,
		// chatActivity.mOnMessageListener);
		// }
		// });

	}

	private void handleFileMessage(ChatMessage chatMessage, int position,
			ViewHolder holder) {

		// try {
		// JSONObject jsonObject = new JSONObject(chatMessage.getContent());
		// int fileSize = Integer.parseInt(jsonObject.getString("fileLength"));
		holder.tv_file_name.setText(chatMessage.getContent());
		// holder.tv_file_size.setText(FileUtils
		// .byteCountToDisplaySize(fileSize));
		int resourceId = 0;
		holder.iv_send_file.setImageResource(resourceId);
		if (chatMessage.getDirection() == MessageTypeCst.MESSAGE_TO) {

			// holder.pb_file.setMax(fileSize);
			holder.pb_file.setProgress(chatMessage.getVoiceLength());

			if (chatMessage.getState() == MessageTypeCst.MESSAGE_SENDING) {
				holder.pb_file.setVisibility(View.VISIBLE);
			} else {
				holder.pb_file.setVisibility(View.GONE);
			}
		}

		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		holder.rl_send_file
				.setOnClickListener(new MyClickListener(chatMessage));
		holder.rl_send_file.setOnLongClickListener(new MyLongClickListener(
				chatMessage, position));
		holder.rl_send_file.setVisibility(View.VISIBLE);
		holder.tv_content.setVisibility(View.GONE);
		holder.ll_location.setVisibility(View.GONE);
		holder.audio_linearLayout.setVisibility(View.GONE);
		holder.ll_picture.setVisibility(View.GONE);
		holder.ll_vcard.setVisibility(View.GONE);

	}

	private void handleLocationMessage(ChatMessage chatMessage, int position,
			ViewHolder holder) {

		holder.tv_location.setText(chatMessage.getAddress());
		holder.ll_location.setOnClickListener(new MyClickListener(chatMessage));
		holder.ll_location.setOnLongClickListener(new MyLongClickListener(
				chatMessage, position));
		holder.ll_location.setVisibility(View.VISIBLE);
		holder.tv_content.setVisibility(View.GONE);
		holder.audio_linearLayout.setVisibility(View.GONE);
		holder.ll_picture.setVisibility(View.GONE);
		holder.ll_vcard.setVisibility(View.GONE);
		holder.rl_send_file.setVisibility(View.GONE);

	}

	private void handleVioceMessage(ChatMessage chatMessage, int position,
			ViewHolder holder) {

		holder.audio_linearLayout.setVisibility(View.VISIBLE);
		holder.audio_time.setVisibility(View.VISIBLE);
		// handler.sendEmptyMessage(ChatActivity.START_AUDIO_ANIMATION_MSG);//
		// 解决接收新消息语音动画停止问题
		if (chatMessage.getDirection() == MessageTypeCst.MESSAGE_FROM) {
			if (chatMessage.getState() == 3) {
				holder.iv_unread_audio.setVisibility(View.INVISIBLE);
			} else {
				holder.iv_unread_audio.setVisibility(View.VISIBLE);
			}
			holder.audio_image.setIv_unread(holder.iv_unread_audio);
			if (chatMessage.getState() == MessageTypeCst.MESSAGE_SUCCESS
					|| chatMessage.getState() == 3) {
				if (TextUtils.isEmpty(chatMessage.getPath())) {
					repeatRecieverVedio(chatMessage, holder.audio_time,
							holder.audio_image, holder.audio_linearLayout);
				} else {
					setVedio(chatMessage, holder.audio_image,
							holder.audio_time, holder.audio_linearLayout,
							holder.mideoview_audio_linear,
							chatMessage.getPath(), position);
				}
			} else if (chatMessage.getState() == MessageTypeCst.MESSAGE_FAIL) {
				repeatRecieverVedio(chatMessage, holder.audio_time,
						holder.audio_image, holder.audio_linearLayout);
			}
		} else {
			setVedio(chatMessage, holder.audio_image, holder.audio_time,
					holder.audio_linearLayout, holder.mideoview_audio_linear,
					chatMessage.getPath(), position);
		}

		holder.audio_image.setmUser(mUser);
		holder.audio_image.setHandler(handler);

		holder.audio_image.setChatMessage(chatMessage, position);
		holder.tv_content.setVisibility(View.GONE);
		holder.ll_location.setVisibility(View.GONE);
		holder.ll_picture.setVisibility(View.GONE);
		holder.ll_vcard.setVisibility(View.GONE);
		holder.rl_send_file.setVisibility(View.GONE);

	}

	private void handleVideoMessage(final ChatMessage chatMessage,
			int position, ViewHolder holder) {

		// holder.audio_linearLayout.setVisibility(View.VISIBLE);
		// holder.audio_time.setVisibility(View.VISIBLE);
		// holder.audio_image.setmUser(mUser);
		// holder.audio_image.setHandler(handler);
		holder.tv_content.setText(chatMessage.getContent());
		holder.tv_content.setAutoLinkMask(1);
		holder.tv_content.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		holder.tv_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(chatActivity, VideoActivity.class);
				intent.putExtra("username", chatMessage.getNid());
				intent.putExtra("type", "answer");
				intent.putExtra("fileID", chatMessage.getFileToken());
				chatActivity.startActivity(intent);
			}
		});
		// holder.audio_image.setChatMessage(chatMessage, position);
		holder.tv_content.setVisibility(View.VISIBLE);
		holder.ll_location.setVisibility(View.GONE);
		holder.ll_picture.setVisibility(View.GONE);
		holder.ll_vcard.setVisibility(View.GONE);
		holder.rl_send_file.setVisibility(View.GONE);

	}

	private void handleVcardMessage(ChatMessage chatMessage, int position,
			ViewHolder holder) {

		String vcardJson = chatMessage.getContent();
		try {
			JSONObject jsonObject = new JSONObject(vcardJson);

			final String nid = jsonObject.getString("NID");

			// LoadImageUtil.getInstance(chatActivity).loadRoundImage(holder.iv_vcard,
			// user);

			holder.tv_vcard_name.setText(jsonObject.getString("name"));
			holder.tv_vcard_dept.setText(jsonObject.getString("dept"));
			holder.tv_vcard_job.setText(jsonObject.getString("job"));
			holder.ll_vcard
					.setOnClickListener(new MyClickListener(chatMessage));
			holder.ll_vcard.setOnLongClickListener(new MyLongClickListener(
					chatMessage, position));
		} catch (Exception e) {
			e.printStackTrace();
		}
		holder.ll_vcard.setVisibility(View.VISIBLE);
		holder.tv_content.setVisibility(View.GONE);
		holder.ll_location.setVisibility(View.GONE);
		holder.audio_linearLayout.setVisibility(View.GONE);
		holder.ll_picture.setVisibility(View.GONE);
		holder.rl_send_file.setVisibility(View.GONE);

	}

	private void handleTextMessage(ChatMessage chatMessage, int position,
			ViewHolder holder) {
		String content = chatMessage.getContent();
		// 过滤
		// if (TextUtil.isBeShareText(content)) {
		// // 处理BeShare消息
		// handleBeShareMsg(chatMessage, holder, content);
		// } else {
		if (holder.head_image != null)
			holder.head_image.setBackground(chatActivity.getResources()
					.getDrawable(R.drawable.avatar_female));
		holder.tv_content.setCompoundDrawables(null, null, null, null);
		holder.tv_content.setAutoLinkMask(Linkify.ALL);
		holder.tv_content.setText(ExpressionUtil.getExpressionString(
				chatActivity, content, "\\[([\u4E00-\u9FA5]+)\\]"));
		holder.tv_content.setMovementMethod(LinkMovementClickMethod
				.getInstance());
		// }
		holder.tv_content.setOnLongClickListener(new MyLongClickListener(
				chatMessage, position));
		holder.tv_content.setVisibility(View.VISIBLE);
		holder.audio_linearLayout.setVisibility(View.GONE);
		holder.ll_picture.setVisibility(View.GONE);
		holder.ll_vcard.setVisibility(View.GONE);
		holder.ll_location.setVisibility(View.GONE);
		holder.rl_send_file.setVisibility(View.GONE);
	}

	private void handleImageMessage(final ChatMessage chatMessage,
			int position, ViewHolder holder) {
		if (chatMessage.getDirection() == MessageTypeCst.MESSAGE_FROM) {
			if (TextUtils.isEmpty(chatMessage.getPath())
					|| chatMessage.getPath().equals("null")) {
				if (chatMessage.getState() == MessageTypeCst.MESSAGE_FAIL) {
					// 接收失败
					holder.img_image.setClickable(false);
					holder.img_image.setImageResource(1);
					holder.img_image.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// Toast.makeText(chatActivity,
							// R.string.restart_receive, 1).show();
							// if (null != mIMServiceBinder)
							// {
							// mIMServiceBinder.repeatDownLoadFile(chatMessage,
							// chatActivity.mOnMessageListener);
							// }
							// else
							// {
							// Toast.makeText(chatActivity,
							// "mIMServiceBinder is null", 0).show();
							// }
						}
					});
				} else {
					// 正在接收
					holder.img_image.setImageResource(1);
					holder.img_image.setClickable(true);
				}
			} else {
				holder.img_image.setClickable(false);
				holder.img_image.setChatMessage(chatMessage, position);
				showImageView(chatMessage, holder.img_image);
			}
		} else {
			holder.img_image.setClickable(false);
			holder.img_image.setChatMessage(chatMessage, position);
			showImageView(chatMessage, holder.img_image);
		}

		holder.img_image.setHandler(handler);

		holder.ll_picture.setVisibility(View.VISIBLE);
		holder.tv_content.setVisibility(View.GONE);

		holder.audio_linearLayout.setVisibility(View.GONE);
		holder.ll_vcard.setVisibility(View.GONE);
		holder.ll_location.setVisibility(View.GONE);
		holder.rl_send_file.setVisibility(View.GONE);
	}

	private class MyLongClickListener implements OnLongClickListener {
		private ChatMessage chatMessage;
		private int position;

		private MyLongClickListener(ChatMessage chatMessage, int position) {
			this.chatMessage = chatMessage;
			this.position = position;
		}

		@Override
		public boolean onLongClick(View view) {

			// if (MsgOperatePupWindow.getInstance(chatActivity).isShown()) {
			// return true;
			// }
			//
			// if (chatMessage.getType() == MessageTypeCst.MESSAGE_TYPE_TEXT) {
			// isLongClick = true;
			// MsgOperatePupWindow.getInstance(chatActivity)
			// .setButtonCopyVisible();
			// } else {
			// MsgOperatePupWindow.getInstance(chatActivity)
			// .setButtonCopyGone();
			// }
			// int x = view.getWidth() / 2
			// - MsgOperatePupWindow.getInstance(chatActivity).getWidth()
			// / 2;
			// int y = -(view.getHeight() - MsgOperatePupWindow.getInstance(
			// chatActivity).getHeight()) / 2;
			// MsgOperatePupWindow.getInstance(chatActivity).showAsDropDown(view,
			// null, null, 0, x, y);
			// MsgOperatePupWindow.getInstance(chatActivity).getBt_copy()
			// .setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// longClickCopy(chatMessage);
			// }
			// });
			//
			// MsgOperatePupWindow.getInstance(chatActivity).getBt_delete()
			// .setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// longClickDelete(chatMessage, position);
			// }
			// });
			//
			// MsgOperatePupWindow.getInstance(chatActivity).getBt_transmit()
			// .setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// longClickTransmit(chatMessage);
			// }
			// });
			// MsgOperatePupWindow.getInstance(chatActivity).getBt_calender()
			// .setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// longClickCalender(chatMessage);
			// }
			// });
			return true;
		}

	}

	private class MyClickListener implements OnClickListener {

		ChatMessage chatMessage;

		public MyClickListener(ChatMessage chatMessage) {
			this.chatMessage = chatMessage;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.chatting_iv:
				clickHeadPhoto(chatMessage.getNid());
				break;
			case R.id.ll_vcard:
				clickVcard(chatMessage);
				break;
			case R.id.ll_location:
				clickLocation(chatMessage);
				break;
			case R.id.rl_send_file:
				clickFile(chatMessage);
				break;
			default:
				break;
			}
		}
	}

	private void longClickCopy(ChatMessage chatMessage) {
		Toast.makeText(chatActivity.getApplicationContext(), "已复制到剪切板", 0)
				.show();
		String content = chatMessage.getContent();
		// ClipBoardUtil.copy(chatActivity, content);
		// MsgOperatePupWindow.getInstance(chatActivity).dismiss();
	}

	public void setMessageDateSign(List<ChatMessage> list) {
		ChatMessage chatMessage;
		ChatMessage lastChatMessage;
		for (int i = 0; i < list.size(); i++) {
			chatMessage = list.get(i);
			// if (i == 0) {
			// chatMessage.setDateSign(DateUtil.getTimestamp(new Date(
			// chatMessage.getServerTime())));
			// } else {
			// // 两条消息时间离得如果稍长，显示时间
			// lastChatMessage = list.get(i - 1);
			// if (!DateUtil.isCloseEnough(chatMessage.getServerTime(),
			// lastChatMessage.getServerTime())) {
			// chatMessage.setDateSign(DateUtil.getTimestamp(new Date(
			// chatMessage.getServerTime())));
			// } else {
			// chatMessage.setDateSign("");
			// }
			// }
		}
	}

	private void longClickDelete(ChatMessage message, int position) {

		// MsgOperatePupWindow.getInstance(chatActivity).dismiss();
		// IMChatDao.getInstance(chatActivity).deleteById(ChatMessage.class,
		// message.getId());
		// ChatMessage removedMessage = list.remove(position);
		//
		// if (list.size() == 0) {
		//
		// ChatRecoder cr = new ChatRecoder();
		// cr.setNid(UserUtil.getNId(removedMessage.getNid()));
		// cr.setContent("");
		// cr.setContextType(100);
		//
		// IMChatDao.getInstance(chatActivity).update(cr,
		// new String[] { "content", "state", "contextType" });
		//
		// } else if (list.size() == position) {
		// ChatMessage chatMessage = list.get(position - 1);
		// ChatRecoder cr = IMChatDao.getInstance(chatActivity).findById(
		// ChatRecoder.class, chatMessage.getNid());
		//
		// if (chatMessage.getDirection() == MessageTypeCst.MESSAGE_FROM) {
		// cr.setFrom(true);
		// } else {
		// cr.setFrom(false);
		// }
		// if (chatMessage.getType() == MessageTypeCst.MESSAGE_TYPE_VCARD) {
		// cr.setContent(chatActivity
		// .getString(R.string.msg_type_business_card));
		// } else if (chatMessage.getType() == MessageTypeCst.MESSAGE_TYPE_FILE)
		// {
		// cr.setContent("[" + chatActivity.getString(R.string.file) + ":"
		// + chatMessage.getFileName() + "]");
		// } else {
		// cr.setContent(chatMessage.getContent());
		// }
		// cr.setContextType(chatMessage.getType());
		// cr.setLocalTime(chatMessage.getLocalTime());
		// cr.setServerTime(chatMessage.getServerTime());
		// IMChatDao.getInstance(chatActivity).update(cr);
		//
		// }
		// if (message.getType() == MessageTypeCst.MESSAGE_TYPE_FILE
		// && message.getState() != MessageTypeCst.MESSAGE_SUCCESS) {
		// JSONObject jsonObject;
		// try {
		// jsonObject = new JSONObject(message.getContent());
		// String filePath = jsonObject.getString("filePath");
		// IMChatMessage.getInstance(chatActivity, mUser).removeUpload(
		// filePath);
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// }

		setMessageDateSign(list);
		notifyDataSetChanged();
	}

	private void longClickTransmit(ChatMessage chatMessage) {
		// chatMessage =
		// IMChatDao.getInstance(chatActivity).findById(ChatMessage.class,
		// chatMessage.getId());
		// if (chatMessage.getType() == MessageTypeCst.MESSAGE_TYPE_FILE)
		// {
		// if (TextUtils.isEmpty(chatMessage.getFileToken()))
		// {
		// MsgOperatePupWindow.getInstance(chatActivity).dismiss();
		// Toast.makeText(chatActivity.getApplication(),
		// R.string.file_not_exist, 0).show();
		// return;
		// }
		// }
		// Intent intent = new Intent(chatActivity, IMTransmitActivity.class);
		// intent.putExtra("user", mUser);
		// intent.putExtra("message", chatMessage);
		// AppManager.getAppManager().startActivity(chatActivity, intent);
		// MsgOperatePupWindow.getInstance(chatActivity).dismiss();
	}

	@SuppressLint({ "InlinedApi", "NewApi" })
	private void longClickCalender(ChatMessage message) {

		// try {
		// Calendar beginTime = Calendar.getInstance();
		// Calendar endTime = Calendar.getInstance();
		// Intent intent = new Intent(Intent.ACTION_INSERT);
		// intent.setData(Events.CONTENT_URI)
		// .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
		// beginTime.getTimeInMillis())
		// .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
		// endTime.getTimeInMillis())
		// .putExtra(
		// Events.TITLE,
		// chatActivity
		// .getString(R.string.notification_title_app_name));
		// if (message.getType() == MessageTypeCst.MESSAGE_TYPE_TEXT) {
		// intent.putExtra(Events.DESCRIPTION, message.getContent());
		// }
		//
		// chatActivity.startActivity(intent);
		// MsgOperatePupWindow.getInstance(chatActivity).dismiss();
		// } catch (Exception e) {
		// Toast.makeText(chatActivity.getApplicationContext(),
		// R.string.verson_above_40_can_use, Toast.LENGTH_SHORT)
		// .show();
		// e.printStackTrace();
		// }

	}

	private void clickVcard(ChatMessage chatMessage) {

		// try {
		// JSONObject vcard = new JSONObject(chatMessage.getContent());
		//
		// Intent infoIntent = new Intent(chatActivity,
		// IMUserInfoActivity.class);
		// infoIntent.putExtra("nid", vcard.getString("NID"));
		// infoIntent.putExtra("user", mUser);
		// AppManager.getAppManager().startActivity(chatActivity, infoIntent);
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }

	}

	private void clickLocation(ChatMessage chatMessage) {
		// Intent intent;
		// intent = new Intent(chatActivity, GaodeMapActivity.class);
		// intent.putExtra("latitude", chatMessage.getLatitude());
		// intent.putExtra("longitude", chatMessage.getLongitude());
		// intent.putExtra("address", chatMessage.getAddress());
		// AppManager.getAppManager().startActivity(chatActivity, intent);

	}

	private void clickFile(ChatMessage chatMessage) {

		// List<FileManager> fileManagerList = null;
		// try
		// {
		// fileManagerList = IMChatDao.getInstance(chatActivity).getDbUtils()
		// .findAll(Selector.from(FileManager.class).where("fileName", "=",
		// chatMessage.getFileName()));
		// }
		// catch (DbException e)
		// {
		// e.printStackTrace();
		// }
		// if (fileManagerList != null && !fileManagerList.isEmpty())
		// {
		// FileManager fileManager = fileManagerList.get(0);
		// if (fileManager.isFrom())
		// {
		// Intent intent;
		// intent = new Intent(chatActivity, FileInfoDownloadActivity.class);
		// intent.putExtra("fileToken", fileManager.getFileToken());
		// intent.putExtra("fileName", fileManager.getFileName());
		// intent.putExtra("messageId", fileManager.getMessageId());
		// intent.putExtra("user", mUser);
		// intent.putExtra("time", fileManager.getTime());
		// intent.putExtra("type", MessageTypeCst.RECODER_SIGLE);
		// intent.putExtra("fileSize", fileManager.getFileSize());
		// intent.putExtra("description", fileManager.getDescription());
		// AppManager.getAppManager().startActivity(chatActivity, intent);
		// }
		// else
		// {
		// Intent intent;
		// intent = new Intent(chatActivity, FileInfoUploadActivity.class);
		// if (TextUtils.isEmpty(fileManager.getFilePath()))
		// {
		// Toast.makeText(chatActivity, R.string.file_not_exist, 0).show();
		// return;
		// }
		// intent.putExtra("filePath", fileManager.getFilePath());
		// intent.putExtra("fileSize", fileManager.getFileSize());
		// intent.putExtra("messageId", chatMessage.getId());
		// intent.putExtra("user", mUser);
		// intent.putExtra("time", fileManager.getTime());
		// intent.putExtra("type", MessageTypeCst.RECODER_SIGLE);
		// intent.putExtra("description", fileManager.getDescription());
		// AppManager.getAppManager().startActivity(chatActivity, intent);
		// }
		// }
		// else
		// {
		// Toast.makeText(chatActivity, R.string.file_not_exist, 0).show();
		// }
	}

	private void clickHeadPhoto(String nid) {
		// User user;
		// try {
		// user = UcLibrayDBUtils.getInstance(chatActivity).getUser(nid);
		// if (user != null) {
		// Intent intent = new Intent(chatActivity,
		// IMUserInfoActivity.class);
		// intent.putExtra("user", mUser);
		// intent.putExtra("nid", nid);
		// AppManager.getAppManager().startActivity(chatActivity, intent);
		// } else {
		// // Intent intent = new Intent(chatActivity,
		// // IMNoPeopleInfoActivity.class);
		// // AppManager.getAppManager().startActivity(chatActivity,
		// // intent);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * load image into image view
	 * 
	 * @param iv
	 * @param position
	 * @return the image exists or not
	 */
	private void showImageView(ChatMessage chatMessage, ImageView iv) {
		// first check if the thumbnail image already loaded into cache
		if (chatMessage.getPath() != null) {
			if (chatMessage.getPath().endsWith("/")) {
				chatMessage.setPath(chatMessage.getPath()
						+ chatMessage.getFileName());
			}

			// Bitmap bitmap =
			// ImageCache.getInstance().get(chatMessage.getPath());
			// if (bitmap != null) {
			// // thumbnail image is already loaded, reuse the drawable
			// iv.setImageBitmap(bitmap);
			// return;
			// } else {
			// new LoadSigleImageTask().execute(chatActivity, chatMessage, iv,
			// handler);
			// }
		} else {
			// iv.setImageResource(R.drawable.unreceive);
		}
	}

	static class ViewHolder {
		// 聊天日期
		TextView tv_chatting_date;
		// 头像
		ImageView head_image;
		// 发送图片
		LinearLayout ll_picture;
		MideoView img_image;
		TextView tv_content;
		// 发送名片
		LinearLayout ll_vcard;
		ImageView iv_vcard;
		TextView tv_vcard_name;
		TextView tv_vcard_dept;
		TextView tv_vcard_job;
		// 发送语音
		LinearLayout audio_linearLayout;
		LinearLayout mideoview_audio_linear;
		MideoView audio_image;
		TextView audio_time;
		// 语音读取状态
		ImageView iv_unread_audio;
		int flag;
		// 发送状态
		ImageView img_state;
		// 放松位置
		LinearLayout ll_location;
		TextView tv_location;
		// 发送文件
		RelativeLayout rl_send_file;
		ImageView iv_send_file;
		TextView tv_file_name;
		TextView tv_file_size;
		ProgressBar pb_file;

	}
}
