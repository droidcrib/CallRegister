package com.blogspot.droidcrib.callregister.telephony;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.blogspot.droidcrib.callregister.contract.Constants;
import com.blogspot.droidcrib.callregister.eventbus.NewCallEvent;
import com.blogspot.droidcrib.callregister.ui.dialogs.CallMemoDialogActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import static com.blogspot.droidcrib.callregister.contract.Constants.IS_CATCH_INCOMINGS;
import static com.blogspot.droidcrib.callregister.contract.Constants.IS_CATCH_MISSED;
import static com.blogspot.droidcrib.callregister.contract.Constants.IS_CATCH_OUTGOINGS;
import static com.blogspot.droidcrib.callregister.contract.Constants.SHARED_PREFS;

/**
 *
 */
public class CallReceiver extends PhonecallReceiver {



    private static final String TAG = "CallReceiver";




    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
        //
        Log.d(TAG, "Incoming call received " + number);
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
        //
        Log.d(TAG, "Incoming call answered " + number);
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        //

        Boolean isCatchCall = ctx.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getBoolean(IS_CATCH_INCOMINGS,true);
        Log.d(TAG, "Incoming call ended " + number + " " + isCatchCall);
        if(isCatchCall) {
            Log.d(TAG, "catch Call Incoming " + number);
            requestCallMemoDialog(ctx, number, start, Constants.INCOMING_CALL);
        }
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        //
        Log.d(TAG, "Outgoing call started " + number);
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        //

        Boolean isCatchCall = ctx.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getBoolean(IS_CATCH_OUTGOINGS,true);
        Log.d(TAG, "Outgoing call ended " + number + " " + isCatchCall);
        if(isCatchCall) {
            Log.d(TAG, "catch Call Outgoing " + number);
            requestCallMemoDialog(ctx, number, start, Constants.OUTGOING_CALL);
        }
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        //

        Boolean isCatchCall = ctx.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getBoolean(IS_CATCH_MISSED,true);
        Log.d(TAG, "Call missed " + number + " " + isCatchCall);
        if(isCatchCall) {
            Log.d(TAG, "catch Call missed " + number);
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