package com.backflippedstudios.mrfrankenstein.Fragements;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.backflippedstudios.mrfrankenstein.Main_Activity;
import com.backflippedstudios.mrfrankenstein.NotificationPublisher;
import com.backflippedstudios.mrfrankenstein.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

/**
 * Created by C0rbin on 7/29/2017.
 */

public class Tab4Fragment extends Fragment {
    String title = "Alerts/Sounds";

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mspEditor;
    private MediaPlayer mp_heliTakeoff;
    private MediaPlayer mp_airplaneTakeoff;
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String SP_KEY_CHECKBOX = "PersistantCheckbox";
    private View tabView;

    public Tab4Fragment(){
        //Required empyy public constructor
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
        tabView = inflater.inflate(R.layout.fragement_tab4, container, false);
        ///////////////////////
        ///Start Tab 4 Setup///
        //Check notificaion button has been pressed, get text box text and send notification
        //based on the timer picker for how long in the future
        //Android Manifest needs to be updated to allow receiving delayed notifications
        final TextView tv_notificationText = (TextView) tabView.findViewById(R.id.editText_notification);
        Button b_sendNotification = (Button) tabView.findViewById(R.id.buttonNotification);
        final EditText et_delayTime = (EditText) tabView.findViewById(R.id.editTextDelayTime);
        b_sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationCompat.Builder mBuilder =
                        (NotificationCompat.Builder) new NotificationCompat.Builder(view.getContext())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("My notification")
                                .setContentText(tv_notificationText.getText());
                NotificationManager mNotificationManager =
                        (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

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
                    AlarmManager alarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
                }
                else{
                    mNotificationManager.notify(001, notification);
                }
            }
        });



        //TAB4 initilizes and callbacks
        //1. Check if checkbox was selected from the shared prefercens
        //2. Create callback when checkbox state changes and update shared prefs
        //3. Create callback for button to play aircraft takeoff
        //4. Create callback to play heli sound
        //5. Create stop button to stop sounds
        //6. Create images and move across the screen when play buttons have been clicked
        //7. When adding a GIF here is the notes on what needs to be updated in the gradle file:
        //     https://stackoverflow.com/a/35273824

        mSharedPreferences = getContext().getSharedPreferences(PREFS_NAME, 0);
        mspEditor = mSharedPreferences.edit();
        CheckBox tab4CheckBox = (CheckBox) tabView.findViewById(R.id.cb_tab4);
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



        Button airplaneTakeOff = (Button) tabView.findViewById(R.id.b_takeoff);
        mp_airplaneTakeoff = MediaPlayer.create(getContext(), R.raw.aircraft003);
        airplaneTakeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp_airplaneTakeoff.start();
                RelativeLayout tab4 = (RelativeLayout) tabView.findViewById(R.id.tab4);
                ImageView iv_airplane = new ImageView(getContext());
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

        Button heliTakeOff = (Button) tabView.findViewById(R.id.b_heliHover);
        mp_heliTakeoff = MediaPlayer.create(getContext(), R.raw.heli_hover);
        heliTakeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp_heliTakeoff.start();
                RelativeLayout tab4 = (RelativeLayout) tabView.findViewById(R.id.tab4);
                ImageView iv_heli = new ImageView(getContext());
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

        Button stop = (Button) tabView.findViewById(R.id.b_stop);
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

        return tabView;
    }
}
