package com.jeson.imdemo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.util.Base64;

/**
 * Bitmap工具
 * 
 * @author jiang
 * 
 */
public class BitmapUtil {

	/**
	 * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1.
	 * 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	 * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
	 * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
	 * 
	 * @param imagePath
	 *            图像的路径
	 * @param width
	 *            指定输出图像的宽度
	 * @param height
	 *            指定输出图像的高度
	 * @return 生成的缩略图
	 */
	public static Bitmap getImageThumbnail(String imagePath) {
		Bitmap bitmap = null;
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeFile(imagePath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 300f;// 这里设置高度为800f
		float ww = 160f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		int width = newOpts.outWidth / be;
		int height = newOpts.outHeight / be;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, newOpts);

		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1.
	 * 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	 * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
	 * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
	 * 
	 * @param imagePath
	 *            图像的路径
	 * @param width
	 *            指定输出图像的宽度
	 * @param height
	 *            指定输出图像的高度
	 * @return 指定输出图像的高度
	 */
	public static Bitmap getThumbnailSize(String imagePath) {
		int width = 120;
		int height = 120;
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		bitmap = BitmapUtil.toRoundCornerBitmap(bitmap);
		return bitmap;
	}

	/**
	 * 获取视频的缩略图 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
	 * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
	 * 
	 * @param videoPath
	 *            视频的路径
	 * @param width
	 *            指定输出视频缩略图的宽度
	 * @param height
	 *            指定输出视频缩略图的高度度
	 * @param kind
	 *            参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
	 *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
	 * @return 指定大小的视频缩略图
	 */
	public static Bitmap getVideoThumbnail(String videoPath, int width,
			int height, int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * 获取正方形 切图 方法
	 * 
	 * 将原图切为 以其长宽中较小一个为边长 获取中间图像的正方形图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getSqureBmp(Bitmap bitmap) {
		int w = bitmap.getWidth(); // 得到图片的宽，高
		int h = bitmap.getHeight();
		int wh = w > h ? h : w;// 裁切后所取的正方形区域边长
		int retX = w > h ? (w - h) / 2 : 0;// 基于原图，取正方形左上角x坐标
		int retY = w > h ? 0 : (h - w) / 2;
		return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
	}

	public static void compressBmpToFile(Bitmap bmp, File file) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = 100;
		if (bmp != null) {
			bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
			while (baos.toByteArray().length / 1024 > 300) {
				baos.reset();
				options -= 10;
				bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
			}
			try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(baos.toByteArray());
				fos.flush();
				fos.close();
				if (bmp != null && !bmp.isRecycled()) {
					bmp.recycle();
					bmp = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = 100;
		image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		while (baos.toByteArray().length / 1024 > 300) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			options -= 10;// 每次都减少10
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		} catch (OutOfMemoryError e) {
			return image;
		}

		if (image != null) {
			image.recycle();
			image = null;
		}

		return bitmap;
	}

	public static Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = 1;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		if (bitmap == null) {
			return null;
		}
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 计算图片的缩放值
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	/**
	 * 根据路径获得图片并压缩，返回bitmap用于显示(头像专用)
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap getSmallHeadBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 160, 160);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}

	public static Bitmap getSmallHeadBitmap(ByteArrayInputStream inputStream) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(inputStream, null, options);
		options.inSampleSize = calculateInSampleSize(options, 160, 160);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeStream(inputStream, null, options);
	}

	/**
	 * 根据路径获得图片并压缩，返回bitmap用于显示
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap getSmallBitmap(String filePath, int phoneWidth,
			int phoneHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, phoneWidth,
				phoneHeight);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 
	 * @param bitmap
	 *            要旋转的图片
	 * @param degree
	 *            旋转的角度
	 * @return 这是旋转后的图片
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, float degree) {
		if (bitmap != null) {
			Matrix matrix = new Matrix();
			matrix.postRotate(degree);
			try {
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix, true);
			} catch (OutOfMemoryError e) {
				return bitmap;
			}

			return bitmap;
		} else {
			return bitmap;
		}

	}

	/**
	 * 
	 * 根据给定压缩格式将bitmap转换成byte数组
	 * 
	 * @param bitmap
	 * @param format
	 *            压缩格式
	 */
	public static byte[] getByteFromBitmap(Bitmap bitmap, CompressFormat format) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(format, 100, baos);
		byte[] byteArray = baos.toByteArray();
		try {
			baos.close();
		} catch (IOException e) {
			baos = null;
			e.printStackTrace();
		}
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
		return byteArray;
	}

	/**
	 * 圆形图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap toRoundedBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			try {
				Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
						bitmap.getHeight(), Config.ARGB_8888);
				Canvas canvas = new Canvas(output);

				final int color = 0xff424242;
				final Paint paint = new Paint();
				final Rect rect = new Rect(0, 0, bitmap.getWidth(),
						bitmap.getHeight());
				final RectF rectF = new RectF(rect);
				final float roundPx = bitmap.getWidth() / 2;
				final float roundPy = bitmap.getHeight() / 2;

				paint.setAntiAlias(true);
				canvas.drawARGB(0, 0, 0, 0);
				paint.setColor(color);
				canvas.drawRoundRect(rectF, roundPx, roundPy, paint);

				paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
				canvas.drawBitmap(bitmap, rect, rect, paint);
				return output;
			} catch (java.lang.OutOfMemoryError e) {
				e.printStackTrace();
				return bitmap;
			}
		} else {
			return null;
		}

	}

	/**
	 * 加圆角
	 * 
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap toRoundCornerBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			try {
				Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
						bitmap.getHeight(), Config.ARGB_4444);
				Canvas canvas = new Canvas(output);

				final int color = 0xff424242;

				final Paint paint = new Paint();

				final Rect rect = new Rect(0, 0, bitmap.getWidth(),
						bitmap.getHeight());

				final RectF rectF = new RectF(rect);

				// final float roundPx = bitmap.getWidth() / 8;
				// final float roundPy = bitmap.getHeight() / 8;

				paint.setAntiAlias(true);

				canvas.drawARGB(0, 0, 0, 0);

				paint.setColor(color);

				canvas.drawRoundRect(rectF, 15, 15, paint);

				paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

				canvas.drawBitmap(bitmap, rect, rect, paint);

				return output;
			} catch (java.lang.OutOfMemoryError e) {
				e.printStackTrace();
				return bitmap;
			}
		} else {
			return null;
		}

	}

	/**
	 * 依据给定byte数组获得bitmap
	 * 
	 * @param byteArray
	 * @return
	 */
	public static Bitmap getBitmapFromByteArray(byte[] byteArray) {
		ByteArrayInputStream baos = new ByteArrayInputStream(byteArray);
		return BitmapFactory.decodeStream(baos);
	}

	/**
	 * 
	 * 根据指定压缩格式获得bitmap的Base64字符串
	 * 
	 * @param bitmap
	 * @param format
	 *            压缩格式
	 */
	public static String getBase64StringFromBitmap(Bitmap bitmap,
			CompressFormat format) {

		String file = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(format, 100, baos);
		file = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
		try {
			baos.close();
		} catch (IOException e) {
			baos = null;
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * 根据给定Base64字符串获得Bitmap
	 * 
	 * @param base64
	 * @return
	 */
	public static Bitmap getBitmapFromBase64String(String base64) {
		byte[] byteArray = Base64.decode(base64, Base64.DEFAULT);
		ByteArrayInputStream baos = new ByteArrayInputStream(byteArray);
		return BitmapFactory.decodeStream(baos);
	}

	/**
	 * 圆形文字头像
	 * 
	 * @param context
	 * @param hasLogin
	 * @param word
	 * @return
	 */
	public static Bitmap getRoundedHeadWordBitmap(Context context,
			boolean hasLogin, String word, int imageType) {
		Bitmap wordBmp = null;
		try {
			Bitmap srcBmp;
			if (hasLogin) {
				if (imageType == 0) {
					imageType = 1;
				}
				srcBmp = BitmapFactory.decodeResource(
						context.getResources(),
						context.getResources().getIdentifier(
								"head_img" + imageType, "drawable",
								context.getPackageName()));
			} else {
				srcBmp = BitmapFactory.decodeResource(
						context.getResources(),
						context.getResources().getIdentifier(
								"unlogin_head_img_bg", "drawable",
								context.getPackageName()));
			}

			srcBmp = toRoundedBitmap(srcBmp);

			int width = srcBmp.getWidth();
			int hight = srcBmp.getHeight();

			wordBmp = Bitmap.createBitmap(width, hight, srcBmp.getConfig()); // 建立一个空的BItMap
			Canvas canvas = new Canvas(wordBmp);// 初始化画布绘制的图像到icon上

			Paint paint = new Paint();
			paint.setDither(true); // 获取跟清晰的图像采样
			paint.setFilterBitmap(true);// 过滤一些

			Rect src = new Rect(0, 0, srcBmp.getWidth(), srcBmp.getHeight());// 创建一个指定的新矩形的坐标
			Rect dst = new Rect(0, 0, width, hight);
			canvas.drawBitmap(srcBmp, src, dst, paint);// 将photo缩放或扩大到dst使用的填充区

			Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
					| Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
			float density = Tools.getDeviceDensity(context);
			textPaint.setTextSize(35 * density);
			textPaint.setTypeface(Typeface.DEFAULT);// 采用默认的宽度
			textPaint.setColor(Color.WHITE);// 采用的颜色
			// textPaint.setShadowLayer(3f,
			// 1,1,this.getResources().getColor(android.R.color.background_dark));//阴影的设置
			float fontWidth = textPaint.measureText(word);
			FontMetrics fm = textPaint.getFontMetrics();
			float fontHeight = fm.descent - fm.ascent;
			float fontLeading = fm.leading - fm.ascent;

			canvas.drawText(word, (width - fontWidth) / 2, (hight - fontHeight)
					/ 2 + fontLeading, textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
			if (srcBmp != null && !srcBmp.isRecycled()) {
				srcBmp.recycle();
				srcBmp = null;
			}
		} catch (OutOfMemoryError e) {
			return null;
		}
		return wordBmp;
	}

	/**
	 * 部门圆形文字头像
	 * 
	 * @param context
	 * @param hasLogin
	 * @param word
	 * @return
	 */
	public static Bitmap getRoundedDepartWordBitmap(Context context, String word) {
		Bitmap wordBmp = null;
		try {
			Bitmap srcBmp;
			srcBmp = BitmapFactory.decodeResource(
					context.getResources(),
					context.getResources().getIdentifier("head_img1",
							"drawable", context.getPackageName()));

			srcBmp = toRoundedBitmap(srcBmp);

			int width = srcBmp.getWidth();
			int hight = srcBmp.getHeight();

			wordBmp = Bitmap.createBitmap(width, hight, srcBmp.getConfig()); // 建立一个空的BItMap
			Canvas canvas = new Canvas(wordBmp);// 初始化画布绘制的图像到icon上

			Paint paint = new Paint();
			paint.setDither(true); // 获取跟清晰的图像采样
			paint.setFilterBitmap(true);// 过滤一些

			Rect src = new Rect(0, 0, srcBmp.getWidth(), srcBmp.getHeight());// 创建一个指定的新矩形的坐标
			Rect dst = new Rect(0, 0, width, hight);
			canvas.drawBitmap(srcBmp, src, dst, paint);// 将photo缩放或扩大到dst使用的填充区

			Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
					| Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
			float density = Tools.getDeviceDensity(context);
			textPaint.setTextSize(35 * density);
			textPaint.setTypeface(Typeface.DEFAULT);// 采用默认的宽度
			textPaint.setColor(Color.WHITE);// 采用的颜色
			// textPaint.setShadowLayer(3f,
			// 1,1,this.getResources().getColor(android.R.color.background_dark));//阴影的设置
			float fontWidth = textPaint.measureText(word);
			FontMetrics fm = textPaint.getFontMetrics();
			float fontHeight = fm.descent - fm.ascent;
			float fontLeading = fm.leading - fm.ascent;

			canvas.drawText(word, (width - fontWidth) / 2, (hight - fontHeight)
					/ 2 + fontLeading, textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
			if (srcBmp != null && !srcBmp.isRecycled()) {
				srcBmp.recycle();
				srcBmp = null;
			}
		} catch (OutOfMemoryError e) {
			return null;
		}

		return wordBmp;
	}

	/**
	 * 获得文字头像
	 * 
	 * @param context
	 * @param hasLogin
	 * @param word
	 * @return
	 */
	public static Bitmap getHeadWordBitmap(Context context, boolean hasLogin,
			String word) {
		Bitmap wordBmp = null;
		try {
			Bitmap srcBmp;
			if (hasLogin) {
				srcBmp = BitmapFactory.decodeResource(
						context.getResources(),
						context.getResources().getIdentifier(
								"haslogin_head_img_bg", "drawable",
								context.getPackageName()));
			} else {
				srcBmp = BitmapFactory.decodeResource(
						context.getResources(),
						context.getResources().getIdentifier(
								"unlogin_head_img_bg", "drawable",
								context.getPackageName()));
			}

			int width = srcBmp.getWidth();
			int hight = srcBmp.getHeight();

			wordBmp = Bitmap.createBitmap(width, hight, srcBmp.getConfig()); // 建立一个空的BItMap
			Canvas canvas = new Canvas(wordBmp);// 初始化画布绘制的图像到icon上

			Paint paint = new Paint();
			paint.setDither(true); // 获取跟清晰的图像采样
			paint.setFilterBitmap(true);// 过滤一些

			Rect src = new Rect(0, 0, srcBmp.getWidth(), srcBmp.getHeight());// 创建一个指定的新矩形的坐标
			Rect dst = new Rect(0, 0, width, hight);
			canvas.drawBitmap(srcBmp, src, dst, paint);// 将photo缩放或扩大到dst使用的填充区

			Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
					| Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
			float density = Tools.getDeviceDensity(context);
			textPaint.setTextSize(35 * density);
			textPaint.setTypeface(Typeface.DEFAULT);// 采用默认的宽度
			textPaint.setColor(Color.WHITE);// 采用的颜色
			// textPaint.setShadowLayer(3f,
			// 1,1,this.getResources().getColor(android.R.color.background_dark));//阴影的设置
			float fontWidth = textPaint.measureText(word);
			FontMetrics fm = textPaint.getFontMetrics();
			float fontHeight = fm.descent - fm.ascent;
			float fontLeading = fm.leading - fm.ascent;
			canvas.drawText(word, (width - fontWidth) / 2, (hight - fontHeight)
					/ 2 + fontLeading, textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
			if (srcBmp != null && !srcBmp.isRecycled()) {
				srcBmp.recycle();
				srcBmp = null;
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		}

		return wordBmp;
	}

	public static byte[] bmpToByteArray(final Bitmap bmp,
			final boolean needRecycle) {
		int i;
		int j;
		if (bmp.getHeight() > bmp.getWidth()) {
			i = bmp.getWidth();
			j = bmp.getWidth();
		} else {
			i = bmp.getHeight();
			j = bmp.getHeight();
		}

		Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
		Canvas localCanvas = new Canvas(localBitmap);

		while (true) {
			localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i,
					j), null);
			if (needRecycle)
				bmp.recycle();
			ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
			localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
					localByteArrayOutputStream);
			localBitmap.recycle();
			byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
			try {
				localByteArrayOutputStream.close();
				return arrayOfByte;
			} catch (Exception e) {
				// F.out(e);
			}
			i = bmp.getHeight();
			j = bmp.getHeight();
		}
	}

}
