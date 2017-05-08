package com.gil_becker.nutritionreport;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Gil-B on 17/03/2017.
 */

public class AdapterForTheListOnReportActivity extends BaseAdapter {
    private Context mContext;
    private ArrayList mData;
    private Map<String,Float> mMap;

    public AdapterForTheListOnReportActivity(Context context,
                                             HashMap<String,Float> map) {
        this.mContext = context;
        this.mMap = map;
        mData = new ArrayList();
        Iterator i = map.entrySet().iterator();
        while (i.hasNext()){
            mData.add((Map.Entry)i.next());
        }
        System.out.println("gil:mData= "+mData);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public HashMap.Entry<String, Float> getItem(int position) {

        return (HashMap.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        Map<String , Float > treeMap = new TreeMap();
        ImageView iconView = new ImageView(mContext);

        if (convertView == null) {
            LayoutInflater inflater = ((AppCompatActivity)mContext).getLayoutInflater();
            v = inflater.inflate(R.layout.row,null,true);

        }
        else {
            v = convertView;
        }
        //set the text for each row
        //foodName = nutrition name
        //consumedAmount = total amount of the nutrition consumed per day

        HashMap.Entry <String, Float> item = getItem(position);
        TextView nutritionTitle = (TextView)v.findViewById(R.id.foodName);
        TextView amountTitle = (TextView)v.findViewById(R.id.consumed_amount);
        nutritionTitle.setText(item.getKey().toString()+" = ");
        Float f = (Float)0.0f;
        f=item.getValue();
        amountTitle.setText(f.toString());
//        TextView consumedAmount = (TextView)v.findViewById(R.id.consumed_amount);

        return v;
    }
}

