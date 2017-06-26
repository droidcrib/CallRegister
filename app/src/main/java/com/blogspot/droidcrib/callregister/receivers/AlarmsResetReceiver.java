package com.blogspot.droidcrib.callregister.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blogspot.droidcrib.callregister.model.AlarmRecord;
import com.blogspot.droidcrib.callregister.ui.activities.NewReminderActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.blogspot.droidcrib.callregister.contract.Constants.ACTION_CREATE_NOTIFICATION;
import static com.blogspot.droidcrib.callregister.contract.Constants.EXTRA_ALARM_RECORD_ID;

/**
 * Created by BulanovA on 26.06.2017.
 */

public class AlarmsResetReceiver extends BroadcastReceiver {

    List<AlarmRecord> mAlarmRecordsList;
    private final long mCurrentTime = System.currentTimeMillis();
    private static Calendar mCalendar = Calendar.getInstance();
    private static Date mDate = new Date();
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {

        mAlarmRecordsList = AlarmRecord.queryAll();

        for (AlarmRecord record : mAlarmRecordsList) {
            if (mCurrentTime < record.alarmDateInMillis) {
                // Set new AlarmManager here
                alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                // Set intent with notification message
                Intent i = new Intent();
                i.putExtra(EXTRA_ALARM_RECORD_ID, record.getId());
                i.setAction(ACTION_CREATE_NOTIFICATION);
                long recId = record.getId();
                alarmIntent = PendingIntent.getBroadcast(context, (int)recId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Set alarm date and time
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                mCalendar.set(Calendar.YEAR, record.year);
                mCalendar.set(Calendar.MONTH, record.month);
                mCalendar.set(Calendar.DAY_OF_MONTH, record.dayOfMonth);
                mCalendar.set(Calendar.HOUR_OF_DAY, record.hourOfDay);
                mCalendar.set(Calendar.MINUTE, record.minute);

                // Set new alarm
                alarmMgr.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), alarmIntent);
            }
        }

    }
}
