package jp.spidernet.myphone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class StartUpService extends Service {

	private static final int REPEATING_TIME = 10*60*1000; //10分
	private static final String TAG = "StartUpService";
	private PendingIntent mAlarmSender;

	public StartUpService() {
	}

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate() was called");
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "onStartCommand() was called");
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onStart(Intent intent, int startId) {
		Log.v(TAG, "onStart() was called");
		super.onStart(intent, startId);
		mAlarmSender = PendingIntent.getBroadcast(this, 0, new Intent(this,
				AlarmReceiver.class), 0);
		startAtlantis();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.v(TAG, "onBind() was called");
		return null;
	}

	public void startAtlantis() {
		// Schedule the alarm!
		AlarmManager am = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		// repeat
		am.cancel(mAlarmSender);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0,
				REPEATING_TIME, mAlarmSender);
	}

}
