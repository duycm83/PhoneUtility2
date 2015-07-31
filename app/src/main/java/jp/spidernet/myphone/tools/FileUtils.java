package jp.spidernet.myphone.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Parcelable;
import android.util.Log;

public class FileUtils {
    private static final String TAG = "FileUtils";

	/**
     * By default File#delete fails for non-empty directories, it works like "rm". 
     * We need something a little more brutual - this does the equivalent of "rm -r"
     * @param path Root File Path
     * @return true iff the file and all sub files/directories have been removed
     * @throws FileNotFoundException
     */
    public static boolean deleteRecursive(File path) throws FileNotFoundException{
        if (!path.exists()) throw new FileNotFoundException(path.getAbsolutePath());
        boolean ret = true;
        if (path.isDirectory()){
            for (File f : path.listFiles()){
                ret = ret && FileUtils.deleteRecursive(f);
            }
        }
        return ret && path.delete();
    }
    
    /**
     * 繧ｳ繝斐�蜈��繝代せ[srcPath]縺九ｉ縲√さ繝斐�蜈医�繝代せ[destPath]縺ｸ
     * 繝輔ぃ繧､繝ｫ縺ｮ繧ｳ繝斐�繧定｡後＞縺ｾ縺吶��
     * 繧ｳ繝斐�蜃ｦ逅�↓縺ｯFileChannel#transferTo繝｡繧ｽ繝�ラ繧貞茜逕ｨ縺励∪縺吶��
     * 蟆壹�√さ繝斐�蜃ｦ逅�ｵゆｺ�ｾ後�∝�蜉帙�蜃ｺ蜉帙�繝√Ε繝阪Ν繧偵け繝ｭ繝ｼ繧ｺ縺励∪縺吶��
     * @param srcPath    繧ｳ繝斐�蜈��繝代せ
     * @param destPath    繧ｳ繝斐�蜈医�繝代せ
     * @throws IOException    菴輔ｉ縺九�蜈･蜃ｺ蜉帛�逅�ｾ句､悶′逋ｺ逕溘＠縺溷�ｴ蜷�
     */
    public static void copyTransfer(String srcPath, String destPath) 
        throws IOException {
    	FileInputStream inputStream = new FileInputStream(srcPath);
        FileChannel srcChannel = inputStream.getChannel();
        FileOutputStream outputStream = new FileOutputStream(destPath);
        FileChannel destChannel = outputStream.getChannel();
        try {
            srcChannel.transferTo(0, srcChannel.size(), destChannel);
        } finally {
            srcChannel.close();
            destChannel.close();
            inputStream.close();
            outputStream.close();
        }

    }

	public static void copyDirectory(File fromFile, File toDir) {
		try {
//			copyTransfer(fromFile.getAbsolutePath(), toDir.getAbsolutePath());
			copyDirectoryOneLocationToAnotherLocation(fromFile, toDir);
		} catch (IOException e) {
			Log.e(TAG, "copyDirectory FAILED", e);
		}
	}
	
	/**
	 * 
	 * @param file  File (or directory) to be moved
	 * @param directoryname Move file to new directory
	 * @return
	 */
	public static boolean move(File fromFile, File toDir) {
		boolean result = fromFile.renameTo(new File(toDir.getPath(), fromFile.getName()));
		return result;
	}
	
	public static void copyDirectoryOneLocationToAnotherLocation(File sourceLocation, File targetLocation)
	        throws IOException {

	    if (sourceLocation.isDirectory()) {
	        if (!targetLocation.exists()) {
	            targetLocation.mkdir();
	        }

	        String[] children = sourceLocation.list();
	        for (int i = 0; i < sourceLocation.listFiles().length; i++) {

	            copyDirectoryOneLocationToAnotherLocation(new File(sourceLocation, children[i]),
	                    new File(targetLocation, children[i]));
	        }
	    } else {

	        InputStream in = new FileInputStream(sourceLocation);
	        if (targetLocation.isDirectory()) {
	        	targetLocation = new File(targetLocation.getAbsolutePath(), sourceLocation.getName());
	        }
	        OutputStream out = new FileOutputStream(targetLocation);

	        // Copy the bits from instream to outstream
	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = in.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }
	        in.close();
	        out.close();
	    }

	}

}
