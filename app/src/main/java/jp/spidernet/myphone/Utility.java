package jp.spidernet.myphone;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jp.spidernet.myphone.tools.FileUtils;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

public class Utility {
	private static final String TAG = Utility.class.getSimpleName();
	private static HashMap<String, Integer> iconsMap = null;
	private static int THUMBNAIL_SIZE = 100;
	public interface MIMETYPE {
		String VIDEO_MPEG  = "video/mpeg";
		String JPEG  = "image/jpeg";
		String AUDIO_MPEG  = "audio/mpeg";
		String TEXT_PLAIN  = "text/plain";
		String TORRENT  = "application/x-bittorrent";
		String APK = "application/vnd.android.package-archive";
		
	}
	public static final String APK = "apk";
	public static final String TORRENT = "torrent";
	public static final String TXT = "txt";
	private static final int REPEATING_TIME = 10*60*1000; 
	
	public static int getFileExtensionId(String fileName) {
		String ext = getFileExtension(fileName);
		int id = getFileExtensionIconId(ext);
		return id;
	}

	private static int getFileExtensionIconId(String fileExtension) {
		if (fileExtension == null)
			return R.drawable.ic_unkown_file;
		if (iconsMap == null) {
			iconsMap = new HashMap<String, Integer>();
			iconsMap.put("3gp", R.drawable.ic_3gp);
			iconsMap.put("avi", R.drawable.ic_avi);
			iconsMap.put("bmp", R.drawable.ic_bmp);
			iconsMap.put("doc", R.drawable.ic_doc);
			iconsMap.put("docx", R.drawable.ic_doc);
			iconsMap.put("fla", R.drawable.ic_fla);
			iconsMap.put("flv", R.drawable.ic_flv);
			iconsMap.put("gif", R.drawable.ic_gif);
			iconsMap.put("html", R.drawable.ic_html);
			iconsMap.put("iso", R.drawable.ic_iso);
			iconsMap.put("jpg", R.drawable.ic_jpeg);
			iconsMap.put("jpeg", R.drawable.ic_jpeg);
			iconsMap.put("lock", R.drawable.ic_lock);
			iconsMap.put("mid", R.drawable.ic_mid);
			iconsMap.put("mkv", R.drawable.ic_mkv);
			iconsMap.put("mov", R.drawable.ic_mov);
			iconsMap.put("mp3", R.drawable.ic_mp3);
			iconsMap.put("mpg", R.drawable.ic_mpeg);
			iconsMap.put("pdf", R.drawable.ic_pdf);
			iconsMap.put("png", R.drawable.ic_png);
			iconsMap.put("ppt", R.drawable.ic_ppt);
			iconsMap.put("pptx", R.drawable.ic_ppt);
			iconsMap.put("swf", R.drawable.ic_swf);
			iconsMap.put("txt", R.drawable.ic_txt);
			iconsMap.put("wav", R.drawable.ic_wav);
			iconsMap.put("wma", R.drawable.ic_wma);
			iconsMap.put("xls", R.drawable.ic_xls);
			iconsMap.put("csv", R.drawable.ic_xls);
			iconsMap.put("xml", R.drawable.ic_xml);
			iconsMap.put("xlsx", R.drawable.ic_xls);
			iconsMap.put("rar", R.drawable.ic_rar);
			iconsMap.put("tar", R.drawable.ic_tar);
			iconsMap.put("zip", R.drawable.ic_zip2);
			iconsMap.put("mp4", R.drawable.ic_mp4);
			iconsMap.put("apk", R.drawable.ic_apk);
			iconsMap.put("torrent", R.drawable.ic_torrent);
		}
		Integer id = iconsMap.get(fileExtension.toLowerCase());
		if (id == null)
			id = R.drawable.ic_unkown_file;
		return id;
	}

	public static String getFileExtension(String fileName) {
		int mid = fileName.lastIndexOf(".");
		if (mid <= 0)
			return null;
		String ext = fileName.substring(mid + 1, fileName.length());
		return ext;
	}

	public static Bitmap getPreview(File image) {

		BitmapFactory.Options bounds = new BitmapFactory.Options();
		bounds.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(image.getPath(), bounds);
		if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
			return null;

		int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
				: bounds.outWidth;

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = originalSize / THUMBNAIL_SIZE;
		return BitmapFactory.decodeFile(image.getPath(), opts);
	}

