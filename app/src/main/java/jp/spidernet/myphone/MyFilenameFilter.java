package jp.spidernet.myphone;

import java.io.File;
import java.io.FilenameFilter;

public class MyFilenameFilter implements FilenameFilter {
	public static enum TYPE {
		NAME, EXT_APK, EXT_IMAGE, EXT_AUDIO, EXT_VIDEO, EXT_WORD, EXT_EXCEL, EXT_TORENT, EXT_TEXT, EXT_NONE, SIZE_0_100KB, SIZE_100_1MB, SIZE_1_10MB, SIZE_10_100MB, SIZE_100MB, FOLDER
	};

	private String searchName = null;
	private TYPE searchType = TYPE.NAME;

	public MyFilenameFilter(String searchName) {
		this.searchName = searchName;
	}

	public MyFilenameFilter(TYPE type, String searchName) {
		this.searchName = searchName;
		this.searchType = type;
	}

	@Override
	public boolean accept(File dir, String filename) {
		File file = new File(dir, filename);
		if (file.isDirectory() && searchType == TYPE.FOLDER) {
			return true;
		} else if (!file.isFile() || searchName == null) {
			return false;
		}
		boolean matchedName = false;
		if (searchName == null || searchName.isEmpty() || filename.contains(searchName)) {
			matchedName = true;
		}
		if (matchedName) {
			if (searchType == TYPE.NAME) {
				return true;
			}
		} else {
			return false;
		}
		boolean result = false;
		switch (searchType) {
		case EXT_AUDIO:
			if (result == false) {
				result = filename.endsWith(".mp3") || filename.endsWith(".MP3");
			}
			if (result == false) {
				result = filename.endsWith(".wma") || filename.endsWith(".WMA");
			}
			if (result == false) {
				result = filename.endsWith(".mid") || filename.endsWith(".MID");
			}
			break;
		case EXT_VIDEO:
			if (result == false) {
				result = filename.endsWith(".mp4") || filename.endsWith(".MP4");
			}
			if (result == false) {
				result = filename.endsWith(".avi") || filename.endsWith(".AVI");
			}
			if (result == false) {
				result = filename.endsWith(".mkv") || filename.endsWith(".MKV");
			}
			if (result == false) {
				result = filename.endsWith(".3gp") || filename.endsWith(".3GP");
			}
			if (result == false) {
				result = filename.endsWith(".wmv") || filename.endsWith(".WMV");
			}
			break;
		case EXT_WORD:
			if (result == false) {
				result = filename.endsWith(".doc") || filename.endsWith(".DOC");
			}
			if (result == false) {
				result = filename.endsWith(".docx")
						|| filename.endsWith(".DOCX");
			}
			break;
		case EXT_EXCEL:
			if (result == false) {
				result = filename.endsWith(".xls") || filename.endsWith(".XLS");
			}
			if (result == false) {
				result = filename.endsWith(".xlsx")
						|| filename.endsWith(".XLSX");
			}
			break;
		case EXT_TORENT:
			if (result == false) {
				result = filename.endsWith(".torrent")
						|| filename.endsWith(".TORRENT");
			}
			break;
		case EXT_TEXT:
			if (result == false) {
				result = filename.endsWith(".txt") || filename.endsWith(".TXT");
			}
			break;
		case EXT_APK:
			if (result == false) {
				result = filename.endsWith(".apk") || filename.endsWith(".APK");
			}
			break;
		case EXT_IMAGE:
			if (result == false) {
				result = filename.endsWith(".jpg") || filename.endsWith(".JPG");
			}
			if (result == false) {
				result = filename.endsWith(".png") || filename.endsWith(".PNG");
			}
			if (result == false) {
				result = filename.endsWith(".gif") || filename.endsWith(".GIF");
			}
			break;
		case SIZE_0_100KB:
			result = file.length() <= 102400;// 100Kbytes
			break;
		case SIZE_100_1MB:
			result = file.length() > 102400 && file.length() <= 1048576; //1MB=1048576bytes
			break;
		case SIZE_1_10MB:
			result = file.length() > 1048576 && file.length() <= 10485760;//10MB = 10485760bytes
			break;
		case SIZE_10_100MB:
			result = file.length() > 10485760 && file.length() <= 104857600;
			break;
		case SIZE_100MB:
			result = file.length() > 104857600; //100MB = 104857600bytes
			                         
			break;
		case EXT_NONE:
			if (filename.contains(".") == false) {
				result = true;
			}
				
			break;
		default:
			break;
		}
		file = null;
		return result;
	}
}
