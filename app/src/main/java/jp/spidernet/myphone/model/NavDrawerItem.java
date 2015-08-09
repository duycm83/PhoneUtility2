package jp.spidernet.myphone.model;

import jp.spidernet.myphone.R;

/**
 * Created by lamphucduy on 2015/08/04.
 */
public class NavDrawerItem {
    public int icon;
    public String name;

    public NavDrawerItem(int icon, String name)
    {
        this.icon = icon;
        this.name = name;
    }

    public static NavDrawerItem[] createDrawerItems() {
        NavDrawerItem[] arrays = new NavDrawerItem[5];
        arrays[0] = new NavDrawerItem(R.drawable.ic_sd_card_white_24dp, "Internal SD Card");
        arrays[1] = new NavDrawerItem(R.drawable.ic_sd_card_white_24dp, "External SD Card");
        arrays[2] = new NavDrawerItem(R.drawable.ic_collections_white_24dp, "Pictures");
        arrays[3] = new NavDrawerItem(R.drawable.ic_video_library_white_24dp, "Videos");
        arrays[4] = new NavDrawerItem(R.drawable.ic_file_download_white_24dp, "Download Folder");
        return arrays;
    }
}
