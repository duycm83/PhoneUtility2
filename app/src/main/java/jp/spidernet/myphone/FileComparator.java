package jp.spidernet.myphone;

import java.io.File;
import java.util.Comparator;

public class FileComparator implements Comparator<File> {
	private int mSortTpye = 0;
	private boolean mIsDescent = false;
	public FileComparator(int sortType, boolean isDescent) {
		this.mSortTpye = sortType;
		this.mIsDescent = isDescent;
	}
	@Override
	public int compare(File leftFile, File rightFile) {
		int result = -1;
		if (this.mIsDescent) {
			switch (mSortTpye) {
			case R.id.sortSize:
				long lsize = leftFile.length();
				long rsize = rightFile.length();
				if (lsize > rsize) {
					result = 1;
				}
				break;
			case R.id.sortUpdate:
				long leftLastmodified = leftFile.lastModified();
				long rightLastmodified = leftFile.lastModified();
				if (leftLastmodified > rightLastmodified) {
					result = 1;
				}
				break;
			case R.id.sortFileName:
				String leftname = leftFile.getName();
				String rightname = rightFile.getName();
				if (leftname.compareToIgnoreCase(rightname) > 0) {
					result = 1;
				}
				break;
			default:
				break;
			}
		} else {
			switch (mSortTpye) {
			case R.id.sortSize:
				long lsize = leftFile.length();
				long rsize = rightFile.length();
				if (lsize < rsize) {
					result = 1;
				}
				break;
			case R.id.sortUpdate:
				long leftLastmodified = leftFile.lastModified();
				long rightLastmodified = leftFile.lastModified();
				if (leftLastmodified < rightLastmodified) {
					result = 1;
				}
				break;
			case R.id.sortFileName:
				String leftname = leftFile.getName();
				String rightname = rightFile.getName();
				if (leftname.compareToIgnoreCase(rightname) < 0) {
					result = 1;
				}
				break;
			default:
				break;
			}
		}
		return result;
	}
}