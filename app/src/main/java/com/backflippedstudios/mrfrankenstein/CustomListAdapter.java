package com.backflippedstudios.mrfrankenstein;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.backflippedstudios.mrfrankenstein.Fragements.Tab3Fragment;
import com.backflippedstudios.mrfrankenstein.Fragements.Tab4Fragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;

import static com.backflippedstudios.mrfrankenstein.Fragements.Tab3Fragment.ListType.LT_USE_SHARED_PREFS;

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
        TextView tv = view.findViewById(R.id.custom_layout_tv1);

        tv.setText(searchArrayList.get(i));
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(20);

        ImageView ivRemove = view.findViewById(R.id.imageViewRemove);

        ivRemove.setOnClickListener(new CustomOnClickListener(i));

        return view;
    }

    public class CustomOnClickListener implements View.OnClickListener{

        Integer rowItem;

        public  CustomOnClickListener(Integer rowItem){
            this.rowItem = rowItem;
        }

        @Override
        public void onClick(View view) {

            //Sync with DB and shared prefs and trackedList
            String itemToRemove = searchArrayList.get(rowItem);
            searchArrayList.remove(searchArrayList.get(rowItem));
            switch(Tab3Fragment.listTypeToUse){
                case LT_USE_SHARED_PREFS:
                    SharedPreferences mSharedPreferences = view.getContext().getSharedPreferences(Tab3Fragment.PREFS_NAME, 0);
                    SharedPreferences.Editor mspEditor = mSharedPreferences.edit();
                    mspEditor.putStringSet(Tab3Fragment.SP_KEY_SAVED_LIST,new HashSet<String>(searchArrayList));
                    mspEditor.commit();
                    break;
                case LT_USE_FIREBASE:
                    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
                    mDatabaseRef.child(Tab3Fragment.FIREBASE_KEY_LIST).child(itemToRemove).removeValue();
                    break;
            }
            notifyDataSetChanged();
        }
    }
}