	public static void sortFilesList(ArrayList<File> files) {
		// Obtain the array of (file, timestamp) pairs.
		int size = files.size();
		SortableFileName[] pairs = new SortableFileName[size];
		for (int i = 0; i < size; i++)
			pairs[i] = new SortableFileName(files.get(i));

		// Sort them by timestamp.
		Arrays.sort(pairs);

		// Take the sorted pairs and extract only the file part, discarding the
		// timestamp.
		files.clear();
		for (int i = 0; i < size; i++)
			files.add(pairs[i].f);
	}

	public static ArrayList<File> makeFilesArrayList(File[] files) {
		ArrayList<File> result = new ArrayList<File>();
		if (files != null)
			for (File file : files) {
				result.add(file);
			}
		return result;
	}

	public static String reportTraffic(long trafficPrint) {
		if ((trafficPrint / 1000000000) > 0) {
			return (trafficPrint / 1000000000 + " GB");
		} else if ((trafficPrint / 1000000) > 0) {
			return (trafficPrint / 1000000 + " MB");
		} else if ((trafficPrint / 1000) > 0) {
			return (trafficPrint / 1000 + " kB");
		} else {
			return (trafficPrint + " byte");
		}

	}

	public static Bitmap getIconInApkFile(File fileApk, Context context) {
		Bitmap bmp = null;

		if (fileApk.getPath().endsWith(".apk")) {
			String filePath = fileApk.getPath();
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageArchiveInfo(filePath,
							PackageManager.GET_ACTIVITIES);
			if (packageInfo != null) {
				ApplicationInfo appInfo = packageInfo.applicationInfo;
				if (Build.VERSION.SDK_INT >= 8) {
					appInfo.sourceDir = filePath;
					appInfo.publicSourceDir = filePath;
				}
				Drawable icon = appInfo.loadIcon(context.getPackageManager());
				bmp = ((BitmapDrawable) icon).getBitmap();
			}
		}
		return bmp;
	}
	
	public static boolean rename(File renamedFile, String newName) {
		boolean result = false;
		result = renamedFile.renameTo(new File(newName));
		return result;
	}
	
	public static void move(ArrayList<File> fromFiles, File toDir) {
		for (File file : fromFiles) {
			FileUtils.move(file, toDir);
		}
	}
	
	public static void move(String[] filesArray, File toDir) {
		ArrayList<File> fromFiles = new ArrayList<File>();
		for (String filePath : filesArray) {
			File file = new File(filePath);
			fromFiles.add(file);
		}
		move(fromFiles, toDir);
	}
	
	public static void copy(ArrayList<File> fromFiles, File toDir) {
		for (File file : fromFiles) {
			FileUtils.copyDirectory(file, toDir);
		}
	}
	
	public static void copy(String[] filesArray, File toDir) {
		ArrayList<File> fromFiles = new ArrayList<File>();
		for (String filePath : filesArray) {
			File file = new File(filePath);
			fromFiles.add(file);
		}
		copy(fromFiles, toDir);
	}
	public static void getSensorInfo(Context context) {
		// Sensor
		SensorManager sensorManager = (SensorManager) context
				.getSystemService(context.SENSOR_SERVICE);
		List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
		Log.v(TAG, String.format("Sensor Size: %d", sensorList.size()));
		for (Sensor sensor : sensorList) {
			Log.v(TAG, "************************");
			Log.v(TAG, String.format("Name: %s", sensor.getName()));
			Log.v(TAG, String.format("Type: %d", sensor.getType()));
			Log.v(TAG, String.format("Vendor: %s", sensor.getVendor()));
			Log.v(TAG, String.format("Version: %d", sensor.getVersion()));
			Log.v(TAG,
					String.format("MaximumRange: %f", sensor.getMaximumRange()));
			Log.v(TAG, String.format("MinDelay: %d", sensor.getMinDelay()));
			Log.v(TAG, String.format("Power: %f", sensor.getPower()));
			Log.v(TAG, String.format("Resolution: %f", sensor.getResolution()));
		}
	}
	
	public static void getLocationInfo(Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		List<String> providerList = locationManager.getAllProviders();
		for (String string : providerList) {
			Log.v(TAG, String.format("Name: %s",string));
		}
//		LocationProvider locationProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		String provider = locationManager.getBestProvider(criteria, true);
		Log.v(TAG, "provider"+provider);
	}
	
	public static void startAtlantis(Context context) {
		// Schedule the alarm!
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		// repeat
		long firstTime = SystemClock.elapsedRealtime();
		firstTime += REPEATING_TIME;
		PendingIntent alarmSender = PendingIntent.getBroadcast(context, 0, new Intent(context,
				AlarmReceiver.class), 0);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
				REPEATING_TIME, alarmSender);
	}
}
