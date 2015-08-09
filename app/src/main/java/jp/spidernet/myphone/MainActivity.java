package jp.spidernet.myphone;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import jp.spidernet.myphone.adapter.FileListAdapter;
import jp.spidernet.myphone.adapter.NavDrawerAdapter;
import jp.spidernet.myphone.dialog.OpenFileDialog;
import jp.spidernet.myphone.model.NavDrawerItem;
import jp.spidernet.myphone.tools.CommonDialogFactory;
import jp.spidernet.myphone.tools.FileUtils;
import jp.spidernet.myphone.tools.ISimpleListener;

public class MainActivity extends AppCompatActivity  {
	public static final int EDIT_MODE_CUT = 1;
	public static final int EDIT_MODE_COPY = 2;
	public static final String EXTRA_FILE_LIST = "files_list";
	public static final String EXTRA_EDIT_MODE = "edit_mode";
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final int DIALOG_ABOUT_ID = 1;
	private static final int DIALOG_DELETE_ID = 2;
	private static final int DIALOG_PLAY_SEARCH_TORRENT_ID = 3;
	protected SwipeListView mListView = null;
	protected File SDDIR = Environment.getExternalStorageDirectory();
	protected File mCurrentDir = SDDIR;
	protected ArrayList<File> mListFiles;
	protected Stack<Integer> mSelectedPosStack = new Stack<Integer>();
	protected FileListAdapter mFilesListAdapter = null;

	protected ArrayList<File> mCheckedFilesList = new ArrayList<File>();
	protected Menu mMenu = null;
	BroadcastReceiver deviceAtatchReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v(TAG, "ACTION=" + intent.getAction());
			Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT)
					.show();
			UsbManager manager = (UsbManager) getSystemService(USB_SERVICE);
			HashMap<String, UsbDevice> devicemap = manager.getDeviceList();
			for (Iterator<String> iterator = devicemap.keySet().iterator(); iterator
					.hasNext();) {
				String type = (String) iterator.next();
				UsbDevice device = devicemap.get(type);
				Log.v(TAG, device.getDeviceName());
				Log.v(TAG, "productId=" + device.getProductId());
				Log.v(TAG, "vendorId=" + device.getVendorId());
			}
			updateNewDir(mCurrentDir);
		}

	};
	boolean flag = false;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private android.widget.ListView mDrawerListView;
	private ArrayList<File> mCutFilesList = new ArrayList<File>();
	private ArrayList<File> mCopyFilesList = new ArrayList<File>();
	private AdView mAdView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.AppTheme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		actionBarSetup();

		setCloseFooter();
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		}
		Utility.getLocationInfo(getBaseContext());
		mListView = (SwipeListView) findViewById(R.id.listView);
		registerForContextMenu(mListView);

		IntentFilter intentfilter = new IntentFilter();
		intentfilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		intentfilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		registerReceiver(deviceAtatchReceiver, intentfilter);
		updateNewDir(mCurrentDir);
		mDrawerLayout = (DrawerLayout)findViewById(R.id.main_dl);
		initDrawer();


	}

	private void initDrawer() {
		// （3本線の）切り替えボタンの生成
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name);
		mDrawerToggle.setDrawerIndicatorEnabled(true);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		mDrawerListView = (ListView) findViewById(R.id.main_lv_drawer);
		NavDrawerAdapter adapter = new NavDrawerAdapter(this, R.layout.drawer_list_item, NavDrawerItem.createDrawerItems());
		mDrawerListView.setAdapter(adapter);
	}
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

