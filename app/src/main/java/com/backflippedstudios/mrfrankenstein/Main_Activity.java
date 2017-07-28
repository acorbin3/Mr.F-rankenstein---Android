package com.backflippedstudios.mrfrankenstein;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Main_Activity extends AppCompatActivity {
    private float lastX;
    private TabHost mTabHost;
    private int currentTab;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String SP_KEY_SAVED_LIST = "GE_Engines";
    private static final String SP_KEY_CHECKBOX = "PersistantCheckbox";
    private CustomListAdapter listAdapter;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mspEditor;
    private MediaPlayer mp_heliTakeoff;
    private MediaPlayer mp_airplaneTakeoff;
    private FirebaseDatabase mDatabase;

    public enum ListType{
        LT_USE_SHARED_PREFS,
        LT_USE_INTERNAL_LIST,
        LT_USE_FIREBASE
    }

    private final ListType listTypeToUse = ListType.LT_USE_FIREBASE;

    ArrayList<String> trackedList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed__example);

        mTabHost = (TabHost) findViewById(R.id.tab_host);
        mTabHost.setup();

        //Create tabs and add to the tabhost
        TabHost.TabSpec mSpc = mTabHost.newTabSpec("First Tab");
        mSpc.setContent(R.id.tab1);
        mSpc.setIndicator("Formats");
        mTabHost.addTab(mSpc);

        mSpc = mTabHost.newTabSpec("Second Tab");
        mSpc.setContent(R.id.tab2);
        mSpc.setIndicator("Change Text");
        mTabHost.addTab(mSpc);

        mSpc = mTabHost.newTabSpec("Third Tab");
        mSpc.setContent(R.id.tab3);
        mSpc.setIndicator("Update List");
        mTabHost.addTab(mSpc);

        mSpc = mTabHost.newTabSpec("4th Tab");
        mSpc.setContent(R.id.tab4);
        mSpc.setIndicator("Alerts Sounds");
        mTabHost.addTab(mSpc);



        //Create animation between swiping tabs
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId)
            {
                View currentView = mTabHost.getCurrentView();

                if (mTabHost.getCurrentTab() > currentTab)
                {
                    currentView.setAnimation( inFromRightAnimation() );
                }
                else
                {
                    currentView.setAnimation( outToRightAnimation() );
                }

                currentTab = mTabHost.getCurrentTab();
            }
        });


        /////////////////
        ///Tab 2 setup///

        //Create onlick listener for tab 2 to change the Text View on that page to a text input +
        //random number added at the end
        Button changeTextImageTab2 = (Button) findViewById(R.id.button1);
        changeTextImageTab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tab2Text = (TextView) findViewById(R.id.textView3);
                EditText tab2EditText = (EditText) findViewById(R.id.editText);
                Random rand = new Random();
                tab2Text.setText(tab2EditText.getText() + " " + Integer.toString(rand.nextInt(100)));

            }
        });
        ///End Tab 2 setup//
        ////////////////////

        /////////////////
        ///Tab 3 setup///


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
                mSharedPreferences = getSharedPreferences(PREFS_NAME, 0);
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
                final TextView tv_listTitle = (TextView) findViewById(R.id.textViewListTitle);
                tv_listTitle.setText("Epic Cars");
                //Update the edit text that says insert car
                EditText et_listEditBox = (EditText) findViewById(R.id.editTextListEntry);
                et_listEditBox.setText("Enter the next Epic car!");
                break;
            case LT_USE_FIREBASE:
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
                        TextView tv_liveView = (TextView) findViewById(R.id.textViewLiveDB);
                        tv_liveView.setText(value);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("DEBUG", "Failed to read value.", error.toException());
                    }
                });

                DatabaseReference listRef = mDatabase.getReference("message");
                break;

        }

        //Inflate the ListView on tab3
        ListView listView = (ListView) findViewById(R.id.list1);
        listAdapter = new CustomListAdapter(this, trackedList);
        listView.setAdapter(listAdapter);


        //Create on click listener for tab 3 to add an item to the list
        ImageView iv_InsertItem = (ImageView) findViewById(R.id.imageView2);
        final EditText et_NewItemText = (EditText) findViewById(R.id.editTextListEntry);
        iv_InsertItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Never insert the status from the last item that was inserted
                if(!et_NewItemText.getText().toString().toLowerCase().equals("inserted")){

                    trackedList.add(et_NewItemText.getText().toString());
                    listAdapter.updateList(trackedList);
                    et_NewItemText.setText("Inserted");

                    switch (listTypeToUse){
                        case LT_USE_INTERNAL_LIST:
                            mspEditor.putStringSet(SP_KEY_SAVED_LIST,new HashSet<String>(trackedList));
                            mspEditor.commit();
                            break;
                        case LT_USE_FIREBASE:
                            break;
                    }

                }
            }
        });


        //Check notificaion button has been pressed, get text box text and send notification
        //based on the timer picker for how long in the future
        //Android Manifest needs to be updated to allow receiving delayed notifications
        final TextView tv_notificationText = (TextView) findViewById(R.id.editText_notification);
        Button b_sendNotification = (Button) findViewById(R.id.buttonNotification);
        final EditText et_delayTime = (EditText) findViewById(R.id.editTextDelayTime);
        b_sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationCompat.Builder mBuilder =
                        (NotificationCompat.Builder) new NotificationCompat.Builder(view.getContext())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("My notification")
                                .setContentText(tv_notificationText.getText());
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                Notification notification = mBuilder.build();

                //Check if edit text is an integer and if so parse it and delay the notification
                //Otherwise create the notification now.
                if(et_delayTime.getText().toString().matches("^-?\\d+$")) {
                    Integer delay = Integer.parseInt(et_delayTime.getText().toString());

                    Intent notificationIntent = new Intent(view.getContext(), NotificationPublisher.class);
                    notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
                    notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(view.getContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    long futureInMillis = SystemClock.elapsedRealtime() + delay;
                    AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
                }
                else{
                    mNotificationManager.notify(001, notification);
                }
            }
        });

        ///End Tab 3 setup//
        ////////////////////



        //TAB4 initilizes and callbacks
        //1. Check if checkbox was selected from the shared prefercens
        //2. Create callback when checkbox state changes and update shared prefs
        //3. Create callback for button to play aircraft takeoff
        //4. Create callback to play heli sound
        //5. Create stop button to stop sounds
        //6. Create images and move across the screen when play buttons have been clicked
        //7. When adding a GIF here is the notes on what needs to be updated in the gradle file:
        //     https://stackoverflow.com/a/35273824

        mSharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        mspEditor = mSharedPreferences.edit();
        CheckBox tab4CheckBox = (CheckBox) findViewById(R.id.cb_tab4);
        if(mSharedPreferences.getBoolean(SP_KEY_CHECKBOX,false)){
            tab4CheckBox.setChecked(true);
        }

        tab4CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mspEditor.putBoolean(SP_KEY_CHECKBOX,b);
                mspEditor.commit();
            }
        });



        Button airplaneTakeOff = (Button) findViewById(R.id.b_takeoff);
        mp_airplaneTakeoff = MediaPlayer.create(this, R.raw.aircraft003);
        airplaneTakeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp_airplaneTakeoff.start();
                RelativeLayout tab4 = (RelativeLayout) findViewById(R.id.tab4);
                ImageView iv_airplane = new ImageView(Main_Activity.this);
                iv_airplane.setScaleX((float) .5);
                iv_airplane.setScaleY((float) .5);

                iv_airplane.setImageResource(R.drawable.c919);
                iv_airplane.setX(0);
                iv_airplane.setY(25);
                iv_airplane.setVisibility(View.VISIBLE);
                iv_airplane.animate().alpha(0).x(1000).y(2000).setDuration(5000);
                tab4.addView(iv_airplane);

            }
        });

        Button heliTakeOff = (Button) findViewById(R.id.b_heliHover);
        mp_heliTakeoff = MediaPlayer.create(this, R.raw.heli_hover);
        heliTakeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp_heliTakeoff.start();
                RelativeLayout tab4 = (RelativeLayout) findViewById(R.id.tab4);
                ImageView iv_heli = new ImageView(Main_Activity.this);
                iv_heli.setScaleX((float) .5);
                iv_heli.setScaleY((float) .5);

                iv_heli.setImageResource(R.drawable.popup_helicopter);
                iv_heli.setX(0);
                iv_heli.setY(1000);
                iv_heli.setVisibility(View.VISIBLE);
                iv_heli.animate().alpha(0).x(-200).y(0).scaleX((float).1).scaleY((float).1).setDuration(5000);
                tab4.addView(iv_heli);
            }
        });

        Button stop = (Button) findViewById(R.id.b_stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp_airplaneTakeoff.stop();
                mp_heliTakeoff.stop();
                try {
                    mp_heliTakeoff.prepare();
                    mp_airplaneTakeoff.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Once the MediaPlaer has been stopped you need to reprepare the Media Players
            }
        });

        ///End Tab 4///
        ///////////////




    }
    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch(touchevent.getAction()){
            case MotionEvent.ACTION_DOWN:

            {
                lastX = touchevent.getX();
                break;
            }
            case MotionEvent.ACTION_UP: {
                float currentX = touchevent.getX();

                // if left to right swipe on screen
                if (lastX < currentX) {

                    switchTabs(false);
                }

                // if right to left swipe on screen
                if (lastX > currentX) {
                    switchTabs(true);
                }

                break;
            }
        }
        return false;
    }

    public void switchTabs(boolean direction) {
        if (!direction) // true = move left
        {
            if (mTabHost.getCurrentTab() == 0)
                mTabHost.setCurrentTab(mTabHost.getTabWidget().getTabCount() - 1);
            else
                mTabHost.setCurrentTab(mTabHost.getCurrentTab() - 1);
        } else
        // move right
        {
            if (mTabHost.getCurrentTab() != (mTabHost.getTabWidget()
                    .getTabCount() - 1))
                mTabHost.setCurrentTab(mTabHost.getCurrentTab() + 1);
            else
                mTabHost.setCurrentTab(0);
        }
    }

    public Animation inFromRightAnimation()
    {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(240);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    public Animation outToRightAnimation()
    {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(240);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }


}
