package jp.spidernet.myphone.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.CheckBox;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import jp.spidernet.myphone.FileComparator;
import jp.spidernet.myphone.MainActivity;
import jp.spidernet.myphone.R;
import jp.spidernet.myphone.Utility;
import jp.spidernet.myphone.tools.FileUtils;

public class FileListAdapter extends ArrayAdapter<File> {
	public static enum FILE_TYPE {
		IMAGE, VIDEO, APK
	};
	private boolean mIsSearchList = false;
	private ArrayList<File> mFilesList = null;
	private MainActivity mActivity;
	private HashMap<String, Boolean> mCheckedMap = null;
	private ArrayList<CompoundButton> mCheckedView = null;
    private static final String TAG = FileListAdapter.class.getSimpleName();

	public FileListAdapter( MainActivity activity, int layoutId,
			ArrayList<File> files) {
		super(activity, layoutId, files);
		mFilesList = files;
		mActivity = activity;
		mCheckedMap = new HashMap<>();
		mCheckedView = new ArrayList<>();
		if (activity.isCut() == false) {
			activity.setCheckedFilesList(new ArrayList<File>());
		}
	}
	
	public FileListAdapter(MainActivity activity, int layoutId,
			ArrayList<File> files, boolean isSearchList) {
		this(activity, layoutId, files);
		this.mIsSearchList = isSearchList;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		View listItem = null;
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) mActivity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			listItem = layoutInflater.inflate(R.layout.listitem, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) listItem.findViewById(R.id.iv_icon);
			holder.btnDelete = listItem.findViewById(R.id.btn_delete);
			holder.btnRename = listItem.findViewById(R.id.btn_rename);
			holder.btnInfo = listItem.findViewById(R.id.btn_info);
			listItem.setTag(holder);
		} else {
			listItem = convertView;
			holder = (ViewHolder) listItem.getTag();
		}
		final File file = mFilesList.get(position);
		final String title = file.getName();
		long fileSize = file.length();
		long lastModified = file.lastModified();
		TextView tvTitle = (TextView) listItem.findViewById(R.id.tvTitle);
		TextView tvFileInfo = (TextView) listItem.findViewById(R.id.tvFileInfo);
		ImageView ivIcon = (ImageView) listItem.findViewById(R.id.iv_icon);
		TextView tvNum = (TextView) listItem.findViewById(R.id.tv_num);
		CheckBox checkBox = (CheckBox) listItem.findViewById(R.id.checkBox);

		tvTitle.setText(title);
		String fileName = file.getName();
		String info = "";
		if (file.isDirectory()) {
			File[] childrenLists = file.listFiles();
			int childFiles = 0;
			int childFolders = 0;
			if (childrenLists != null) {
				tvNum.setVisibility(View.VISIBLE);
				int length = childrenLists.length;
				if (length > 0) {
					if (length > 999) {
						tvNum.setText(R.string.larger999);
					} else {
						tvNum.setText(String.valueOf(length));
					}
				}
				ivIcon.setImageResource(R.drawable.ic_folder_open_black_48dp);
				for (int i = 0; i < length; i++) {
					File file2 = childrenLists[i];
					if (file2.isDirectory())
						childFolders++;
					else if (file2.isFile())
						childFiles++;
				}
			} else {
				//do nothing
			}
			info = mActivity.getString(R.string.numfiles_numfolders,
					childFiles, childFolders);
			tvFileInfo.setText(info);
		} else {
			tvNum.setVisibility(View.GONE);
			if (mIsSearchList) {
				TextView tvFilePath = (TextView) listItem.findViewById(R.id.tvFilePath);
				tvFilePath.setText(file.getPath());
				tvFilePath.setVisibility(View.VISIBLE);
				checkBox.setVisibility(View.GONE);
			}
			holder.imageView.setTag(file.getAbsolutePath());
			
			info = mActivity.getString(R.string.file_details, Utility
					.reportTraffic(fileSize),
					SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT)
							.format(new Date(lastModified)));
			tvFileInfo.setText(info);
			int id = Utility.getFileExtensionId(fileName);
			ivIcon.setImageResource(id);
			holder.iconType = id;
			if (id == R.drawable.ic_jpeg || id == R.drawable.ic_png
					|| id == R.drawable.ic_gif || id == R.drawable.ic_bmp) {
				holder.fileType = FILE_TYPE.IMAGE;
				LoadThumbTask task = new LoadThumbTask(holder.imageView, FILE_TYPE.IMAGE);
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, file);
			} else if (id == R.drawable.ic_3gp || id == R.drawable.ic_mp4
					|| id == R.drawable.ic_mpeg || id == R.drawable.ic_avi) {
				holder.fileType = FILE_TYPE.VIDEO;
				LoadThumbTask task = new LoadThumbTask(holder.imageView, FILE_TYPE.VIDEO);
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, file);
			} else if (id == R.drawable.ic_apk) {
				holder.fileType = FILE_TYPE.APK;
				LoadThumbTask task = new LoadThumbTask(holder.imageView, FILE_TYPE.APK);
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, file);
			} else {
				ivIcon.setImageResource(id);
			}
		}
		/*******/
		checkBox.setTag(checkBox.getId(), file);
		checkBox.setOncheckListener(onCheckedChangeListener);
		Boolean isChecked = (Boolean) mCheckedMap.get(fileName);
		if (isChecked != null) {
			checkBox.setChecked(isChecked);

		} else {
			checkBox.setChecked(false);

		}
		// Set click action for back menu button
		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int id = v.getId();
				if (id == R.id.btn_delete) {
					Log.d(TAG, "button delete clicked");
					if (file.isFile()) {
						showDialogConfirmDeleteOne(title, position, file);
					} else if (file.isDirectory()) {
						File[] files = file.listFiles();
						if (files != null && files.length > 0) {
							showDialogConfirmDeleteMulti(title, position, file);
						} else {
							showDialogConfirmDeleteOne(title, position, file);
						}
					}
				} else if (id == R.id.btn_rename) {
					Log.d(TAG, "button rename clicked");
					showDialogRename(title, position);
				} else if (id == R.id.btn_info) {
					Log.d(TAG, "button info clicked");
				}
			}
		};
		holder.btnDelete.setOnClickListener(onClickListener);
		holder.btnRename.setOnClickListener(onClickListener);
		holder.btnInfo.setOnClickListener(onClickListener);
		return listItem;
	}

	private void showDialogRename(String currentName, final int position) {
		final EditText editText = new EditText(mActivity);
		editText.setText(currentName);
		new MaterialDialog.Builder(mActivity)
				.title(R.string.rename_message)
				.customView(editText, true)
				.positiveText(R.string.rename)
				.negativeText(R.string.cancel)
				.positiveColorRes(R.color.colorPrimary)
				.negativeColorRes(R.color.colorPrimary)
				.callback(new MaterialDialog.ButtonCallback() {
					@Override
					public void onPositive(MaterialDialog dialog) {
						String newFileName = editText.getText().toString();
						boolean result = Utility.rename(
								mFilesList.get(position),
								newFileName);
						if (result) {
							mFilesList.remove(position);
							mFilesList.add(position, new File(
									newFileName));
							FileListAdapter.this
									.notifyDataSetChanged();
						} else {
							Toast.makeText(
									mActivity,
									mActivity.getString(R.string.cant_rename),
									Toast.LENGTH_SHORT).show();
						}
					}
				})
		.show();
	}

	private void showDialogConfirmDeleteOne(String fileName, final int position, final File file) {
		new MaterialDialog.Builder(mActivity)
				.title((file.isFile() ? R.string.delete_confirm_one_file: R.string.delete_confirm_empty_directory))
				.content(fileName)
				.positiveText(R.string.delete)
				.negativeText(R.string.cancel)
				.positiveColorRes(R.color.colorPrimary)
				.negativeColorRes(R.color.colorPrimary)
				.callback(new MaterialDialog.ButtonCallback() {
					@Override
					public void onPositive(MaterialDialog dialog) {
						boolean result = file.delete();
						if (result) {
							mFilesList.remove(position);
							FileListAdapter.this
									.notifyDataSetChanged();
						} else {
							Toast.makeText(
									mActivity,
									mActivity.getString(R.string.cant_delete),
									Toast.LENGTH_SHORT).show();
						}
					}
				})
				.show();
	}

	private void showDialogConfirmDeleteMulti(String fileName, final int position, final File file) {
		new MaterialDialog.Builder(mActivity)
				.title(R.string.delete_confirm_multi)
				.content(fileName)
				.positiveText(R.string.delete)
				.negativeText(R.string.cancel)
				.positiveColorRes(R.color.colorPrimary)
				.negativeColorRes(R.color.colorPrimary)
				.callback(new MaterialDialog.ButtonCallback() {
					@Override
					public void onPositive(MaterialDialog dialog) {
						boolean result = false;
						try {
							result = FileUtils.deleteRecursive(file);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						if (result) {
							mFilesList.remove(position);
							FileListAdapter.this
									.notifyDataSetChanged();
						} else {
							Toast.makeText(
									mActivity,
									mActivity.getString(R.string.cant_delete),
									Toast.LENGTH_SHORT).show();
						}
					}
				})
				.show();
	}

	private void showFileInformation(String fileName, final File file) {
		new MaterialDialog.Builder(mActivity)
				.title(R.string.delete_confirm_multi)
				.content(fileName)
				.positiveText(R.string.delete)
				.negativeText(R.string.cancel)
				.positiveColorRes(R.color.colorPrimary)
				.negativeColorRes(R.color.colorPrimary)
				.callback(new MaterialDialog.ButtonCallback() {
					@Override
					public void onPositive(MaterialDialog dialog) {
					}
				})
				.show();
	}
	CheckBox.OnCheckListener onCheckedChangeListener = new CheckBox.OnCheckListener() {

		@Override
		public void onCheck(CheckBox checkBox, boolean b) {
			//TODO write event code here
		}

//		public void onCheckedChanged(CompoundButton buttonView,
//				boolean isChecked) {
//			File file = (File) buttonView.getTag(buttonView.getId());
//			mCheckedMap.put(file.getName(), isChecked);
//
//			if (isChecked) {
//				mActivity.addToCheckedFilesList(file);
//				mCheckedView.add(buttonView);
//			} else {
//				mActivity.removeFromCheckedFilesList(file);
//				mCheckedView.remove(buttonView);
//			}
//		}
	};

	public void clearCheckedView() {
		int size = mCheckedView.size();
		for (int i = 0; i < size; i++) {
			mCheckedView.get(0).setChecked(false);
		}
	}

	@SuppressWarnings("unchecked")
	public void sortFile(int option) {
		
		Collections.sort(mFilesList, new FileComparator(R.id.sortSize, true));
		
	}
	
	Comparator<File> sizeComparator = null;
	
	
	class ViewHolder {
		ImageView imageView;
		View btnDelete;
		View btnRename;
		View btnInfo;
		int iconType;
		FILE_TYPE fileType;
		int position;
	}

	class LoadThumbTask extends AsyncTask<File, Void, Bitmap> {
//		private int mPosition;
//	    private ViewHolder mHolder;
//
//	    public LoadThumbTask(int position, ViewHolder holder) {
//	        mPosition = position;
//	        mHolder = holder;
//	    }
		private ImageView imageView;
		private String tag;
		private FILE_TYPE fileType = FILE_TYPE.IMAGE;

		public LoadThumbTask(ImageView imageView, FILE_TYPE fileType) {
			this.imageView = imageView;
			// ImageView に設定したタグをメンバへ
			this.tag = imageView.getTag().toString();
			this.fileType = fileType;
		}

		@Override
		protected Bitmap doInBackground(File... files) {
			Bitmap thumb = null;
			switch (fileType) {
			case IMAGE:
				thumb = Utility.getPreview(files[0]);
				break;
			case VIDEO:
				thumb = ThumbnailUtils.createVideoThumbnail(files[0].getAbsolutePath(),
						MediaStore.Images.Thumbnails.MINI_KIND);
				break;
			case APK:
				thumb = Utility.getIconInApkFile(files[0], mActivity);
				break;

			default:
				break;
			}
			
			return thumb;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// メンバのタグと imageView にセットしたタグが一致すれば
			// 画像をセットする
			if (imageView.getTag().equals(tag)) {
				if (result != null) {
					imageView.setImageBitmap(result);
				}
			}
		}
	}
}