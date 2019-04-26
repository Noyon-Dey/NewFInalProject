package com.example.bitmproject.tourmate;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by saran on 1/4/2018.
 */

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    int tabCount;
    CurrentWeatherFragment currentWeatherFragment;
    ForcastWeatherFragment forcastWeatherFragment;


    public TabPagerAdapter(FragmentManager fm,
                           int tabCount,
                           CurrentWeatherFragment currentWeatherFragment,
                           ForcastWeatherFragment forcastWeatherFragment) {
        super(fm);
        this.tabCount=tabCount;
        this.currentWeatherFragment=currentWeatherFragment;
        this.forcastWeatherFragment=forcastWeatherFragment;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position){
            case 0:
                return currentWeatherFragment;
            case 1:
                return forcastWeatherFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}


