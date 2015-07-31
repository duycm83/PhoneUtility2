package jp.spidernet.myphone;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.SwipeListView;

import java.io.File;
import java.util.ArrayList;

public class SearchResultActivity extends MainActivity implements
		SearchView.OnQueryTextListener {
	private static final String TAG = "SearchResultActivity";
	private SearchView mSearchView;
	private TextView mStatusView;
	private MyFilenameFilter.TYPE mSearchType = MyFilenameFilter.TYPE.NAME;
	public static boolean isFirstLoad = true;
	private int DIALOG_SEARCH_TYPE = 1;
	private int DIALOG_SEARCH_PROGRESS = 2;
	private int DIALOG_SEARCH_SIZE = 3;
	private int DIALOG_SEARCH_EXT = 4;
	private String mSearchWord = "";
	private String mSearchTypeName = "";

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		mStatusView = (TextView) findViewById(R.id.status_text);
		mListView = (SwipeListView) findViewById(R.id.listView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isFirstLoad) {
			updateNewDir(null);
			isFirstLoad = false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_result, menu);
		MenuItem searchItem = menu.findItem(R.id.search);
		mSearchView = (SearchView) searchItem.getActionView();
		mSearchView.setQueryHint(getText(R.string.inputfilename));
		setupSearchView(searchItem);
		// mSearchView.onActionViewExpanded();
		return true;
	}

	private void updateSearchResult() {
		Utility.sortFilesList(mListFiles);

		mFilesListAdapter = new FileListAdapter(SearchResultActivity.this,
				R.layout.listitem, mListFiles, true);

		mListView.setAdapter(mFilesListAdapter);
	}

	private void setupSearchView(MenuItem searchItem) {
		if (isAlwaysExpanded()) {
			mSearchView.setIconifiedByDefault(false);
		} else {
			searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
					| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		}

		mSearchView.setOnQueryTextListener(this);
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		String searchText = getString(R.string.action_search, newText);
		mStatusView.setText(searchText);
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		mSearchWord = query;
		SearchAsyncTask asyncTask = new SearchAsyncTask();
		asyncTask.execute(mSearchWord);
		return true;
	}

	public boolean onClose() {
		mStatusView.setText("Closed!");
		return false;
	}

	public boolean isAlwaysExpanded() {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (R.id.search_options == id) {
			showDialog(DIALOG_SEARCH_TYPE);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		
		super.onPrepareDialog(id, dialog);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		if (id == DIALOG_SEARCH_TYPE) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.search_title).setSingleChoiceItems(
					R.array.search_type, 0,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// The 'which' argument contains the index position
							// of the selected item
							switch (which) {
							case 0:
								mSearchType = MyFilenameFilter.TYPE.NAME;
								break;
							case 1:
								showDialog(DIALOG_SEARCH_SIZE);
								break;
							case 2:
								showDialog(DIALOG_SEARCH_EXT);
								break;
							default:
								break;
							}
							dialog.dismiss();
						}
					});
			dialog = builder.create();
		} else if (id == DIALOG_SEARCH_SIZE) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.search_size_title).setSingleChoiceItems(
					R.array.search_size, 0,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								mSearchType =  MyFilenameFilter.TYPE.SIZE_0_100KB;
								mSearchTypeName = "0~100KB";
								break;
							case 1:
								mSearchType =  MyFilenameFilter.TYPE.SIZE_100_1MB;
								break;
							case 2:
								mSearchType =  MyFilenameFilter.TYPE.SIZE_1_10MB;
								break;
							case 3:
								mSearchType =  MyFilenameFilter.TYPE.SIZE_10_100MB;
								break;
							case 4:
								mSearchType =  MyFilenameFilter.TYPE.SIZE_100MB;
								break;
							default:
								break;
							}
							dialog.dismiss();
							SearchAsyncTask asyncTask = new SearchAsyncTask();
							asyncTask.execute(mSearchWord);
						}
					});
			dialog = builder.create();
		} else if (id == DIALOG_SEARCH_EXT) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.search_extend_title).setSingleChoiceItems(
					R.array.search_extend, 0,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								mSearchType =  MyFilenameFilter.TYPE.EXT_TEXT;
								break;
							case 1:
								mSearchType =  MyFilenameFilter.TYPE.EXT_IMAGE;
								break;
							case 2:
								mSearchType =  MyFilenameFilter.TYPE.EXT_AUDIO;
								break;
							case 3:
								mSearchType =  MyFilenameFilter.TYPE.EXT_VIDEO;
								break;
							case 4:
								mSearchType =  MyFilenameFilter.TYPE.EXT_EXCEL;
								break;
							case 5:
								mSearchType =  MyFilenameFilter.TYPE.EXT_WORD;
								break;
							case 6:
								mSearchType =  MyFilenameFilter.TYPE.EXT_TORENT;
								break;
							case 7:
								mSearchType =  MyFilenameFilter.TYPE.EXT_TORENT;
								break;
							case 8:
								mSearchType =  MyFilenameFilter.TYPE.EXT_NONE;
								break;
							default:
								break;
							}
							dialog.dismiss();
							SearchAsyncTask asyncTask = new SearchAsyncTask();
							asyncTask.execute(mSearchWord);
						}
					});
			dialog = builder.create();
		} else if (id == DIALOG_SEARCH_PROGRESS) {
			dialog = ProgressDialog.show(this,
					getString(R.string.search_progress),
					getString(R.string.search_progress_name, mSearchWord));
		}
		return dialog;
	}

	@Override
	protected void onDestroy() {
		isFirstLoad = true;
		super.onDestroy();
	}

	class SearchAsyncTask extends AsyncTask<String, Integer, Integer> {
		private final File ROOT_DIR = new File("/mnt/");
		private AlertDialog dialog = null;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(
					SearchResultActivity.this,
					getString(R.string.search_progress),
					getString(R.string.search_progress_name,
							ROOT_DIR.toString()));
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			mListFiles.clear();
			searchAction(ROOT_DIR, mListFiles, mSearchType, params[0]);
			int resultSize = mListFiles.size();
			return resultSize;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (dialog != null) {
				dialog.dismiss();
			}
			if (result == -1) {
				Toast.makeText(SearchResultActivity.this,
						"A number not is inputed", Toast.LENGTH_SHORT).show();
			} else if (result >= 0) {
				String searchText = getString(R.string.action_search_result,
						result);
				mStatusView.setText(searchText);
				if (result > 0) {
					updateSearchResult();
				} else {
					Log.v(TAG, "not found");
				}
			}
			super.onPostExecute(result);
		}

		private void searchAction(final File target, ArrayList<File> result,
				MyFilenameFilter.TYPE type, String keyword) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					dialog.setMessage(target.getPath());
				}
			});
			File[] nextFolders = target.listFiles(new MyFilenameFilter(
					MyFilenameFilter.TYPE.FOLDER, null));
			File[] matchedFiles = target.listFiles(new MyFilenameFilter(type,
					keyword));
			if (matchedFiles != null) {
				for (File file : matchedFiles) {
					result.add(file);
				}
			} else {
				Log.v(TAG, "file not matchs");
			}

			if (nextFolders != null) {
				for (File file : nextFolders) {
					searchAction(file, result, type, keyword);
				}
			} else {
				Log.v(TAG, "Next folders not found");
			}
		}

	}
}
