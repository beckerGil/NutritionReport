package com.gil_becker.nutritionreport;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Gil-B on 14/03/2017.
 */

public class AdapterForTheListOnMainActivity extends BaseAdapter {

    private Context mContext;

    private List<String> mList;

    public AdapterForTheListOnMainActivity(Context context,
                                           List<String> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        ImageView iconView = new ImageView(mContext);

        if (convertView == null) {
            LayoutInflater inflater = ((AppCompatActivity)mContext).getLayoutInflater();
            v = inflater.inflate(R.layout.row_adapter_main,null,true);

        }
        else {
            v = convertView;
        }

        TextView txtTitle = (TextView)v.findViewById(R.id.foodName);
        txtTitle.setText((mList.get(position).toString()));


        return v;
    }
}
