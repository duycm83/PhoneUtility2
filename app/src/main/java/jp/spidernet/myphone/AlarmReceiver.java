package jp.spidernet.myphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

	@Override
    public void onReceive(Context context, Intent intent) {
    	Log.v(TAG, "onReceive was called");
    	Intent startActivityIntent = new Intent(context, AboutActivity.class);
    	startActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	context.startActivity(startActivityIntent);
    }
}