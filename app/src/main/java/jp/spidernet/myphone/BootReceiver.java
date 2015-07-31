package jp.spidernet.myphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	private static final String TAG = "BootReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(TAG, "onReceive was called");
//		Intent myStarterIntent = new Intent(context, StartUpService.class);
//		context.startService(myStarterIntent);
	}
}
