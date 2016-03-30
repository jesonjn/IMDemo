package com.jeson.watercamera;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jeson.imdemo.R;
import com.jeson.watercamera.util.BitmapUtils;

public class WaterPhotoActivity extends Activity {
	private static final String TAG = "WaterPhotoActivity";
	private Context mContext;
	private ImageView mImageView;
	private TextView mTiaoZheng;
	private RelativeLayout mWaterPhoto;
	private TextView mSure, mCancle;
	private int waterType;
	private int scroll = 0;
	private final String SAVE_BASE_PATH = Environment
			.getExternalStorageDirectory() + "/waterphoto/";

	private Bitmap bitmap;
	private ViewPager mViewPager;
	private List<View> views = new ArrayList<View>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waterphoto_save);
		mContext = this;
		findView();
		init();
	}

	private void findView() {
		mImageView = (ImageView) findViewById(R.id.water_photo);
		mTiaoZheng = (TextView) findViewById(R.id.tiaozheng);
		mSure = (TextView) findViewById(R.id.sure);
		mCancle = (TextView) findViewById(R.id.cancle);
		mWaterPhoto = (RelativeLayout) findViewById(R.id.rl_water);
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
	}

	private void init() {
		waterType = getIntent().getExtras().getInt("waterType");
		byte[] data = getIntent().getByteArrayExtra("data");
		bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		scrollImageView(bitmap);

		mTiaoZheng.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (bitmap != null) {
					scrollImageView(bitmap);
				}
			}
		});

		mSure.setOnClickListener(new OnClickListener() {
			private ArrayList<String> list = new ArrayList<String>();

			@Override
			public void onClick(View v) {
				String scroll_type = scroll + "" + waterType;
				if (list.contains(scroll_type)) {
					return;
				}
				Bitmap bitmap = getScreenShot(mWaterPhoto);
				String photoName = System.currentTimeMillis() + ".jpg";
				String path = SAVE_BASE_PATH + photoName;
				boolean isOk = BitmapUtils.save(bitmap, path);
				if (isOk) {
					list.add(scroll_type);
					Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
					bitmap.recycle();
				}
			}
		});
		mCancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cancel();
			}

		});
		// viewPager
		LayoutInflater inflater = LayoutInflater.from(mContext);
		views.add(inflater.inflate(R.layout.water_camera_page1, null));
		views.add(inflater.inflate(R.layout.water_camera_page2, null));
		views.add(inflater.inflate(R.layout.water_camera_page3, null));
		views.add(inflater.inflate(R.layout.water_camera_page4, null));
		views.add(inflater.inflate(R.layout.water_camera_page5, null));

		mViewPager.setAdapter(new MyViewPagerAdapter());
		mViewPager.setOnPageChangeListener(new MyOnPagerChangeListener());
		mViewPager.setCurrentItem(waterType);
	}

	private void scrollImageView(Bitmap bitmap) {
		scroll += 90;
		if (scroll >= 360) {
			scroll = 0;
		}
		Matrix matrix = new Matrix();
		matrix.preRotate(scroll);
		bitmap = BitmapUtils.getBitmap(bitmap, bitmap.getWidth(), matrix);
		mImageView.setImageBitmap(bitmap);
	}

	// 获取指定Activity的截屏，保存到png文件
	private Bitmap getScreenShot(RelativeLayout waterPhoto) {
		// View是你需要截图的View
		View view = waterPhoto;
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();

		/*
		 * // 获取状态栏高度 Rect frame = new Rect();
		 * activity.getWindow().getDecorView(
		 * ).getWindowVisibleDisplayFrame(frame); int statusBarHeight =
		 * frame.top; System.out.println(statusBarHeight);
		 */

		/*
		 * // 获取屏幕长和高 int width =
		 * activity.getWindowManager().getDefaultDisplay().getWidth(); int
		 * height = activity.getWindowManager().getDefaultDisplay()
		 * .getHeight();
		 */

		// 获取长和高
		int width = view.getWidth();
		int height = view.getHeight();

		// 去掉标题栏
		// Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
		Bitmap b = Bitmap.createBitmap(b1, 0, 0, width, height);
		view.destroyDrawingCache();
		return b;
	}

	private void cancel() {
		bitmap.recycle();
		finish();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			cancel();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// ////////////////////////////////////////////////////////////////////
	// viewPager
	private class MyViewPagerAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(views.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(views.get(position));
			return views.get(position);
		}

	}

	private class MyOnPagerChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			waterType = arg0;
		}

	}
}
