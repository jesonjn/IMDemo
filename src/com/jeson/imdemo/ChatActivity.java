package com.jeson.imdemo;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jeson.imsdk.AGPBMessage.PB_Text;
import com.jeson.imsdk.AGPBMessage.PB_Text.TextData;
import com.jeson.imsdk.listener.ChatMessageListener;
import com.jeson.watercamera.WaterCameraActivity;

/**
 * 
 * @author jiangneng
 * 
 */
@SuppressLint("NewApi")
public class ChatActivity extends Activity implements OnClickListener {

	PullToRefreshListView mPullToRefreshListView;
	IMEditText mIMEditText;
	Button mSendButton;
	private String mUserName;
	ChattingAdapter adapter;
	private ImageView expressionIV;
	private ImageView plusIV;
	private LinearLayout chatting_plus_view;
	private LinearLayout ll_expression_plus_view;
	private ViewPager vp_plus;
	private ViewPager vp_expression;
	private final List<View> plusViews = new ArrayList<View>();
	private final List<View> expressionViews = new ArrayList<View>();
	private GridView gv_expression1;
	private GvAdapter gvAdapter;
	private final static int EXPRESSION_NAME_LENGTH = 4;
	private String[] senButtons;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		final EditText text = new EditText(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		mUserName = getIntent().getStringExtra("user");
		builder.setMessage(R.string.entry_username);
		builder.setView(text);
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mUserName = text.getText().toString();
				init();
			}
		});
		builder.create().show();

	}

	public void init() {
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.chatting_history_lv);
		mIMEditText = (IMEditText) findViewById(R.id.text_editor);
		plusIV = (ImageView) findViewById(R.id.sms_button_insert);
		expressionIV = (ImageView) findViewById(R.id.iv_expression);
		mSendButton = (Button) findViewById(R.id.send_button);
		chatting_plus_view = (LinearLayout) findViewById(R.id.chatting_plus_view);
		ll_expression_plus_view = (LinearLayout) findViewById(R.id.ll_expressions_plus_view);

		LinearLayout ll_expression_view_item1 = (LinearLayout) View.inflate(
				this, R.layout.expression_plus_viewpager_item1, null);
		vp_plus = (ViewPager) chatting_plus_view
				.findViewById(R.id.chatting_plus_viewpager);
		vp_expression = (ViewPager) ll_expression_plus_view
				.findViewById(R.id.vp_expressions);
		initSendButton();
		expressionViews.add(ll_expression_view_item1);
		vp_expression.setAdapter(new ExpressionAdapter());
		vp_plus.setAdapter(new PlusPagerAdapter());
		gv_expression1 = (GridView) ll_expression_view_item1
				.findViewById(R.id.gv_expression);
		gvAdapter = new GvAdapter(getList(), this);
		gv_expression1.setAdapter(gvAdapter);

		adapter = new ChattingAdapter(this, new Handler(), mUserName);
		mPullToRefreshListView.setAdapter(adapter);
		mPullToRefreshListView.setMoreData(false);
		mIMEditText.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				return false;
			}
		});
		mIMEditText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				return false;
			}
		});
		mSendButton.setOnClickListener(this);
		plusIV.setOnClickListener(this);
		expressionIV.setOnClickListener(this);
		/*** 表情点击 **/
		gv_expression1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position < gvAdapter.lists.size() - 1) {
					String imgName = "["
							+ getResources()
									.getStringArray(R.array.expressions)[position]
							+ "]";
					int start = mIMEditText.getSelectionStart();
					StringBuilder sb = new StringBuilder(mIMEditText.getText()
							.toString());
					sb.insert(start, imgName);
					mIMEditText.setText(sb.toString());
					mIMEditText.setSelection(start + imgName.length());
				} else {
					// 处理删除按钮

					if (mIMEditText.getText().length() >= EXPRESSION_NAME_LENGTH) {
						String str = mIMEditText
								.getText()
								.subSequence(
										mIMEditText.getSelectionStart()
												- EXPRESSION_NAME_LENGTH,
										mIMEditText.getSelectionStart())
								.toString();
						try {
							if (ExpressionUtil.isExpression(ChatActivity.this,
									str, "\\[([\u4E00-\u9FA5]+)\\]")) {
								mIMEditText.getEditableText().delete(
										mIMEditText.getSelectionStart()
												- EXPRESSION_NAME_LENGTH,
										mIMEditText.getSelectionStart());
							} else {
								mIMEditText.getEditableText().delete(
										mIMEditText.getSelectionStart() - 1,
										mIMEditText.getSelectionStart());
							}
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (NoSuchFieldException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					} else {
						if (mIMEditText.getSelectionStart() > 0) {
							mIMEditText.getEditableText().delete(
									mIMEditText.getSelectionStart() - 1,
									mIMEditText.getSelectionStart());
						}
					}

				}
			}
		});

		((IMApplication) getApplication()).getWebSDKIBinner().addChatMessage(
				mUserName, new ChatMessageListener() {
					public boolean proccessMessage(PB_Text packet) {
						ChatMessage chatMessage = new ChatMessage();
						chatMessage.setContent(packet.getContent());
						chatMessage.setNid(mUserName);
						chatMessage.setDirection(MessageTypeCst.MESSAGE_FROM);
						chatMessage.setType(MessageTypeCst.MESSAGE_TYPE_TEXT);
						adapter.addChatMessage(chatMessage);
						runOnUiThread(new Runnable() {

							@Override
							public void run() {

								adapter.notifyDataSetChanged();
								slipToBottomNow();
							}
						});
						return true;
					}

					@Override
					public boolean proccessVideoOffer(String user, TextData data) {
						ChatMessage chatMessage = new ChatMessage();
						chatMessage.setContent(data.getTdkey());
						chatMessage.setFileToken(data.getTdvalue());
						chatMessage.setNid(mUserName);
						chatMessage.setDirection(MessageTypeCst.MESSAGE_FROM);
						chatMessage.setType(MessageTypeCst.MESSAGE_TYPE_VIDEO);
						adapter.addChatMessage(chatMessage);
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								adapter.notifyDataSetChanged();
								slipToBottomNow();
							}
						});
						return true;
					}
				});

	}

	/**
	 * 初始化更多操作功能按扭
	 * 
	 * @param view
	 */
	@SuppressLint("NewApi")
	private void initSendButton() {
		senButtons = getResources().getStringArray(R.array.send_bt_text);
		for (int i = 0; i < senButtons.length; i = (i + 8)) {
			LinearLayout ll_plus_view_item1 = (LinearLayout) View.inflate(this,
					R.layout.chatting_plus_viewpager_item1, null);
			plusViews.add(ll_plus_view_item1);
			PlusView up = (PlusView) ll_plus_view_item1
					.findViewById(R.id.plus_item_up);
			PlusView down = (PlusView) ll_plus_view_item1
					.findViewById(R.id.plus_item_down);
			int a = (senButtons.length - i) > 8 ? 8 : (senButtons.length - i);
			if (a > 0 && a <= 4) {
				String[] tags = new String[a];
				Drawable[] ids = new Drawable[a];
				for (int j = 0; j < a; j++) {
					tags[j] = senButtons[i + j];
					ids[j] = getResources().getDrawable(
							getresourceId(senButtons[i + j]));
				}
				up.set(tags, ids);
				up.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (senButtons[0].equals(v.getTag())) {
							Intent intent = new Intent(ChatActivity.this,
									VideoActivity.class);
							intent.putExtra("username", mUserName);
							intent.putExtra("type", "offer");
							startActivity(intent);

						} else if (senButtons[1].equals(v.getTag())) {
							Intent intent = new Intent(ChatActivity.this,
									WaterCameraActivity.class);
							intent.putExtra("username", mUserName);
							intent.putExtra("type", "answer");
							startActivity(intent);
							Toast.makeText(getBaseContext(), "暂未开放，敬请期待", 1)
									.show();

						} else if (senButtons[2].equals(v.getTag())) {
							Intent intent = new Intent(
									ChatActivity.this,
									com.jeson.erweimascan.ErcodeScanActivity.class);
							intent.putExtra("username", mUserName);
							intent.putExtra("type", "answer");
							startActivity(intent);
							Toast.makeText(getBaseContext(), "暂未开放，敬请期待", 1)
									.show();
						}

					}
				});
			} else if (a > 4 && a <= 8) {
				String[] tags = new String[4];
				Drawable[] ids = new Drawable[4];
				for (int j = 0; j < 4; j++) {
					tags[j] = senButtons[i + j];
					ids[j] = getResources().getDrawable(
							getresourceId(senButtons[i + j]));
				}
				up.set(tags, ids);
				up.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

					}
				});
				String[] tagss = new String[a - 4];
				Drawable[] idss = new Drawable[a - 4];
				for (int j = 4; j < a; j++) {
					tagss[j - 4] = senButtons[i + j];
					idss[j - 4] = getResources().getDrawable(
							getresourceId(senButtons[i + j]));
				}
				down.set(tagss, idss);
				down.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

					}
				});
			}

		}
	}

	public void updateAdapter() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.send_button:
			sendMessage(mIMEditText.getText().toString());
			chatting_plus_view.setVisibility(View.GONE);
			ll_expression_plus_view.setVisibility(View.GONE);
			break;
		case R.id.sms_button_insert:
			chatting_plus_view.setVisibility(View.VISIBLE);
			ll_expression_plus_view.setVisibility(View.GONE);
			break;
		case R.id.iv_expression:
			chatting_plus_view.setVisibility(View.GONE);
			ll_expression_plus_view.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	protected void sendMessage(String message) {
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setContent(message);
		chatMessage.setDirection(MessageTypeCst.MESSAGE_TO);
		chatMessage.setType(MessageTypeCst.MESSAGE_TYPE_TEXT);
		chatMessage.setNid(mUserName);
		adapter.addChatMessage(chatMessage);
		((IMApplication) getApplication()).getWebSDKIBinner().sendMessage(
				mUserName, message);
		mIMEditText.setText("");
		adapter.notifyDataSetChanged();
		slipToBottomNow();
	}

	private class PlusPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return plusViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			vp_plus.removeView(plusViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			vp_plus.addView(plusViews.get(position));

			return plusViews.get(position);
		}
	}

	private class ExpressionAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return expressionViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			vp_expression.removeView(plusViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			vp_expression.addView(expressionViews.get(position));

			return expressionViews.get(position);
		}

	}

	private class GvAdapter extends BaseAdapter {

		public final List<Bitmap> lists;
		private final Context context;

		public GvAdapter(List<Bitmap> lists, Context context) {
			this.lists = lists;
			this.context = context;
		}

		@Override
		public int getCount() {
			return lists.size();
		}

		@Override
		public Object getItem(int position) {
			return lists.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				ImageView mImageView = new ImageView(context);
				mImageView.setImageBitmap(lists.get(position));
				convertView = mImageView;
			}
			return convertView;
		}

	}

	private List<Bitmap> getList() {

		DecimalFormat format = new DecimalFormat("00");
		List<Bitmap> lists = new ArrayList<Bitmap>();
		for (int i = 1; i <= 21; i++) {
			Bitmap mBitmap = BitmapFactory.decodeResource(getResources(),
					getresourceId("expression_" + format.format(i)));
			lists.add(mBitmap);
		}
		return lists;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		// videoManager.onStop();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private int getresourceId(String name) {
		Field field;
		try {
			field = R.drawable.class.getField(name);
			return Integer.parseInt(field.get(null).toString());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 提示音
	 */
	private void promptTone() {

		Uri notification = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
				notification);
		r.play();
	}

	/**
	 * 立即滑到listView底部
	 */
	private void slipToBottomNow() {
		mPullToRefreshListView.getRefreshableView().setSelection(
				adapter.getCount());
	}

}
