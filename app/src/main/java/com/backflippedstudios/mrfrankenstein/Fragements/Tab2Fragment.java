package com.backflippedstudios.mrfrankenstein.Fragements;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.backflippedstudios.mrfrankenstein.R;

import java.util.Random;

/**
 * Created by C0rbin on 7/29/2017.
 */

public class Tab2Fragment extends Fragment {


    String title = "Change Text";
    View mViewGroup;

    public Tab2Fragment() {
        //Required empyy public constructor
    }

    public String getTitle() {
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
        View view = inflater.inflate(R.layout.fragement_tab2, container, false);
        mViewGroup = view;

        //Create onlick listener for tab 2 to change the Text View on that page to a text input +
        //random number added at the end
        Button changeTextImageTab2 = (Button) view.findViewById(R.id.buttonChangeText);
        changeTextImageTab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tab2Text = (TextView) mViewGroup.findViewById(R.id.textViewTab2);
                EditText tab2EditText = (EditText) mViewGroup.findViewById(R.id.editTextTab2);
                Random rand = new Random();
                tab2Text.setText(tab2EditText.getText() + " " + Integer.toString(rand.nextInt(100)));
            }
        });

        return view;
    }
}
