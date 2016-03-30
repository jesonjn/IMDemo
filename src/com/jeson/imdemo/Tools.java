package com.jeson.imdemo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

/**
 * 公共工具类
 * 
 * @author zhugl
 * @version 1.0
 * @since um1.0
 */

public class Tools {
	private static java.text.SimpleDateFormat dateFormat1 = new SimpleDateFormat(
			"yyyy-MM-dd");

	private static java.text.SimpleDateFormat dateFormat2 = new SimpleDateFormat(
			"yyyyMMdd");

	public static String VERSION = null;

	public static String macAddr = "";

	public static float density;

	private static final String PACKAGE_NAME = "com.huatai.esales.client.system.util";

	public static String getVersion(Context context) {
		if (null == VERSION) {
			try {
				VERSION = context.getPackageManager().getPackageInfo(
						PACKAGE_NAME, PackageManager.GET_CONFIGURATIONS).versionName;
			} catch (NameNotFoundException e) {
				VERSION = "";
			}
		}
		return VERSION;
	}

	/**
	 * 判断是否为最新版本方法
	 * 
	 * @param localVersion
	 *            本地版本号
	 * @param onlineVersion
	 *            线上版本号
	 * @return boolean
	 */
	public static boolean isAppNewVersion(String localVersion,
			String onlineVersion) {
		if (localVersion.equals(onlineVersion)) {
			return false;
		}
		String[] locArray = localVersion.split("\\.");
		String[] onlineArray = onlineVersion.split("\\.");

		int length = locArray.length < onlineArray.length ? locArray.length
				: onlineArray.length;

		for (int i = 0; i < length; i++) {
			if (Integer.parseInt(locArray[i]) > Integer
					.parseInt(onlineArray[i])) {
				return false;
			} else if (Integer.parseInt(locArray[i]) < Integer
					.parseInt(onlineArray[i])) {
				return true;
			}
		}

		return true;
	}

	/**
	 * 当前手机是否有可用网络 (所有网络类型)
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnect(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {

					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {

						if (info.isAvailable()) {

							return true;
						}
					}
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 当前 环境是否wifi已连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnect(Context context) {
		// 获取手机所有连接管理对象
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
				// 如果当前手机没有任何网络连接时,networkInfo == null
				if (null != networkInfo) {
					return "wifi".equalsIgnoreCase(networkInfo.getTypeName());
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 系统提供的数组拷贝方法arraycopy
	 * */
	public static byte[] sysCopy(List<byte[]> srcArrays) {
		int len = 0;
		for (byte[] srcArray : srcArrays) {
			len += srcArray.length;
		}
		byte[] destArray = new byte[len];
		int destLen = 0;
		for (byte[] srcArray : srcArrays) {
			System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
			destLen += srcArray.length;
		}
		return destArray;
	}

