package com.blogspot.droidcrib.callregister.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.eventbus.NewCallEvent;
import com.blogspot.droidcrib.callregister.eventbus.PickerDateChangedEvent;
import com.blogspot.droidcrib.callregister.eventbus.PickerTextChangedEvent;
import com.blogspot.droidcrib.callregister.eventbus.PickerTimeCangedEvent;
import com.facebook.cache.disk.DiskStorageCache;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;


/**
 *
 */

public class ReminderTabsPagerAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    int mNumOfTabs;


    private static final String TAG = "ReminderTabsPagerAdapter";

    public ReminderTabsPagerAdapter(Context context, int numOfTabs) {
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

        switch (position) {
            case 0:
                DatePicker datePicker = new DatePicker(mContext);
                datePicker.setCalendarViewShown(false);
                frameLayout.addView(datePicker, 0);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        Log.d(TAG, "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
                        EventBus.getDefault().post(new PickerDateChangedEvent(year, month, dayOfMonth));
                    }
                });
                break;

            case 1:
                TimePicker timePicker = new TimePicker(mContext);
                timePicker.setIs24HourView(true);
                timePicker.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {


                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        view.setIs24HourView(true);
                        Log.d(TAG, "hourOfDay = " + hourOfDay + " minute " + minute);
                        EventBus.getDefault().post(new PickerTimeCangedEvent(hourOfDay, minute));
                    }
                });

                frameLayout.addView(timePicker, 0);
                break;

            case 2:
                final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT); // Width , height
                EditText editText = new EditText(mContext);
                editText.setLayoutParams(lparams);
                frameLayout.addView(editText, 0);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        Log.d(TAG, "Memo text = " + s);
                        EventBus.getDefault().post(new PickerTextChangedEvent(s));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                break;

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
