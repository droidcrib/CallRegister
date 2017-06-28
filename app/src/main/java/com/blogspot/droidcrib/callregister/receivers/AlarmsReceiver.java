package com.blogspot.droidcrib.callregister.receivers;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.blogspot.droidcrib.callregister.ui.activities.SingleFragmentActivity;

import java.io.IOException;

import static com.blogspot.droidcrib.callregister.contract.Constants.ACTION_REMOVE_NOTIFICATION;
import static com.blogspot.droidcrib.callregister.contract.Constants.ACTION_SHOW_ALARM_DETAILS;
import static com.blogspot.droidcrib.callregister.contract.Constants.ACTION_SHOW_ALARM_DETAILS_IN_LIST;
import static com.blogspot.droidcrib.callregister.contract.Constants.EXTRA_ALARM_RECORD_ID;
import static com.blogspot.droidcrib.callregister.contract.Constants.EXTRA_PHONE_NUMBER;
import static com.blogspot.droidcrib.callregister.contract.Constants.INTENT_TXT;

public class AlarmsReceiver extends BroadcastReceiver {

    private String mName;
    private String mMemo;
    private Bitmap mAvatar;
    private Bitmap circeAvatar;
    private Intent intentAction;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        long alarmRecordId = intent.getLongExtra(EXTRA_ALARM_RECORD_ID, -1);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        AlarmRecord alarmRecord = AlarmRecord.getRecordById(alarmRecordId);
        mMemo = alarmRecord.memoText;

        if (alarmRecord.callRecord != null) {
            mName = alarmRecord.callRecord.name;
            Drawable d = context.getResources().getDrawable(R.drawable.ic_alarm_notif);
            Drawable currentState = d.getCurrent();
            if (currentState instanceof BitmapDrawable)
                mAvatar = ((BitmapDrawable) currentState).getBitmap();
            if (alarmRecord.callRecord.avatarUri != null) {
                Uri imageUri = Uri.parse(alarmRecord.callRecord.avatarUri);
                try {
                    mAvatar = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            circeAvatar = getCircledBitmap(mAvatar, 96);
        }

        if (alarmRecord.callRecord != null) {
            intentAction = new Intent(context, SingleFragmentActivity.class);
            intentAction.setAction(ACTION_SHOW_ALARM_DETAILS);
        } else {
            intentAction = new Intent(context, MainActivity.class);
            intentAction.setAction(ACTION_SHOW_ALARM_DETAILS_IN_LIST);
        }

        intentAction.putExtra(EXTRA_ALARM_RECORD_ID, alarmRecordId);
        PendingIntent pIntentAction = PendingIntent.getActivity(context, (int) alarmRecordId, intentAction,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setPriority(Notification.PRIORITY_MAX)
                .setWhen(0)
                .setSmallIcon(R.drawable.ic_watch_later_white_24dp)
                .setLargeIcon(circeAvatar)
                .setContentTitle(mName)
                .setContentText(mMemo)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mMemo))
                .setVibrate(new long[]{500, 500, 500, 500, 500})
                .setLights(Color.GREEN, 3000, 3000)
                .setContentIntent(pIntentAction)
                .setAutoCancel(true);
        nm.notify((int) alarmRecordId, notification.build());
    }


    public static Bitmap getCircledBitmap(Bitmap bmp, int radius) {
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