//	@Override
//	public void onBackStackChanged() {
//		mDrawerToggle.setDrawerIndicatorEnabled(mFm.getBackStackEntryCount() == 0);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(mFm.getBackStackEntryCount() > 0);
//		mDrawerToggle.syncState();
//	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		Log.d(TAG, "intent: " + intent);
		String action = intent.getAction();

		if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
			Log.v(TAG, action);
			Toast.makeText(getBaseContext(), action, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Sets the Action Bar for new Android versions.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void actionBarSetup() {
		// ツールバーをアクションバーとしてセット
		Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
		setSupportActionBar(toolbar);
		toolbar.inflateMenu(R.menu.main_menu);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			android.support.v7.app.ActionBar ab = getSupportActionBar();
			ab.setDisplayShowHomeEnabled(true);
			ab.setSubtitle(mCurrentDir.getAbsolutePath());
		}
	}

	protected void setAdapter() {
		mListView.setAdapter(mFilesListAdapter);
		mListView.setSwipeListViewListener(new BaseSwipeListViewListener() {

			@Override
			public void onClickFrontView(int position) {
				Log.d(TAG, String.format("onClickFrontView %d", position));
				onItemClick(position);
			}

			@Override
			public void onClickBackView(int position) {
				Log.d(TAG, String.format("onClickBackView %d", position));
			}
		});
	}

	private void onItemClick(int pos) {
		changeToMenuMain();
		File selectFile = mListFiles.get(pos);
		if (!selectFile.canRead()) {
            Toast.makeText(getBaseContext(),
					getString(R.string.cant_open), Toast.LENGTH_SHORT)
					.show();
            return;
        }
		if (selectFile.isFile()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String fileName = selectFile.getName();
            String extension = Utility.getFileExtension(fileName);
            String mimeType = null;
            if (Utility.APK.equalsIgnoreCase(extension)) {
                mimeType = Utility.MIMETYPE.APK;
            } else if (Utility.TORRENT.equalsIgnoreCase(extension)) {
                mimeType = MimeTypeMap.getSingleton()
                        .getMimeTypeFromExtension(extension);
                if (mimeType == null)
                    mimeType = Utility.MIMETYPE.TORRENT;
            } else if(Utility.TXT.equalsIgnoreCase(extension)){
                mimeType = MimeTypeMap.getSingleton()
                        .getMimeTypeFromExtension(extension);
                if (mimeType == null)
                    mimeType = Utility.MIMETYPE.TEXT_PLAIN;
            } else {
                OpenFileDialog dialog = new OpenFileDialog();
                dialog.setInfomation(intent, Uri.fromFile(selectFile));
                dialog.show(getSupportFragmentManager(), OpenFileDialog.TAG);
                return;
            }
            intent.setDataAndType(Uri.fromFile(selectFile), mimeType);
            try {
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                if (Utility.MIMETYPE.TORRENT.equalsIgnoreCase(mimeType))
                    showDialog(DIALOG_PLAY_SEARCH_TORRENT_ID);
            }
        } else {
            mSelectedPosStack.push(pos);
            updateNewDir(selectFile);
        }
	}

	@Override
	public void onBackPressed() {
		String name = null;
		if (mCurrentDir != null) {
			name = mCurrentDir.getName();
		}
		if (SDDIR.getName().equals(name)) {
			super.onBackPressed();
		} else {
			File newDir = upToParentDir(null);
			if (newDir == null) {
				super.onBackPressed();
			}
		}

		changeToMenuMain();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DIALOG_PLAY_SEARCH_TORRENT_ID:
			// 1. Instantiate an AlertDialog.Builder with its constructor
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// 2. Chain together various setter methods to set the dialog
			// characteristics
			builder.setMessage(R.string.dialog_download_torrent_app_message)
					// Add the buttons
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									Intent goToMarket = new Intent(
											Intent.ACTION_VIEW).setData(Uri
											.parse("market://search?q=torrent"));
									startActivity(goToMarket);
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			// 3. Get the AlertDialog from create()
			dialog = builder.create();
			break;
		case DIALOG_ABOUT_ID:
			dialog = new Dialog(this);

			dialog.setContentView(R.layout.about);
			dialog.setTitle(R.string.app_name);
			dialog.findViewById(R.id.close).setOnClickListener(
					new OnClickListener() {
						public void onClick(View arg0) {
							dismissDialog(DIALOG_ABOUT_ID);
						}
					});
			break;
		case DIALOG_DELETE_ID:
			// 1. Instantiate an AlertDialog.Builder with its constructor
			builder = new AlertDialog.Builder(this);
			// 2. Chain together various setter methods to set the dialog
			// characteristics
			builder.setMessage(R.string.delete_confirm).setTitle(
					R.string.delete_file_confirm_title);
			// Add the buttons
			builder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							deleteSelectedFiles();
						}
					});
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							changeToMenuMain();
						}
					});

			// 3. Get the AlertDialog from create()
			dialog = builder.create();

			break;
		default:
			dialog = super.onCreateDialog(id);
			break;
		}
		return dialog;
	}

	protected void deleteSelectedFiles() {
		if (mCheckedFilesList != null) {
			DeleteFileAsyncTask deleteFileAsyncTask = new DeleteFileAsyncTask();
			deleteFileAsyncTask.execute();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main_menu, menu);

		this.mMenu = menu;
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.itemDelete:
			showDialog(DIALOG_DELETE_ID);
			break;
		case R.id.itemCut:
			startEditMode(EDIT_MODE_CUT, mCheckedFilesList);
			break;
		case R.id.itemCopy:
			startEditMode(EDIT_MODE_COPY, mCheckedFilesList);
			break;
//		case R.id.itemAbout:
//			// Intent intent = new Intent(this, AboutActivity.class);
//			// startActivity(intent);
//			showDialog(DIALOG_ABOUT_ID);
//			break;
		case R.id.itemNewFile:
			CommonDialogFactory.make1(this, getString(R.string.new_file),
					new ISimpleListener() {
						public void onClick(String... params) {
							File file = new File(mCurrentDir, params[0]
									+ ".txt");
							if (!file.exists()) {
								try {
									boolean result = file.createNewFile();
									if (result) {
										mListFiles.add(file);
										mFilesListAdapter
												.notifyDataSetChanged();
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}, new ISimpleListener() {
						public void onClick(String... params) {
							File file = new File(mCurrentDir, params[0]);
							if (!file.exists()) {
								boolean result = file.mkdir();
								if (result) {
									mListFiles.add(file);
									mFilesListAdapter.notifyDataSetChanged();
								}
							}
						}
					}, null).show();
			break;
		case R.id.itemCancel:
			clearCheckedItem();
			onMenuEditMode(null);
			break;
		case R.id.itemSearch:
			clearCheckedItem();
			onMenuEditMode(null);
			Intent intent = new Intent(this, SearchResultActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

		return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
	}

	private void clearCheckedItem() {
		mCheckedFilesList.clear();
		if (mCutFilesList != null) {
			mCutFilesList.clear();
		}
		if (mCopyFilesList != null) {
			mCopyFilesList.clear();
		}
		if (mFilesListAdapter != null) {
			mFilesListAdapter.clearCheckedView();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Log.v(TAG, "onCreateContextMenu was called");
		int id = v.getId();
		if (id == R.id.listView) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(mListFiles.get(info.position).getName());
			// String[] menuItems = getResources().getStringArray(
			// R.array.context_menu);
			// for (int i = 0; i < menuItems.length; i++) {
			// menu.add(info.position, i, i, menuItems[i]);
			// }
			menu.add(info.position, 0, 0, R.string.rename);
			// if (mCutFilesList == null)
			// menu.add(info.position, 1, 1, R.string.cut);
			// else
			// menu.add(info.position, 1, 1, R.string.paste);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuItemIndex = item.getItemId();
		if (menuItemIndex == 0) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			final EditText editText = new EditText(MainActivity.this);
//			editText.setText(mListFiles.get(info.position).getName());
//			builder.setMessage(getString(R.string.rename_message))
//					.setView(editText)
//					.setCancelable(false)
//					.setPositiveButton(android.R.string.ok,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int id) {
//									String newName = mCurrentDir.getPath()
//											+ "/"
//											+ editText.getText().toString();
//									boolean result = Utility.rename(
//											mListFiles.get(info.position),
//											newName);
//									if (result) {
//										mListFiles.remove(info.position);
//										mListFiles.add(info.position, new File(
//												newName));
//										mFilesListAdapter
//												.notifyDataSetChanged();
//									} else {
//										Toast.makeText(
//												getBaseContext(),
//												getString(R.string.cant_rename),
//												Toast.LENGTH_SHORT).show();
//									}
//								}
//							})
//					.setNegativeButton(android.R.string.cancel,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int id) {
//									dialog.cancel();
//								}
//							});
//			AlertDialog alert = builder.create();
//			alert.show();
		} else if (menuItemIndex == 1) {
			if (mCutFilesList == null) { // cut
				mCutFilesList = new ArrayList<>();
				mCutFilesList.add(mListFiles.get(info.position));
			} else { // paste
				Utility.move(mCutFilesList, mListFiles.get(info.position));
				mCutFilesList = null;
			}
		}
		return true;
	}

	public File upToParentDir(MenuItem v) {
		File upDir = null;
		if (mCurrentDir != null) {
			upDir = mCurrentDir.getParentFile();
		}
		if (upDir != null) {
			updateNewDir(upDir);
			if (!mSelectedPosStack.isEmpty())
				mListView.setSelection(mSelectedPosStack.pop());
		}
		return upDir;
	}

	protected void updateNewDir(File newFileDir) {
		mCurrentDir = newFileDir;
		if (newFileDir == null) {
			if (SearchResultActivity.isFirstLoad)
				mListFiles.clear();
		} else {
			getSupportActionBar().setSubtitle(mCurrentDir.getAbsolutePath());
			mListFiles = Utility.makeFilesArrayList(mCurrentDir.listFiles());
		}
		Utility.sortFilesList(mListFiles);
		mFilesListAdapter = new FileListAdapter(MainActivity.this,
				R.layout.listitem, mListFiles);
		mListView.setAdapter(mFilesListAdapter);
		setAdapter();
	}

	public void addToCheckedFilesList(File file) {
		mCheckedFilesList.add(file);
	}

	public void removeFromCheckedFilesList(File file) {
		if (mCutFilesList != null) {
			mCutFilesList.remove(file);
		}
		if (mCheckedFilesList != null) {
			mCheckedFilesList.remove(file);
		}
	}

	public void setCheckedFilesList(ArrayList<File> checkedFilesList) {
		this.mCheckedFilesList = checkedFilesList;
	}

		public boolean isCut() {
		return (mCutFilesList != null && mCutFilesList.size() > 0);
	};

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(deviceAtatchReceiver);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED) {
			clearCheckedItem();
			changeToMenuMain();
			if (data != null) {
				String currentDir = data
						.getStringExtra(EditActivity.CURRENT_DIR);
				if (currentDir != null) {
					mCurrentDir = new File(currentDir);
					updateNewDir(mCurrentDir);
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onMenuEditMode(View view) {
		if (this.mMenu == null) {
			return;
		} else {
			if (mCheckedFilesList.size() > 0) {
				this.mMenu.clear();
				MenuInflater menuInflater = getMenuInflater();
				menuInflater.inflate(R.menu.edit_select_menu, this.mMenu);
			} else if (mCopyFilesList.size() > 0 || mCutFilesList.size() > 0) {
				// 菴輔ｂ縺励↑縺�
			} else {
				this.mMenu.clear();
				MenuInflater menuInflater = getMenuInflater();
				menuInflater.inflate(R.menu.main_menu, this.mMenu);
			}
		}
	}

	private void changeToMenuMain() {
		clearCheckedItem();
		if (this.mMenu != null) {
			this.mMenu.clear();
			MenuInflater menuInflater = getMenuInflater();
			menuInflater.inflate(R.menu.main_menu, this.mMenu);
		}
	}

	private void startEditMode(int editMode, ArrayList<File> checkedList) {
		int size = checkedList.size();
		String[] filesList = new String[size];
		for (int i = 0; i < size; i++) {
			File file = checkedList.get(i);
			filesList[i] = file.getAbsolutePath();
		}

		Intent intent = new Intent(this, EditActivity.class);
		intent.putExtra(EXTRA_FILE_LIST, filesList);
		intent.putExtra(EXTRA_EDIT_MODE, editMode);
		startActivityForResult(intent, editMode);
	}

	protected void setCloseFooter() {
		final View footer = findViewById(R.id.footer);
		findViewById(R.id.button_close).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						footer.setVisibility(View.GONE);
					}
				});

		mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
	}

	public void onSort(View view) {
		int id = view.getId();
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		mFilesListAdapter.sort(new FileComparator(id, flag));
		mFilesListAdapter.notifyDataSetChanged();
		if (flag) {
			flag = false;
		} else {
			flag = true;
		}
	}

	class DeleteFileAsyncTask extends AsyncTask<File, Integer, Boolean> {
		ProgressDialog dialog = null;

		public DeleteFileAsyncTask() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(MainActivity.this);
			dialog.setTitle(R.string.delete_file_confirm_title);
			dialog.setMessage(getString(R.string.deleting));
			dialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected Boolean doInBackground(File... arrayfiles) {
			if (mCheckedFilesList != null) {
				for (File file : mCheckedFilesList) {
					if (file.isDirectory()) {
						try {
							if (FileUtils.deleteRecursive(file)) {
								mListFiles.remove(file);
							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					} else {
						if (file.delete()) {
							mListFiles.remove(file);
						}
					}
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (dialog != null && dialog.isShowing()) {
				dialog.cancel();
			}
			mFilesListAdapter.notifyDataSetChanged();
			changeToMenuMain();
			super.onPostExecute(result);
		}
	}

}