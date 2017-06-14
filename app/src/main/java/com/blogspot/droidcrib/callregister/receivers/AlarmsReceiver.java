package com.blogspot.droidcrib.callregister.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.ui.activities.MainActivity;

/**
 *
 */

public class AlarmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intentTL = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentTL,
                PendingIntent.FLAG_CANCEL_CURRENT);


        Notification notification = new Notification.Builder(context)
                .setContentTitle("New mail from ")
                .setContentText("subject")
                .setSmallIcon(R.drawable.ic_message_envelope)
                .setContentIntent(pendingIntent)
                .build();
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        nm.notify(1, notification);

    }
}
