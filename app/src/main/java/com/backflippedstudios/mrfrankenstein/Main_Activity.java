package com.backflippedstudios.mrfrankenstein;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.backflippedstudios.mrfrankenstein.Fragements.Tab1Fragment;
import com.backflippedstudios.mrfrankenstein.Fragements.Tab2Fragment;
import com.backflippedstudios.mrfrankenstein.Fragements.Tab3Fragment;
import com.backflippedstudios.mrfrankenstein.Fragements.Tab4Fragment;


public class Main_Activity extends AppCompatActivity {
    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;

    //All tab fragements
    Tab1Fragment tab1Fragment;
    Tab2Fragment tab2Fragment;
    Tab3Fragment tab3Fragment;
    Tab4Fragment tab4Fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipable_tabs);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);
        setupViewPager(viewPager);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        tab1Fragment = new Tab1Fragment();
        tab2Fragment = new Tab2Fragment();
        tab3Fragment = new Tab3Fragment();
        tab4Fragment = new Tab4Fragment();

        adapter.addFragment(tab1Fragment, tab1Fragment.getTitle());
        adapter.addFragment(tab2Fragment, tab2Fragment.getTitle());
        adapter.addFragment(tab3Fragment, tab3Fragment.getTitle());
        adapter.addFragment(tab4Fragment, tab4Fragment.getTitle());

        viewPager.setAdapter(adapter);
    }

}
