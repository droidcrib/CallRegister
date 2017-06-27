package com.blogspot.droidcrib.callregister.telephony;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.blogspot.droidcrib.callregister.contract.Constants;
import com.blogspot.droidcrib.callregister.ui.activities.CallMemoDialogActivity;

import java.util.Date;

import static com.blogspot.droidcrib.callregister.contract.Constants.IS_CATCH_INCOMINGS;
import static com.blogspot.droidcrib.callregister.contract.Constants.IS_CATCH_MISSED;
import static com.blogspot.droidcrib.callregister.contract.Constants.IS_CATCH_OUTGOINGS;
import static com.blogspot.droidcrib.callregister.contract.Constants.SHARED_PREFS;

public class CallReceiver extends PhonecallReceiver {

    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {

        Boolean isCatchCall = ctx.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getBoolean(IS_CATCH_INCOMINGS, true);
        if (isCatchCall) {
            requestCallMemoDialog(ctx, number, start, Constants.INCOMING_CALL);
        }
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {

        Boolean isCatchCall = ctx.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getBoolean(IS_CATCH_OUTGOINGS, true);
        if (isCatchCall) {
            requestCallMemoDialog(ctx, number, start, Constants.OUTGOING_CALL);
        }
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {

        Boolean isCatchCall = ctx.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getBoolean(IS_CATCH_MISSED, true);
        if (isCatchCall) {
            requestCallMemoDialog(ctx, number, start, Constants.MISSED_CALL);
        }
    }

    private void requestCallMemoDialog(Context context, String phoneNumber, Date callDate, String callType) {

        long dateMilliseconds = callDate.getTime();
        Intent intent = new Intent(context, CallMemoDialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.EXTRA_PHONE_NUMBER, phoneNumber);
        intent.putExtra(Constants.EXTRA_DATE, dateMilliseconds);
        intent.putExtra(Constants.EXTRA_CALL_TYPE, callType);
        context.startActivity(intent);
    }
}