	/**
	 * 借助字节输出流ByteArrayOutputStream来实现字节数组的合并
	 * */
	public static byte[] streamCopy(List<byte[]> srcArrays) {
		byte[] destAray = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			for (byte[] srcArray : srcArrays) {
				bos.write(srcArray);
			}
			bos.flush();
			destAray = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
			}
		}
		return destAray;
	}

	/**
	 * 获取 wifi 信号强度
	 * 
	 * @param ctx
	 * @return
	 */
	public static int getWifiRssi(Context ctx) {
		WifiManager mWifiManager = (WifiManager) ctx
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		int wifi = wifiInfo.getRssi(); // 取 wifi 信号强度
		return wifi;
	}

	/**
	 * 
	 * @param strDate
	 *            eg: 2012-05-01
	 * @return
	 */
	public static boolean isInCurrentMonth(String strDate) {
		boolean yesIn = false;
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH);
		month += 1;
		String strMonth = (strDate.length() == 10 && strDate.indexOf("-") != -1) ? strDate
				.substring(5, 7) : "";
		if (!strMonth.equals("") && Integer.parseInt(strMonth) == month)
			yesIn = true;
		return yesIn;
	}

	/**
	 * 转换String为long
	 * 
	 * @param s
	 *            要转换的字符串
	 * @return long
	 */
	public static long toLong(java.lang.String s) {
		try {
			if (s == null)
				return 0L;
			return java.lang.Long.valueOf(s).longValue();
		} catch (java.lang.Exception exception) {

			return 0L;
		}

	}

	/**
	 * 转换String为int
	 * 
	 * @param s
	 *            要转换的字符串
	 * @return int
	 */
	public static int toInteger(java.lang.String s) {
		try {
			if (s == null) {
				return 0;
			}
			return (new Integer(s)).intValue();
		} catch (java.lang.Exception exception) {

			return 0;
		}
	}

	/**
	 * 转换String为double
	 * 
	 * @param s
	 *            要转换的字符串
	 * @return double
	 */
	public static double toDouble(java.lang.String s) {
		try {
			if (s == null)
				return 0.0D;
			return java.lang.Double.valueOf(s).doubleValue();
		} catch (java.lang.Exception exception) {

			return 0L;
		}
	}

	/**
	 * 转换String为float
	 * 
	 * @param s
	 *            要转换的字符串
	 * @return float
	 */
	public static float toFloat(java.lang.String s) {
		try {
			if (s == null)
				return 0.0F;
			return java.lang.Float.valueOf(s).floatValue();
		} catch (java.lang.Exception exception) {

			return 0L;
		}
	}

	/**
	 * 如果字符串为null,返回""
	 * 
	 * @param s
	 *            要转换的对象
	 * @return String
	 */
	public static java.lang.String toGB(java.lang.String s) {
		try {
			if (s == null)
				return "";
			return s;
		} catch (java.lang.Exception exception) {

			return "";
		}
	}

	/**
	 * 转换字符串为Date
	 * 
	 * @param s
	 *            要转换的对象
	 * @return Date
	 */
	public static java.util.Date toDate(java.lang.String s)
			throws java.text.ParseException {
		java.util.Date date = null;
		if (s == null)
			date = null;
		else if (s.length() == 10 && s.indexOf("-") == 4)
			date = dateFormat1.parse(s);
		else if (s.length() == 8)
			date = dateFormat2.parse(s);
		return date;
	}

	/**
	 * 转换字符串为BigDecimal
	 * 
	 * @param s
	 *            要转换的对象
	 * @return BigDecimal
	 */
	public static java.math.BigDecimal toBigDecimal(java.lang.String s) {
		java.math.BigDecimal bigdecimal = null;
		if (s == null)
			return null;
		try {
			bigdecimal = new BigDecimal(s);
		} catch (java.lang.Exception exception) {
			return null;
		}
		return bigdecimal;
	}

	/**
	 * 转换字符串为Timestamp
	 * 
	 * @param s
	 *            要转换的对象
	 * @return Timestamp
	 */
	public static Timestamp toTimestamp(String s) {
		if (s == null || "".equals(s)) {
			return null;
		} else {
			java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(s
					+ " 00:00:00.000000000");
			return timestamp;
		}
	}

	/**
	 * \
	 * 
	 * @param files
	 * @return
	 */

	public static String findFile(File files, String endfilename) {
		if (files.isDirectory()) {
			File[] file = files.listFiles();
			for (File f : file) {
				String fileNamePath = findFile(f, endfilename);
				if (null != fileNamePath) {
					return fileNamePath;
				}

			}
		} else if (files.isFile()) {
			String filename = files.getName().toLowerCase();
			if (filename.trim().equals(endfilename.trim())) {

				return files.getPath();
			}

		}
		return null;

	}

	/**
	 * \
	 * 
	 * @param files
	 * @return
	 */

	public static List<String> findFileSuffix(File files, String fileSuffix) {
		List<String> fileList = new ArrayList<String>();
		if (files.isDirectory()) {
			File[] file = files.listFiles();
			for (File f : file) {
				String fileNamePath = findFile(f, fileSuffix);
				if (null != fileNamePath) {
					fileList.add(fileNamePath);
				}

			}
		} else if (files.isFile()) {
			String filename = files.getName().toLowerCase();
			if (filename.trim().endsWith(fileSuffix.trim())) {
				fileList.add(files.getPath());
				return fileList;
			}

		}
		return fileList;

	}

	/** 递归删除文件及文件夹 **/
	public static void delete(File file) {

		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}

	}

	public static String[] getImageNames(String folderPath, String fixName) {

		File file01 = new File(folderPath);

		String[] files01 = file01.list();

		int imageFileNums = 0;
		for (int i = 0; i < files01.length; i++) {
			File file02 = new File(folderPath + File.separator + files01[i]);

			if (!file02.isDirectory()) {

				if (isImageFile(file02.getName(), fixName)) {

					imageFileNums++;
				}
			}
		}

		String[] files02 = new String[imageFileNums];

		int j = 0;
		for (int i = 0; i < files01.length; i++) {
			File file02 = new File(folderPath + File.separator + files01[i]);

			if (!file02.isDirectory()) {

				if (isImageFile(file02.getName(), fixName)) {
					files02[j] = file02.getPath();
					j++;
				}
			}
		}
		return files02;
	}

	private static boolean isImageFile(String fileName, String fixName) {
		String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length());
		if (fileEnd.equalsIgnoreCase(fixName)) {
			return true;
		} else {
			return false;
		}
	}

	// 在wifi未开启状态下，仍然可以获取MAC地址，但是IP地址必须在已连接状态下否则为0
	public static String getMacAddress(Activity activity) {
		if (macAddr.equals("")) {
			String macAddress = null, ip = null;
			WifiManager wifiMgr = (WifiManager) activity
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = (null == wifiMgr ? null : wifiMgr
					.getConnectionInfo());
			if (null != info) {
				macAddress = info.getMacAddress();
				ip = int2ip(info.getIpAddress());
			}
			macAddr = macAddress;
			return macAddress;
		} else {
			return macAddr;
		}
	}

	public static String int2ip(long ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}

	public static boolean regexName(String str) {
		Pattern pattern = Pattern.compile("^[\u4e00-\u9fa5]{2,10}");

		Matcher matcher = pattern.matcher(str);

		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 统计字符串中汉字个数
	 */
	public static int getLenOfString(String nickname) {
		// 汉字个数
		int chCnt = 0;
		String regEx = "[\u4e00-\u9fa5]"; // 如果考虑繁体字，u9fa5-->u9fff
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(regEx);
		java.util.regex.Matcher m = p.matcher(nickname);
		while (m.find()) {
			chCnt++;
		}
		return chCnt;
	}

	public static boolean checkEmailValid(String strEmail) {
		if (strEmail == null) {
			return false;
		}
		String check = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(strEmail);
		boolean isMatched = matcher.matches();
		return isMatched;

	}

	/**
	 * 验证包括座机
	 * 
	 * @param phoneNumber
	 * @return
	 */
	public static boolean checkPhoneNumberValid(String phoneNumber) {
		if (TextUtils.isEmpty(phoneNumber)) {
			return false;
		}

		String check = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9]{1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-?\\d{7,8}-(\\d{1,4})$))";

		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(phoneNumber);
		boolean isMatched = matcher.matches();
		return isMatched;
	}

	public static boolean checkMobileNumberValid(String strPhoneNum) {

		if (strPhoneNum == null) {
			return false;
		}
		String checkphone = "^((13[0-9])|(15[^4,\\D])|(16[0-9])|(170)|(18[0-9]))\\d{8}$";

		Pattern pattern = Pattern.compile(checkphone);

		Matcher matcher = pattern.matcher(strPhoneNum);

		return matcher.matches();

	}

	/**
	 * 设置文字宽度
	 * 
	 * @param title
	 * @return
	 */
	public static int getTextWidth(String title, float density) {
		Paint paint = new Paint();
		paint.setTextSize(16.0f);// 设置字符大小

		int textWidth = getStringWidth(paint, title, density);
		// int textWidth = (int)paint.measureText(title, 0, title.length());
		return textWidth;

	}

	public static int getStringWidth(Paint paint, String str, float density) {
		int iRet = 0;
		Rect rect = new Rect();

		paint.getTextBounds(str, 0, str.length(), rect);
		iRet = rect.width() + 1;
		iRet = (int) (iRet * density + 0.5) + 20;
		return iRet;
	}



	/**
	 * 是否已安装该应用程序
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isAviliable(Context context, String packageName) {
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> infoList = packageManager.getInstalledPackages(0);// 获取已安装应用程序包的信息
		if (infoList != null) {
			for (PackageInfo pInfo : infoList) {
				if (packageName.equals(pInfo.packageName)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 设置View尺寸满足屏幕尺寸
	 * 
	 * @param context
	 * @param view
	 * @param bitmap
	 */
	public static void setViewSizeFillScrean(Context context, View view,
			Bitmap bitmap) {
		DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		int width = displayMetrics.widthPixels - Tools.dip2px(context, 50);
		int height = width * bitmap.getHeight() / bitmap.getWidth();
		LayoutParams lp = view.getLayoutParams();
		lp.width = width;
		lp.height = height;
		view.setLayoutParams(lp);
	}

	// dp转换px
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	// 获取设备密度
	public static float getDeviceDensity(Context context) {
		if (density == 0) {
			DisplayMetrics displayMetrics = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay()
					.getMetrics(displayMetrics);
			density = displayMetrics.density;
		}
		return density;
	}

	public static void setDeviceDensity(float density) {
		Tools.density = density;
	}

	private static byte[] getSign(Context context) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packageInfo = null;
		try {
			packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), PackageManager.GET_SIGNATURES);
		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}
		Signature[] signatures = packageInfo.signatures;
		return signatures[0].toByteArray();
	}

	public static String getAppSignaturePublicKey(Context context) {
		try {
			CertificateFactory certificateFactory = CertificateFactory
					.getInstance("X.509");
			X509Certificate certificate = (X509Certificate) certificateFactory
					.generateCertificate(new ByteArrayInputStream(
							getSign(context)));
			String publicKey = certificate.getPublicKey().toString();
			// String RSAPublicKey = publicKey.substring(
			// publicKey.indexOf("modulus") + 7,
			// publicKey.indexOf("public"));
			// publicKey = RSAPublicKey.replaceAll("\\W", "").trim();
			return publicKey;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * b转为m
	 * 
	 * @param pro
	 * @return
	 */
	public static String bToM(long pro) {

		double proTemp = pro;
		double diver = 1024 * 1024;
		BigDecimal bigPro = new BigDecimal(proTemp + "");
		BigDecimal bigAll = new BigDecimal(diver + "");
		BigDecimal proDou = bigPro.divide(bigAll, 2, BigDecimal.ROUND_HALF_UP);
		return proDou.toString() + "MB";
	}

	/**
	 * 获取百分比
	 * 
	 * @param pro
	 * @param all
	 * @return
	 */
	public static String getProgress(long pro, long all) {

		if (all > 0 && pro <= all && pro > 0) {
			double proTemp = (double) pro * 100;
			double allTemp = all;
			BigDecimal bigPro = new BigDecimal(proTemp + "");
			BigDecimal bigAll = new BigDecimal(allTemp + "");
			BigDecimal proDou = bigPro.divide(bigAll, 2,
					BigDecimal.ROUND_HALF_UP);
			return proDou.toString() + "%";
		} else {
			return "0.00%";
		}
	}

	/**
	 * 获取文件大小进度 当前进度 cM/aM
	 * 
	 * @param pro
	 * @param all
	 * @return
	 */
	public static String getProgressSize(long pro, long all) {
		if (all > 0 && pro <= all && pro > 0) {
			return bToM(pro) + "/" + bToM(all);
		} else {
			return "";
		}

	}

	/**
	 * 手机号码中间几位隐藏
	 * 
	 * @param phoneNumber
	 *            要隐藏的手机号码
	 * @return
	 */
	public static String convertPhonenumber(String phoneNumber) {

		return phoneNumber.substring(0, 3) + "****"
				+ phoneNumber.substring(7, phoneNumber.length());
	}
}
