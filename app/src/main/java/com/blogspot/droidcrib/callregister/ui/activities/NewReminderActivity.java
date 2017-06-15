package com.blogspot.droidcrib.callregister.ui.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.contract.Constants;
import com.blogspot.droidcrib.callregister.eventbus.PickerDateChangedEvent;
import com.blogspot.droidcrib.callregister.eventbus.PickerTextChangedEvent;
import com.blogspot.droidcrib.callregister.eventbus.PickerTimeCangedEvent;
import com.blogspot.droidcrib.callregister.model.AlarmRecord;
import com.blogspot.droidcrib.callregister.model.CallRecord;
import com.blogspot.droidcrib.callregister.receivers.AlarmsReceiver;
import com.blogspot.droidcrib.callregister.ui.adapters.MeasuredViewPager;
import com.blogspot.droidcrib.callregister.ui.adapters.TabsPagerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.blogspot.droidcrib.callregister.contract.Constants.EXTRA_ALARM_RECORD_ID;
import static com.blogspot.droidcrib.callregister.contract.Constants.EXTRA_CALL_RECORD_ID;

/**
 *
 */

public class NewReminderActivity extends AppCompatActivity {


    private static final String TAG = "NewReminderActivity";

    private MeasuredViewPager mViewPager;
    private long mRecordId;
    private TextView mDisplayName;
    private EditText mNote;
    private ImageView mDisplayCallType;
    private ImageView mDisplayAvatar;
    private TabLayout tabLayout;
    private static Calendar mCalendar = Calendar.getInstance();
    private static Date mDate = new Date();
    private AlarmRecord alarmRecord;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);

        EventBus.getDefault().register(this);

        mRecordId = getIntent().getLongExtra(EXTRA_CALL_RECORD_ID, 0);
        CallRecord callRecord = CallRecord.getRecordById(mRecordId);

        mDisplayName = (TextView) findViewById(R.id.id_person_name);
        mDisplayCallType = (ImageView) findViewById(R.id.id_call_type);
        mDisplayAvatar = (ImageView) findViewById(R.id.id_avatar);
        mNote = (EditText) findViewById(R.id.id_dialog_note);


        // Name
        mDisplayName.setText(callRecord.name);
        // Avatar
        if (callRecord.avatarUri != null) {
            mDisplayAvatar.setImageURI(Uri.parse(callRecord.avatarUri));
        } else {
            mDisplayAvatar.setImageResource(R.drawable.ic_person_black_48dp);
        }
        // Call type
        switch (callRecord.callType) {
            case Constants.INCOMING_CALL:
                mDisplayCallType.setImageResource(R.drawable.ic_call_received_black_48dp);
                break;

            case Constants.OUTGOING_CALL:
                mDisplayCallType.setImageResource(R.drawable.ic_call_made_black_48dp);
                break;

            case Constants.MISSED_CALL:
                mDisplayCallType.setImageResource(R.drawable.ic_call_missed_black_48dp);
                break;
        }


        //  Setup TabLayout
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Date"));
        tabLayout.addTab(tabLayout.newTab().setText("Time"));
        tabLayout.addTab(tabLayout.newTab().setText("Memo"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //  Setup ViewPager
        mViewPager = (MeasuredViewPager) findViewById(R.id.pager);
        final TabsPagerAdapter adapter = new TabsPagerAdapter(this, tabLayout.getTabCount());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout) {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    // do something with content
                }
                if (position == 1) {
                    // do something with content
                }
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        // Set current date-time into tabs headers
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        String date = sdf.format(mDate);
        tabLayout.getTabAt(0).setText(date);
        sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(mDate);
        tabLayout.getTabAt(1).setText(time);

        // Set initial values for AlarmRecord
        mCalendar.setTime(mDate);
        alarmRecord = new AlarmRecord();
        alarmRecord.year = mCalendar.get(Calendar.YEAR);
        alarmRecord.month = mCalendar.get(Calendar.MONTH);
        alarmRecord.dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
        alarmRecord.hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
        alarmRecord.minute = mCalendar.get(Calendar.MINUTE);
        alarmRecord.callRecord = callRecord;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Save AlarmRecord here
                long recordId = alarmRecord.save();
                // Set new AlarmManager here
                alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                // Set intent with notification message
                Intent intent = new Intent(getApplicationContext(), AlarmsReceiver.class);
                intent.putExtra(EXTRA_ALARM_RECORD_ID, recordId);
                alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

                mCalendar.setTimeInMillis(System.currentTimeMillis());
                mCalendar.set(Calendar.YEAR, alarmRecord.year);
                mCalendar.set(Calendar.MONTH, alarmRecord.month);
                mCalendar.set(Calendar.DAY_OF_MONTH, alarmRecord.dayOfMonth);
                mCalendar.set(Calendar.HOUR_OF_DAY, alarmRecord.hourOfDay);
                mCalendar.set(Calendar.MINUTE, alarmRecord.minute);

                alarmMgr.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), alarmIntent);

                Snackbar.make(view, "Reminder set", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                NewReminderActivity.this.finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    /////////////////////////////////////////////////////////////
    // EVENT LISTENERS
    /////////////////////////////////////////////////////////////

    @Subscribe
    public void onEvent(PickerDateChangedEvent event) {
        Log.d("onEvent", "PickerDateChangedEvent " + event.getYear() + " " + event.getMonth() + " " + event.getDayOfMonth());
        alarmRecord.year = event.getYear();
        alarmRecord.month = event.getMonth();
        alarmRecord.dayOfMonth = event.getDayOfMonth();

        // Setup tab header
        String dateStr = event.getMonth() + "/" + event.getDayOfMonth() + "/" + event.getYear();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            mDate = sdf.parse(dateStr);
            sdf = new SimpleDateFormat("dd MMM yyyy");
            String str = sdf.format(mDate);
            tabLayout.getTabAt(0).setText(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onEvent(PickerTimeCangedEvent event) {
        Log.d("onEvent", "PickerTimeCangedEvent " + event.getHourOfDay() + " " + event.getMinute());


        alarmRecord.hourOfDay = event.getHourOfDay();
        alarmRecord.minute = event.getMinute();
        // Setup tab header
        if (event.getMinute() < 10) {
            StringBuilder sb = new StringBuilder("0").append(event.getMinute());
            tabLayout.getTabAt(1).setText(event.getHourOfDay() + " : " + sb);
        } else {
            tabLayout.getTabAt(1).setText(event.getHourOfDay() + " : " + event.getMinute());
        }
    }

    @Subscribe
    public void onEvent(PickerTextChangedEvent event) {
        Log.d("onEvent", "PickerTextChangedEvent " + event.getText());
        alarmRecord.memoText = event.getText().toString();

    }


}
