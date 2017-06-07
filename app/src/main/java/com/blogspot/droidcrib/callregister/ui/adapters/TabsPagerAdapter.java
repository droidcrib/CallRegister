package com.blogspot.droidcrib.callregister.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.blogspot.droidcrib.locationsnotes.ui.fragments.LocationsListFragment;
import com.blogspot.droidcrib.locationsnotes.ui.fragments.MapFragment;

/**
 * Created by Andrey Bulanov on 1/23/2017.
 */

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "wptest";

    int mNumOfTabs;

    public TabsPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MapFragment tabMap = MapFragment.getInstance();
                return tabMap;
            case 1:
                LocationsListFragment tabLocationsList = LocationsListFragment.getInstance();
                return tabLocationsList;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
