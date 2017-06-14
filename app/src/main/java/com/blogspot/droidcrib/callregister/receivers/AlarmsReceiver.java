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

        Intent intent1 = new Intent(Intent.ACTION_SEARCH);
        PendingIntent pendingIntent1 = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);

        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intentTL,
                PendingIntent.FLAG_CANCEL_CURRENT);


        Notification notification = new Notification.Builder(context)
                .setContentTitle("New mail from ")
                .setContentText("subject")
                .setSmallIcon(R.drawable.ic_message_envelope)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_add_white_48dp, null, pendingIntent)
                .addAction(R.drawable.ic_delete_black_48dp, null, pendingIntent1)
                .addAction(R.drawable.ic_arrow_back_white_24dp, null, pendingIntent2)
                .build();
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        nm.notify(1, notification);

    }
}
