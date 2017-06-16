package com.blogspot.droidcrib.callregister.receivers;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.model.AlarmRecord;
import com.blogspot.droidcrib.callregister.ui.activities.MainActivity;

import java.io.IOException;

import static com.blogspot.droidcrib.callregister.contract.Constants.ACTION_REMOVE_NOTIFICATION;
import static com.blogspot.droidcrib.callregister.contract.Constants.ACTION_SHOW_ALARM_DETAILS;
import static com.blogspot.droidcrib.callregister.contract.Constants.EXTRA_ALARM_RECORD_ID;
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
        Log.d(TAG, "extra received = " + intent.getStringExtra(EXTRA_ALARM_RECORD_ID));

        int recordId = (int) intent.getLongExtra(EXTRA_ALARM_RECORD_ID, -1);
        AlarmRecord alarmRecord = AlarmRecord.getRecordById(recordId);
        Log.d(TAG, "-- alarmRecord received : " + alarmRecord.toString());
        String name = alarmRecord.callRecord.name;
        String memo = alarmRecord.memoText;


        Bitmap avatar = null;
        Drawable d = context.getResources().getDrawable(R.drawable.ic_person_outline_white_48dp);
        Drawable currentState = d.getCurrent();
        if (currentState instanceof BitmapDrawable)
            avatar = ((BitmapDrawable) currentState).getBitmap();

        if (alarmRecord.callRecord.avatarUri != null) {
            Uri imageUri = Uri.parse(alarmRecord.callRecord.avatarUri);
            try {
                avatar = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Bitmap circeAvatar = getCroppedBitmap(avatar, 96);
        avatar.recycle();


        Intent intentAction = new Intent(context, MainActivity.class);
        intentAction.setAction(ACTION_SHOW_ALARM_DETAILS);
        intentAction.putExtra(EXTRA_ALARM_RECORD_ID, recordId);
        PendingIntent pIntentAction = PendingIntent.getActivity(context, 0, intentAction,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Intent intentDismiss = new Intent();
        intentDismiss.setAction(ACTION_REMOVE_NOTIFICATION);
        intentDismiss.putExtra(EXTRA_ALARM_RECORD_ID, recordId);
        PendingIntent pIntentDismiss = PendingIntent.getBroadcast(context, 0, intentDismiss,
                PendingIntent.FLAG_CANCEL_CURRENT);


        NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setPriority(Notification.PRIORITY_MAX)
                .setWhen(0)
                .setSmallIcon(R.drawable.ic_watch_later_white_24dp)
                .setLargeIcon(circeAvatar)                               // User avatar here
                .setContentTitle(name)                              // User name or phone number here
                .setContentText(memo)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(memo))
                .setContentIntent(pIntentAction)                    // goto reminder details on click
                .addAction(R.drawable.ic_close_white_24dp, "Dismiss", pIntentDismiss)
                .setAutoCancel(true);
        nm.notify(recordId, notification.build());

    }


    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;

        if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
            float smallest = Math.min(bmp.getWidth(), bmp.getHeight());
            float factor = smallest / radius;
            sbmp = Bitmap.createScaledBitmap(bmp,
                    (int) (bmp.getWidth() / factor),
                    (int) (bmp.getHeight() / factor), false);
        } else {
            sbmp = bmp;
        }

        Bitmap output = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final String color = "#BAB399";
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, radius, radius);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor(color));
        canvas.drawCircle(radius / 2 + 0.7f, radius / 2 + 0.7f,
                radius / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }
}
