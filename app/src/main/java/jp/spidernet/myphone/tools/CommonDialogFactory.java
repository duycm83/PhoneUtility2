package jp.spidernet.myphone.tools;

import jp.spidernet.myphone.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public class CommonDialogFactory {

	/**
	 * 
	 * @param context
	 * @param message
	 * @param yesClick
	 * @param noClick
	 * @return
	 */
	public static AlertDialog make1(Context context, String message,
			final ISimpleListener yesClick1, final ISimpleListener yesClick2,
			final ISimpleListener noClick) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final EditText editText = new EditText(context);
		builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton(R.string.new_file,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (yesClick1 != null) {
									yesClick1.onClick(editText.getText().toString());
								}
							}
						})
				.setNeutralButton(R.string.new_dir,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (yesClick2 != null) {
									yesClick2.onClick(editText.getText().toString());
								}
							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (noClick != null) {
									noClick.onClick(editText.getText().toString());
								}
							}
						});

		builder.setView(editText);
		AlertDialog alert = builder.create();
		return alert;
	}
}
