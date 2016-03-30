package com.jeson.watercamera.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class BitmapUtils {
	/**
	 * ��ָ��·������λͼ����
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap getBitmap(String path) {
		return BitmapFactory.decodeFile(path);
	}

	/**
	 * 加密
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap getBitmap(String path, String end) {
		return BitmapFactory.decodeFile(path + end);
	}

	public static Bitmap getBitmap(byte[] data, int scale) {
		Options opts = new Options();
		opts.inSampleSize = scale;
		return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
	}

	/**
	 * ����ָ����ߵȱ������λͼ���ڴ�
	 * 
	 * @param data
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getBitmap(byte[] data, int width, int height) {
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		int xScale = opts.outWidth / width;
		int yScale = opts.outHeight / height;
		opts.inSampleSize = xScale > yScale ? xScale : yScale;
		opts.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
	}

	public static Bitmap getBitmap(Activity activity, int id, int width) {
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(activity.getResources(), id, opts);
		opts.inSampleSize = opts.outWidth / width;
		opts.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(activity.getResources(), id, opts);
	}

	/**
	 * ����ͼƬ��ָ���ļ�
	 * 
	 * @param bm
	 * @param file
	 */
	public static void save(Bitmap bm, File file) throws FileNotFoundException, IOException {
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream out = new FileOutputStream(file);

		bm.compress(CompressFormat.JPEG, 100, out);
		out.close();
	}

	/**
	 * 图片加密 在图片名字后面加“img”
	 * 
	 * @param bm
	 * @param file
	 */
	public static boolean save(Bitmap bm, String fileName) {
		try {
			File file = new File(fileName);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			if (!file.exists()) {
				file.createNewFile();
			}

			FileOutputStream out = new FileOutputStream(file);

			bm.compress(CompressFormat.JPEG, 100, out);
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void saveDrawable(Drawable drawable, String fileName) throws FileNotFoundException, IOException {
		File file = new File(fileName);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream out = new FileOutputStream(file);
		Bitmap bm = ((BitmapDrawable) drawable).getBitmap();
		bm.compress(CompressFormat.JPEG, 100, out);
		out.close();
	}

	public static Drawable getDrawable(String path) {
		Bitmap bm = getBitmap(path);
		return new BitmapDrawable(bm);
	}

	/***
	 * 等比例压缩图片
	 * 
	 * @param bitmap
	 * @param screenWidth
	 * @param screenHight
	 * @return
	 */
	public static Bitmap getBitmap(Bitmap bitmap, int screenWidth) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scale = (float) screenWidth / w;

		// scale = scale < scale2 ? scale : scale2;

		// 保证图片不变形.
		matrix.postScale(scale, scale);
		// w,h是原图的属性.
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}

	/**
	 * 宽度固定，等比例压缩图片,按matrix处理图片
	 * 
	 * @param bitmap
	 * @param horizontalWidth
	 * @param matrix
	 * @return
	 */
	public static Bitmap getBitmap(Bitmap bitmap, int horizontalWidth, Matrix matrix) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		float scale = (float) horizontalWidth / w;

		// scale = scale < scale2 ? scale : scale2;

		// 保证图片不变形.
		matrix.postScale(scale, scale);
		// w,h是原图的属性.
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}

	public static Bitmap getBitmap(String path, int screenWidth) {
		Bitmap bitmap = getBitmap(path);
		if (bitmap == null)
			return null;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scale = (float) screenWidth / w;

		// scale = scale < scale2 ? scale : scale2;

		// 保证图片不变形.
		matrix.postScale(scale, scale);
		// w,h是原图的属性.
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}

	public static Bitmap getBitmap(Bitmap bitmap, int screenWidth, int screenHight) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scale = (float) screenWidth / w;
		float scale2 = (float) screenHight / h;

		// scale = scale < scale2 ? scale : scale2;

		// 保证图片不变形.
		matrix.postScale(scale, scale);
		// w,h是原图的属性.
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}

}
