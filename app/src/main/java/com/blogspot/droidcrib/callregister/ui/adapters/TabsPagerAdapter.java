package com.blogspot.droidcrib.callregister.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.blogspot.droidcrib.callregister.R;


/**
 * Created by Andrey Bulanov on 1/23/2017.
 */

public class TabsPagerAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    int mNumOfTabs;


    private static final String TAG = "TabsPagerAdapter";

    public TabsPagerAdapter(Context context, int numOfTabs) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mNumOfTabs = numOfTabs;
    }


    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((FrameLayout) object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {


        View itemView = mLayoutInflater.inflate(R.layout.view_pick_pager_item, container, false);

        FrameLayout frameLayout = (FrameLayout) itemView.findViewById(R.id.view_pick_pager_item);
//        imageView.setImageBitmap(FsHelper.decodeSampledBitmapFromUri(mFiles[position].getAbsolutePath(), 160, 160));

        if (position == 0) {
            View debugLayout = mLayoutInflater.inflate(R.layout.test, frameLayout, false);
            frameLayout.addView(debugLayout);
        } else {
//            View debugLayout = mLayoutInflater.inflate(R.layout.test, frameLayout, false);
//            frameLayout.addView(debugLayout);

            TimePicker timePicker = new TimePicker(mContext);
            frameLayout.addView(timePicker, 0);
        }


//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }


}
