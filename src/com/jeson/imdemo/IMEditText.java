package com.jeson.imdemo;

import java.lang.reflect.Field;

import android.R;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

public class IMEditText extends EditText {
	private Context mContext;

	public IMEditText(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public IMEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public IMEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	private void init() {
		// paddingBottom = ScreanDPUtil.convertDpToPixel(getContext(), 55);
		this.setImeOptions(EditorInfo.IME_ACTION_SEND);
		// this.addTextChangedListener(mTextWatcher);
	}

	TextWatcher mTextWatcher = new TextWatcher() {
		private CharSequence temp;
		private int oldLength;
		private int tempLength;

		private int selection;

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			temp = s;
			tempLength = temp.length();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			selection = getSelectionStart();
			oldLength = s.length();
		}

		@Override
		public void afterTextChanged(Editable s) {
			int maxLength = 1000;
			if (temp.length() > maxLength) {

				Toast.makeText(mContext, "字符串长度限制", Toast.LENGTH_SHORT).show();

				CharSequence cs = s;

				cs = cs.subSequence(0, maxLength);

				IMEditText.this.setText(cs);
				setSelection(maxLength);
			} else if (oldLength != tempLength) // 修复两个表情之间不能插入文字的bug
			{
				IMEditText.this.setText(temp);
				if (oldLength > tempLength) {
					setSelection(selection - 1);
				} else {
					setSelection(selection);
				}
			}
		}
	};

	public int getResourceId(String name) {
		try {
			// 根据资源的ID的变量名获得Field的对象,使用反射机制来实现的
			Field field = R.drawable.class.getField(name);
			// 取得并返回资源的id的字段(静态变量)的值，使用反射机制
			return Integer.parseInt(field.get(null).toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}

	public String getTextString() {
		String temp = super.getText().toString();
		// temp = temp.replaceAll( "( ⊙ o ⊙ )","<img src='sms_insert'/>");
		return temp;
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		// Spanned spanned = Html.fromHtml(text.toString(), imgGetter,
		// new Html.TagHandler() {
		//
		// @Override
		// public void handleTag(boolean opening, String tag,
		// Editable output, XMLReader xmlReader) {
		// // LogUtils.d(opening + tag);
		// }
		//
		// });
		super.setText(ExpressionUtil.getExpressionString(mContext,
				text.toString(), "\\[([\u4E00-\u9FA5]+)\\]"), type);
		// super.setMovementMethod(LinkMovementMethod.getInstance());

	}

}
