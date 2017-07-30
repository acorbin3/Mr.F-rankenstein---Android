package com.backflippedstudios.mrfrankenstein.Fragements;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.backflippedstudios.mrfrankenstein.CustomListAdapter;
import com.backflippedstudios.mrfrankenstein.Main_Activity;
import com.backflippedstudios.mrfrankenstein.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by C0rbin on 7/29/2017.
 */

public class Tab3Fragment extends Fragment {
    String title = "Update List";

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String SP_KEY_SAVED_LIST = "GE_Engines";
    public static final String FIREBASE_KEY_LIST = "tvShows";
    private CustomListAdapter listAdapter = null;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mspEditor;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private View thisView;

    public enum ListType{
        LT_USE_SHARED_PREFS,
        LT_USE_INTERNAL_LIST,
        LT_USE_FIREBASE
    }

    public static final ListType listTypeToUse = ListType.LT_USE_FIREBASE;

    public static ArrayList<String> trackedList = new ArrayList<String>();

    public Tab3Fragment(){
        //Required empy public constructor
    }

    public String getTitle(){
        return this.title;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragement_tab3, container, false);

        TextView tv_listTitle = (TextView) view.findViewById(R.id.textViewTitle);
        EditText et_listEditBox = (EditText) view.findViewById(R.id.editTextNewItem);
        switch (listTypeToUse){
            case LT_USE_INTERNAL_LIST:
                //Add all items to the trackedList list
                trackedList.add("TF39(1968)");
                trackedList.add("CF6(1970)");
                trackedList.add("CFM56/F108(1982)");
                trackedList.add("GE90(1994)");
                trackedList.add("GP7200(2006)");
                trackedList.add("GEnx(2007)");
                trackedList.add("LEAP-X(2016)");
                trackedList.add("GE Passport(2014 planned)");
                trackedList.add("GE9X(2018 planned)");
                break;
            case LT_USE_SHARED_PREFS:
                //Persistant Data on tab 3 list. Different options SQL, properties? Firebase
                //Shared preferences
                mSharedPreferences = getContext().getSharedPreferences(PREFS_NAME, 0);
                mspEditor = mSharedPreferences.edit();

                Set<String> spList = mSharedPreferences.getStringSet(SP_KEY_SAVED_LIST, null);

                //When shared prefs are known refresh the trackedList,
                // Otherwise shared prefs are empty we need to initilize them with some default values.
                if (spList != null) {
                    for (String item : spList) {
                        trackedList.add(item);
                    }
                }
                else{
                    trackedList.add("Tesla");
                    trackedList.add("Honda");
                    trackedList.add("Chevy");
                    trackedList.add("Ford");
                    trackedList.add("Hyundai");


                    mspEditor.putStringSet(SP_KEY_SAVED_LIST,new HashSet<String>(trackedList));
                    mspEditor.commit();
                    spList = mSharedPreferences.getStringSet(SP_KEY_SAVED_LIST, null);

                    System.out.print("List of all items in prefs");
                    for(String item : spList){
                        System.out.print(item);
                    }
                }

                //Update the text view on 3rd tab to know its not engines
                //Update the edit text that says insert car
                tv_listTitle.setText("Epic Cars");
                et_listEditBox.setText("Enter the next Epic car!");
                break;
            case LT_USE_FIREBASE:
                tv_listTitle.setText("Epic Movies");
                et_listEditBox.setText("Enter a Epic Movie!");

                mDatabase = FirebaseDatabase.getInstance();
                DatabaseReference myRef = mDatabase.getReference("message");

                myRef.setValue("Hello, World!");

                //Realtime updates to app
                // Read from the database
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String value = dataSnapshot.getValue(String.class);
                        Log.d("DEBUG", "Value is: " + value);
                        TextView tv_liveView = (TextView) getView().findViewById(R.id.textViewDBItem);
                        tv_liveView.setText(value);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("DEBUG", "Failed to read value.", error.toException());
                    }
                });

                DatabaseReference listRef = mDatabase.getReference(FIREBASE_KEY_LIST);
                listRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        trackedList.clear();
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            trackedList.add(child.getKey().toString());
                        }
                        //Notify the listAdapter of the updated track list when it changes and exits
                        if(listAdapter != null){
                            listAdapter.updateList(trackedList);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                break;

        }

        //Inflate the ListView on tab3
        ListView listView = (ListView) view.findViewById(R.id.list1);
        listAdapter = new CustomListAdapter(getContext(), trackedList);
        listView.setAdapter(listAdapter);


        //Create on click listener for tab 3 to add an item to the list
        ImageView iv_InsertItem = (ImageView) view.findViewById(R.id.imageViewInsert);
        final EditText et_NewItemText = (EditText) view.findViewById(R.id.editTextNewItem);
        iv_InsertItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newListItem = et_NewItemText.getText().toString();

                switch (listTypeToUse) {
                    case LT_USE_SHARED_PREFS:
                        trackedList.add(newListItem);
                        listAdapter.updateList(trackedList);
                        mspEditor.putStringSet(SP_KEY_SAVED_LIST, new HashSet<String>(trackedList));
                        mspEditor.commit();
                        break;
                    case LT_USE_FIREBASE:
                        if (mDatabaseRef == null) {
                            mDatabaseRef = FirebaseDatabase.getInstance().getReference();
                        }
                        mDatabaseRef.child(FIREBASE_KEY_LIST).child(newListItem).setValue(new Boolean(true));
                        //We dont need to update trackedList here because the callback for onDataChange
                        // will be called and that will update the trackedList.

                        break;
                }

                et_NewItemText.setText("");
            }
        });

        return view;
    }
}
