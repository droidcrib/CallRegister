package com.blogspot.droidcrib.callregister.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.blogspot.droidcrib.callregister.ui.fragments.CallsListFragment;

/**
 *
 */

public class MainTabsPagerAdapter extends FragmentPagerAdapter {

    int mNumOfTabs;

    public MainTabsPagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return CallsListFragment.getInstance();
//            case 1:
//                return  CallsListFragment.getInstance();
//            case 3:
//                return CallsListFragment.getInstance();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
