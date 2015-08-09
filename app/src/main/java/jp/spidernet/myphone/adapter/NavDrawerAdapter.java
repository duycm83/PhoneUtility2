package jp.spidernet.myphone.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import jp.spidernet.myphone.R;
import jp.spidernet.myphone.model.NavDrawerItem;

/**
 * Created by lamphucduy on 2015/08/04.
 */
public class NavDrawerAdapter extends ArrayAdapter<NavDrawerItem>
{
    private final Context context;
    private final int layoutResourceId;
    private NavDrawerItem data[] = null;

    public NavDrawerAdapter(Context context, int layoutResourceId, NavDrawerItem [] data)
    {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        View v = inflater.inflate(layoutResourceId, parent, false);

        ImageView imageView = (ImageView) v.findViewById(R.id.navDrawerImageView);
        TextView textView = (TextView) v.findViewById(R.id.navDrawerTextView);

        NavDrawerItem choice = data[position];

        imageView.setImageResource(choice.icon);
        textView.setText(choice.name);

        return v;
    }
}