package com.blogspot.droidcrib.callregister.receivers;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.ui.activities.MainActivity;

import static com.blogspot.droidcrib.callregister.contract.Constants.ACTION_REMOVE_NOTIFICATION;
import static com.blogspot.droidcrib.callregister.contract.Constants.ACTION_SHOW_ALARM_DETAILS;
import static com.blogspot.droidcrib.callregister.contract.Constants.EXTRA_PHONE_NUMBER;

/**
 *
 */

public class AlarmsReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Log.d(TAG, "action received = " + intent.getAction());
        Log.d(TAG, "extra received = " + intent.getStringExtra(EXTRA_PHONE_NUMBER));

        if(intent.getAction() != null && intent.getAction().equals(ACTION_REMOVE_NOTIFICATION)){
            nm.cancel(100500);
            return;
        }


        Intent intentAction = new Intent(context, MainActivity.class);
        intentAction.setAction(ACTION_SHOW_ALARM_DETAILS);
        intentAction.putExtra(EXTRA_PHONE_NUMBER, "123 456 789");
        PendingIntent pIntentAction = PendingIntent.getActivity(context, 0, intentAction,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Intent intentDismiss = new Intent();
        intentDismiss.setAction(ACTION_REMOVE_NOTIFICATION);
        intentDismiss.putExtra(EXTRA_PHONE_NUMBER, "0937517151");
        PendingIntent pIntentDismiss = PendingIntent.getBroadcast(context, 0, intentDismiss,
                PendingIntent.FLAG_CANCEL_CURRENT);


        NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setPriority(Notification.PRIORITY_MAX)
                .setWhen(0)
                .setSmallIcon(R.drawable.ic_watch_later_white_24dp)
                //.setLargeIcon()                               // User avatar here
                .setContentTitle("New mail from ")              // User name or phone number here
                .setContentText("subject")                     // Memo text here
                .setStyle(new NotificationCompat.BigTextStyle().bigText("some big text"))
                .setContentIntent(pIntentAction)  // goto reminder details on click
                .addAction(R.drawable.ic_close_white_24dp, "Dismiss", pIntentDismiss)
                .setAutoCancel(true);
        nm.notify(100500, notification.build());

    }
}
