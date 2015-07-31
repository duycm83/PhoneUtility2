package jp.spidernet.myphone;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class AboutActivity extends Activity implements OnClickListener {
	private static final String TAG = "AboutActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
//		View atlantis = findViewById(R.id.adlantis);
//		View closeBtn = findViewById(R.id.close);
//		atlantis.setOnClickListener(this);
		findViewById(R.id.close).setOnClickListener(this);
//		closeBtn.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Calendar rightNow = Calendar.getInstance();
		// int hour = rightNow.get(Calendar.HOUR_OF_DAY);
		// if (hour < 20 && hour > 8) {
		// Vibrator vibrator =
		// (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		// vibrator.vibrate(1000);
		// }
	}

	public void onClick(View v) {
		finish();
	}

	@Override
	protected void onPause() {
		Log.v(TAG, "onPause() was called");
		super.onPause();
//		finish();
	}

	@Override
	protected void onDestroy() {
		Log.v(TAG, "onDestroy() was called");
		super.onDestroy();
	}
}
