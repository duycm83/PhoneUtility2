package jp.spidernet.myphone;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class OpenFileDialog extends DialogFragment {
	public static final String TAG = OpenFileDialog.class.getSimpleName();
	private Intent intent = null;
	private Uri fileUri = null;

	public OpenFileDialog() {
	}

	public void setInfomation(Intent intent, Uri fromFile) {
		this.intent = intent;
		this.fileUri = fromFile;

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.select_file_type)
				.setItems(R.array.select_applications,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								String mimeType = null;
								switch (which) {
								case 0:
									mimeType = Utility.MIMETYPE.VIDEO_MPEG;
									Log.v(TAG, "video");
									break;
								case 1:
									mimeType = Utility.MIMETYPE.AUDIO_MPEG;
									Log.v(TAG, "audio");
									break;
								case 2:
									mimeType = Utility.MIMETYPE.TEXT_PLAIN;
									Log.v(TAG, "image");
									break;
								case 3:
									mimeType = Utility.MIMETYPE.TEXT_PLAIN;
									Log.v(TAG, "text");
									break;
								}
								if (mimeType != null)
								try {
									intent.setDataAndType(fileUri, mimeType);
									startActivity(intent);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
							}
						});
		// Create the AlertDialog object and return it
		return builder.create();
	}
}
