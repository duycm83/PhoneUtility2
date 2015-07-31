package jp.spidernet.myphone;

import java.io.File;

public class SortableFileName implements Comparable<SortableFileName> {
    public File f;
    public int pos;
    
    public SortableFileName(File file) {
        f = file;
    }

    public int compareTo(SortableFileName that) {
        String thatFileName = that.f.getName().toLowerCase();
        String thisFileName = f.getName().toLowerCase();
        return thisFileName.compareTo(thatFileName);
    }
};

