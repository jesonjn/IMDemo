package com.jeson.imdemo;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.jeson.xutils.util.LogUtils;

public class ExpressionUtil {
	private static SensitivewordFilter filter;// 敏感词过滤
	private static Map keyMap = null;

	/**
	 * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
	 * 
	 * @param context
	 * @param spannableString
	 * @param patten
	 * @param start
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws NumberFormatException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static boolean dealExpression(Context context,
			SpannableString spannableString, Pattern patten, int start)
			throws SecurityException, NoSuchFieldException,
			NumberFormatException, IllegalArgumentException,
			IllegalAccessException {
		Matcher matcher = patten.matcher(spannableString);
		while (matcher.find()) {
			String key = matcher.group(1);
			if (matcher.start() < start) {
				continue;
			}

			int index = getExpressionIndex(context, key);

			DecimalFormat format = new DecimalFormat("00");
			String imgName = "expression_" + format.format(index + 1);

			Field field = R.drawable.class.getDeclaredField(imgName);
			int resId = Integer.parseInt(field.get(null).toString()); // 通过上面匹配得到的字符串来生成图片资源id
			if (resId != 0) {
				Drawable drawable = context.getResources().getDrawable(resId);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth() * 2 / 3,
						drawable.getIntrinsicHeight() * 2 / 3);
				// 要让图片替代指定的文字就要用ImageSpan
				ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
				int end = matcher.start() + key.length() + 2; // 计算该图片名字的长度，也就是要替换的字符串的长度
				spannableString.setSpan(span, matcher.start(), end,
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE); // 将该图片替换字符串中规定的位置中
				if (end < spannableString.length()) { // 如果整个字符串还未验证完，则继续。。
					dealExpression(context, spannableString, patten, end);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 得到一个SpanableString对象，通过传入的字符串,并进行正则判断
	 * 
	 * @param context
	 * @param str
	 * @return
	 */
	public static SpannableString getExpressionString(Context context,
			String str, String zhengze) {
		SpannableString spannableString = new SpannableString(str);
		Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
		try {
			boolean isExpression = dealExpression(context, spannableString,
					sinaPatten, 0);
			if (!isExpression) {
				// 过滤
				if (keyMap != null) {
					filter = new SensitivewordFilter(keyMap);
					str = filter.replaceSensitiveWord(str, 2, "*");
					return new SpannableString(str);
				}
			}
		} catch (Exception e) {
			LogUtils.e(e.getMessage());
		}
		return spannableString;
	}

	/**
	 * 查看一个字符串是否为表情
	 * 
	 * @param context
	 * @param str
	 * @param zhengze
	 * @return
	 * @throws NoSuchFieldException
	 * @throws NumberFormatException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static boolean isExpression(Context context, String str,
			String zhengze) throws NoSuchFieldException, NumberFormatException,
			IllegalArgumentException, IllegalAccessException {
		Pattern pattern = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			String key = matcher.group(1);
			int index = getExpressionIndex(context, key);
			DecimalFormat format = new DecimalFormat("00");
			String imgName = "expression_" + format.format(index + 1);
			Field field = R.drawable.class.getDeclaredField(imgName);
			int resId = Integer.parseInt(field.get(null).toString());

			if (resId != 0) {
				return true;
			}
		}

		return false;
	}

	public static int getExpressionIndex(Context context, String chnName) {
		String[] expressions = context.getResources().getStringArray(
				R.array.expressions);

		List<String> chineseNames = Arrays.asList(expressions);

		return chineseNames.indexOf(chnName);

	}

}