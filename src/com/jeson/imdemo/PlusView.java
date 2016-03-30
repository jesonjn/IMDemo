package com.jeson.imdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class PlusView extends LinearLayout {
	private Context mContext;
	private String tag;

	public PlusView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		initView();
	}

	public PlusView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}

	private void initView() {
		// LinearLayout.LayoutParams params = new LayoutParams(0,
		// LayoutParams.MATCH_PARENT);
		// params.setLayoutDirection(LinearLayout.HORIZONTAL);
		// setLayoutParams(params);

	}

	public void set(String[] tags, Drawable[] ids) {
		for (int i = 0; i < tags.length; i++) {
			LinearLayout linear = (LinearLayout) View.inflate(mContext,
					R.layout.plus_item, null);
			((ImageView) linear.findViewById(R.id.iv)).setBackground(ids[i]);
			((TextView) linear.findViewById(R.id.tv)).setText(tags[i]);
			linear.setTag(tags[i]);
			linear.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					tag = (String) v.getTag();
					performClick();
				}
			});
			LinearLayout.LayoutParams params = new LayoutParams(0,
					LayoutParams.MATCH_PARENT);
			params.setLayoutDirection(LinearLayout.HORIZONTAL);
			params.weight = 1;
			addView(linear, params);
		}

	}

	@Override
	public String getTag() {
		return tag;
	}

	public String getCurrentTag() {
		return tag;
	}
}
