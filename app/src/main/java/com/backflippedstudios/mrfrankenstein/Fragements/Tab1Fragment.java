package com.backflippedstudios.mrfrankenstein.Fragements;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.backflippedstudios.mrfrankenstein.R;

/**
 * Created by C0rbin on 7/29/2017.
 */

public class Tab1Fragment extends android.support.v4.app.Fragment {

    String title = "Formats";

    public Tab1Fragment(){
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
        return inflater.inflate(R.layout.fragement_tab1, container, false);
    }
}
