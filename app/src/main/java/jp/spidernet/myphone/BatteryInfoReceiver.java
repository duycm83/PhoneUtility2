package jp.spidernet.myphone;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class BatteryInfoReceiver extends BroadcastReceiver {
	private static final String TAG = "BatteryInfoReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(TAG, "onReceive was called");
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
			// 蜈�崕迥ｶ諷九�蜿門ｾ�
			String statusStr = "";
			int status = intent.getIntExtra("status", 0);
			if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
				statusStr = "蜈�崕荳ｭ";
			} else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
				statusStr = "蜈�崕蛻�妙";
			} else if (status == BatteryManager.BATTERY_STATUS_FULL) {
				statusStr = "蜈�崕貅�繧ｿ繝ｳ";
			} else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
				statusStr = "蜈�崕蛻�妙荳ｭ";
			} else if (status == BatteryManager.BATTERY_STATUS_UNKNOWN) {
				statusStr = "荳肴�";
			}

			// 繝励Λ繧ｰ遞ｮ蛻･縺ｮ蜿門ｾ�
			String pluggedStr = "";
			int plugged = intent.getIntExtra("plugged", 0);
			if (plugged == BatteryManager.BATTERY_PLUGGED_AC) {
				pluggedStr = "AC繧｢繝�繝励ち";
			} else if (plugged == BatteryManager.BATTERY_PLUGGED_USB) {
				pluggedStr = "USB";
			}

			// 繝舌ャ繝�Μ繝ｼ驥上�蜿門ｾ�
			int level = intent.getIntExtra("level", 0);
			int scale = intent.getIntExtra("scale", 0);
			String batLevel = level + "%";
			// 貂ｩ蠎ｦ縺ｮ蜿門ｾ�
			int temperature = intent.getIntExtra("temperature", 0);

			String str = "";
			str += "蜈�崕迥ｶ諷�:" + statusStr + "\n";
			str += "繝励Λ繧ｰ遞ｮ蛻･:" + pluggedStr + "\n";
			str += "繝舌ャ繝�Μ繝ｼ驥�:" + level + "/" + scale + "\n";
			str += "貂ｩ蠎ｦ:" + (temperature / 10) + "蠎ｦ\n";
			notifi(String.valueOf(batLevel), str, context);
		}
	}

	public void notifi(String level, String content, Context context) {
		NotificationManager mNotificationManager;
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(level)
				.setContentText("Battery level:" + level);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(1, mBuilder.build());
	}
}
