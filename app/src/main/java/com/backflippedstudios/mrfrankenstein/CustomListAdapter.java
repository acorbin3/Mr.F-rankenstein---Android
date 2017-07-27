package com.backflippedstudios.mrfrankenstein;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by C0rbin on 7/24/2017.
 */

public class CustomListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<String> searchArrayList;

    public CustomListAdapter(Context context, ArrayList<String> initalList) {
        searchArrayList = initalList;
        mInflater = LayoutInflater.from(context);
    }

    public void updateList(ArrayList<String> updatedList){
        searchArrayList = updatedList;
        //Triggers the list update
        notifyDataSetChanged();
    }

    public void addItem(String newItem){
        searchArrayList.add(newItem);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return searchArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //Init a new view that hasnt existed before
        if (view == null) {
            view = mInflater.inflate(R.layout.custom_layout, viewGroup, false);
        }

        //Find the Text view layout. Then set the text, color, and size.
        TextView tv = (TextView) view.findViewById(R.id.custom_layout_tv1);

        tv.setText(searchArrayList.get(i));
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(20);

        return view;
    }
}
