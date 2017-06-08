package com.blogspot.droidcrib.callregister.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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

import java.util.Calendar;


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
//            View debugLayout = mLayoutInflater.inflate(R.layout.test, frameLayout, false);
//            frameLayout.addView(debugLayout);

            DatePicker datePicker = new DatePicker(mContext);
            datePicker.setCalendarViewShown(false);
            frameLayout.addView(datePicker, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                @Override
                public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    Log.d(TAG, "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);

                }
            });


        } else {
            TimePicker timePicker = new TimePicker(mContext);
            timePicker.setIs24HourView(true);
            frameLayout.addView(timePicker, 0);

            timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    Log.d(TAG, "hourOfDay = " + hourOfDay + " minute " + minute);
                }
            });

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