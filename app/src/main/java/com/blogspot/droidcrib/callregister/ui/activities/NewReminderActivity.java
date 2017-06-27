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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.contract.Constants;
import com.blogspot.droidcrib.callregister.eventbus.PickerDateChangedEvent;
import com.blogspot.droidcrib.callregister.eventbus.PickerTextChangedEvent;
import com.blogspot.droidcrib.callregister.eventbus.PickerTimeCangedEvent;
import com.blogspot.droidcrib.callregister.model.AlarmRecord;
import com.blogspot.droidcrib.callregister.model.CallRecord;
import com.blogspot.droidcrib.callregister.ui.adapters.MeasuredViewPager;
import com.blogspot.droidcrib.callregister.ui.adapters.ReminderTabsPagerAdapter;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.blogspot.droidcrib.callregister.contract.Constants.ACTION_CREATE_NOTIFICATION;
import static com.blogspot.droidcrib.callregister.contract.Constants.EXTRA_ALARM_RECORD_ID;
import static com.blogspot.droidcrib.callregister.contract.Constants.EXTRA_CALL_RECORD_ID;

/**
 *
 */

public class NewReminderActivity extends AppCompatActivity {


    private static final String TAG = "trace_notifications";

    private MeasuredViewPager mViewPager;
    private long mCallRecordId;
    private TextView mDisplayName;
    private EditText mNote;
    private ImageView mDisplayCallType;
    private SimpleDraweeView mDisplayAvatar;
    private TabLayout mTabLayout;
    private static Calendar mCalendar = Calendar.getInstance();
    private static Date mDate = new Date();
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private AlarmHolder alarmHolder = new AlarmHolder();
    private RelativeLayout idCallInfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);

        EventBus.getDefault().register(this);

        mCallRecordId = getIntent().getLongExtra(EXTRA_CALL_RECORD_ID, -1);
        CallRecord callRecord = CallRecord.getRecordById(mCallRecordId);

        idCallInfo = (RelativeLayout) findViewById(R.id.id_call_info);
        mDisplayName = (TextView) findViewById(R.id.id_person_name);
        mDisplayCallType = (ImageView) findViewById(R.id.id_call_type);
        mDisplayAvatar = (SimpleDraweeView) findViewById(R.id.id_avatar);
        mNote = (EditText) findViewById(R.id.id_dialog_note);

        // If callRecord id received in intent
        if(mCallRecordId != -1) {
            idCallInfo.setVisibility(View.VISIBLE);
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
        }


        //  Setup TabLayout
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText("Date"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Time"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Memo"));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //  Setup ViewPager
        mViewPager = (MeasuredViewPager) findViewById(R.id.pager);
        final ReminderTabsPagerAdapter adapter = new ReminderTabsPagerAdapter(this, mTabLayout.getTabCount());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout) {
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

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        mTabLayout.getTabAt(0).setText(date);
        sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(mDate);
        mTabLayout.getTabAt(1).setText(time);

        // Set initial values for AlarmRecord
        mCalendar.setTime(mDate);
        alarmHolder.year = mCalendar.get(Calendar.YEAR);
        alarmHolder.month = mCalendar.get(Calendar.MONTH);
        alarmHolder.dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
        alarmHolder.hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
        alarmHolder.minute = mCalendar.get(Calendar.MINUTE);
        alarmHolder.callRecord = callRecord;
        alarmHolder.memoText = "this is memoShort text. some very very long text here. " +
                "Еще и на русском добавим, чтоб больше было. І українською звичайно ж";

        Log.d(TAG, "-- AlarmHolder initial values: " + alarmHolder.toString());


        // FAB action
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO: do this in asynctask
                long recId = AlarmRecord.insert(
                        alarmHolder.year,
                        alarmHolder.month,
                        alarmHolder.dayOfMonth,
                        alarmHolder.hourOfDay,
                        alarmHolder.minute,
                        alarmHolder.callRecord,
                        alarmHolder.memoText
                );

                // Set new AlarmManager here
                alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                // Set intent with notification message
                Intent intent = new Intent();
                intent.putExtra(EXTRA_ALARM_RECORD_ID, recId);
                intent.setAction(ACTION_CREATE_NOTIFICATION);
                alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), (int)recId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Set alarm date and time
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                mCalendar.set(Calendar.YEAR, alarmHolder.year);
                mCalendar.set(Calendar.MONTH, alarmHolder.month);
                mCalendar.set(Calendar.DAY_OF_MONTH, alarmHolder.dayOfMonth);
                mCalendar.set(Calendar.HOUR_OF_DAY, alarmHolder.hourOfDay);
                mCalendar.set(Calendar.MINUTE, alarmHolder.minute);

                // Set new alarm
                alarmMgr.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), alarmIntent);
                Snackbar.make(view, "Reminder set", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Log.d(TAG, "==> NewReminderActivity extra alarm id send = " + recId);
                Log.d(TAG, "==> NewReminderActivity  alarm intent send = " + alarmIntent.toString());
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
        alarmHolder.year = event.getYear();
        alarmHolder.month = event.getMonth();
        alarmHolder.dayOfMonth = event.getDayOfMonth();

        // Setup tab header
        String dateStr = event.getMonth() + "/" + event.getDayOfMonth() + "/" + event.getYear();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            mDate = sdf.parse(dateStr);
            sdf = new SimpleDateFormat("dd MMM yyyy");
            String str = sdf.format(mDate);
            mTabLayout.getTabAt(0).setText(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onEvent(PickerTimeCangedEvent event) {
        Log.d("onEvent", "PickerTimeCangedEvent " + event.getHourOfDay() + " " + event.getMinute());


        alarmHolder.hourOfDay = event.getHourOfDay();
        alarmHolder.minute = event.getMinute();
        // Setup tab header
        if (event.getMinute() < 10) {
            StringBuilder sb = new StringBuilder("0").append(event.getMinute());
            mTabLayout.getTabAt(1).setText(event.getHourOfDay() + " : " + sb);
        } else {
            mTabLayout.getTabAt(1).setText(event.getHourOfDay() + " : " + event.getMinute());
        }
    }

    @Subscribe
    public void onEvent(PickerTextChangedEvent event) {
        Log.d("onEvent", "PickerTextChangedEvent " + event.getText());
        alarmHolder.memoText = event.getText().toString();

    }


    private static class AlarmHolder {
         int year;
         int month;
         int dayOfMonth;
         int hourOfDay;
         int minute;
         CallRecord callRecord;
         String memoText;

        @Override
        public String toString() {
            return "AlarmHolder{" +
                    "year=" + year +
                    ", month=" + month +
                    ", dayOfMonth=" + dayOfMonth +
                    ", hourOfDay=" + hourOfDay +
                    ", minute=" + minute +
                    ", callRecord=" + callRecord +
                    ", memoText='" + memoText + '\'' +
                    '}';
        }
    }


}
