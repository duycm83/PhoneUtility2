package jp.spidernet.myphone;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

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
	public FileListAdapter(MainActivity activity, int layoutId,
			ArrayList<File> files) {
		super(activity, layoutId, files);
		mFilesList = files;
		mActivity = activity;
		mCheckedMap = new HashMap<String, Boolean>();
		mCheckedView = new ArrayList<CompoundButton>();
		if (activity.isCut() == false) {
			activity.setCheckedFilesList(new ArrayList<File>());
		}
	}
	
	public FileListAdapter(MainActivity activity, int layoutId,
			ArrayList<File> files, boolean isSearchList) {
		this(activity, layoutId, files);
		this.mIsSearchList = isSearchList;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		View listItem = null;
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) mActivity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			listItem = layoutInflater.inflate(R.layout.listitem, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) listItem.findViewById(R.id.iv_icon);
			holder.button1 = (Button) listItem.findViewById(R.id.example_row_b_action_1);
			holder.button2 = (Button) listItem.findViewById(R.id.example_row_b_action_2);
			holder.button3 = (Button) listItem.findViewById(R.id.example_row_b_action_3);
			listItem.setTag(holder);
		} else {
			listItem = convertView;
			holder = (ViewHolder) listItem.getTag();
		}
		File file = mFilesList.get(position);
		String title = file.getName();
		long fileSize = file.length();
		long lastModified = file.lastModified();
		TextView tvTitle = (TextView) listItem.findViewById(R.id.tvTitle);
		TextView tvFileInfo = (TextView) listItem.findViewById(R.id.tvFileInfo);
		ImageView ivIcon = (ImageView) listItem.findViewById(R.id.iv_icon);
		CheckBox checkBox = (CheckBox) listItem.findViewById(R.id.checkBox);
		
		tvTitle.setText(title);
		String fileName = file.getName();
		String info = "";

		if (file.isDirectory()) {
			File[] childrenLists = file.listFiles();
			int childFiles = 0;
			int childFolders = 0;
			if (childrenLists != null) {
				int length = childrenLists.length;
				ivIcon.setImageResource(R.drawable.ic_folder_not_empty);
				for (int i = 0; i < length; i++) {
					File file2 = childrenLists[i];
					if (file2.isDirectory())
						childFolders++;
					else if (file2.isFile())
						childFiles++;
				}
			} else {
				ivIcon.setImageResource(R.drawable.ic_empty_folder);
			}
			info = mActivity.getString(R.string.numfiles_numfolders,
					childFiles, childFolders);
			tvFileInfo.setText(info);
		} else {
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
//				Bitmap bmp = Utility.getPreview(file);
//				if (bmp != null)
//					ivIcon.setImageBitmap(bmp);
//				else
//					ivIcon.setImageResource(id);
				holder.fileType = FILE_TYPE.IMAGE;
//				LoadThumbTask task = new LoadThumbTask(position, holder);
				LoadThumbTask task = new LoadThumbTask(holder.imageView, FILE_TYPE.IMAGE);
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, file);
			} else if (id == R.drawable.ic_3gp || id == R.drawable.ic_mp4
					|| id == R.drawable.ic_mpeg || id == R.drawable.ic_avi) {
				// Bitmap thumb = null;
				// thumb = ThumbnailUtils.createVideoThumbnail(
				// file.getAbsolutePath(),
				// MediaStore.Images.Thumbnails.MINI_KIND);
				// if (thumb != null)
				// ivIcon.setImageBitmap(thumb);
				// else
				// ivIcon.setImageResource(id);
				holder.fileType = FILE_TYPE.VIDEO;
//				LoadThumbTask task = new LoadThumbTask(position, holder);
				LoadThumbTask task = new LoadThumbTask(holder.imageView, FILE_TYPE.VIDEO);
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, file);
			} else if (id == R.drawable.ic_apk) {
//				Bitmap bmp = Utility.getIconInApkFile(file, mActivity);
//				if (bmp != null)
//					ivIcon.setImageBitmap(bmp);
//				else
//					ivIcon.setImageResource(id);
				holder.fileType = FILE_TYPE.APK;
//				LoadThumbTask task = new LoadThumbTask(position, holder);
				LoadThumbTask task = new LoadThumbTask(holder.imageView, FILE_TYPE.APK);
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, file);
			} else {
				ivIcon.setImageResource(id);
			}
		}
		/*******/
		checkBox.setTag(checkBox.getId(), file);
		checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
		Boolean isChecked = (Boolean) mCheckedMap.get(fileName);
		if (isChecked != null) {
			checkBox.setChecked(isChecked);

		} else {
			checkBox.setChecked(false);

		}
		// Set click action for back menu button
		holder.button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "button 1 is clicked");
			}
		});
		holder.button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "button 2 is clicked");
			}
		});
		holder.button3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "button 3 is clicked");
			}
		});
		return listItem;
	}

	OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			File file = (File) buttonView.getTag(buttonView.getId());
			mCheckedMap.put(file.getName(), isChecked);

			if (isChecked) {
				mActivity.addToCheckedFilesList(file);
				mCheckedView.add(buttonView);
			} else {
				mActivity.removeFromCheckedFilesList(file);
				mCheckedView.remove(buttonView);
			}
		}
	};

	public void clearCheckedView() {
		int size = mCheckedView.size();
		for (int i = 0; i < size; i++) {
			// onCheckedChanged縺ｧ繝ｪ繧ｹ繝医い繧､繝�Β縺悟炎髯､縺輔ｌ縺ｦ縺�ｋ縺溘ａ縲√��0縲阪°繧峨メ繧ｧ繝�け繧呈ｶ医☆縲�
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
		Button button1;
		Button button2;
		Button button3;
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