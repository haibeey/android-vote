package com.haibeey.vote.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.haibeey.vote.fragments.TabFragment;
import com.haibeey.vote.fragments.TabFragment2;

/**
 * Created by haibeey on 11/20/2017.
 */

public class TabAdapter extends FragmentPagerAdapter {

    Activity contextActivity;
    FragmentManager fragmentManager;

    public TabAdapter(FragmentManager fm, Activity activity) {
        super(fm);
        fragmentManager=fm;
        contextActivity=activity;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new TabFragment();
            case 1:
                return  new TabFragment2();
        }
        return new TabFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0 :
                return "Topics";
            case 1 :
                return "create an election";
        }
        return  null;
    }

}
