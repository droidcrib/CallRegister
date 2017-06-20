package com.blogspot.droidcrib.callregister.ui.activities;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.model.AlarmRecord;
import com.blogspot.droidcrib.callregister.telephony.ContactsProvider;
import com.blogspot.droidcrib.callregister.ui.fragments.CallDetailsFragment;
import com.blogspot.droidcrib.callregister.ui.fragments.CallsListFragment;

import static com.blogspot.droidcrib.callregister.contract.Constants.ACTION_SHOW_ALARM_DETAILS;
import static com.blogspot.droidcrib.callregister.contract.Constants.EXTRA_ALARM_RECORD_ID;
import static com.blogspot.droidcrib.callregister.contract.Constants.EXTRA_CALL_RECORD_ID;

/**
 *
 */
public class SingleFragmentActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;
    private Fragment mFragment;
    private long mRecordId;
    private long mAlarmRecordId;

    private static final String TAG = "SingleFragmentActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_single_fragment);
        mRecordId = getIntent().getLongExtra(EXTRA_CALL_RECORD_ID, 0);
        mAlarmRecordId = getIntent().getLongExtra(EXTRA_ALARM_RECORD_ID, -1);
        mFragmentManager = getSupportFragmentManager();
        mFragment = mFragmentManager.findFragmentById(R.id.id_fragment_container);

        // Explicitly call to get permission in Android 6
        readPhoneStateWrapper();

        String action = getIntent().getAction();

        Log.d(TAG, "mRecordId = " + mRecordId);
        Log.d(TAG, "mAlarmRecordId = " + mAlarmRecordId);
        Log.d(TAG, "action = " + action);

        if (action != null && action.equals(ACTION_SHOW_ALARM_DETAILS)) {
            AlarmRecord record = AlarmRecord.getRecordById(mAlarmRecordId);
            Log.d(TAG, "record.callRecord.getId() = " + record.callRecord.getId());
            setDetailsFragment(record.callRecord.getId());
        } else {
            setDetailsFragment(mRecordId);
        }


//
//
//        if (mFragment == null) {
//            mFragment = CallDetailsFragment.newInstance(mRecordId);
//            mFragmentManager.beginTransaction()
//                    .add(R.id.id_fragment_container, mFragment)
//                    .commit();
//        }

        // Toolbar setup
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

    }



    public void setDetailsFragment(long id) {
        if (mFragment == null) {
            mFragment = CallDetailsFragment.newInstance(id);
            mFragmentManager.beginTransaction()
                    .replace(R.id.id_fragment_container, mFragment)
                    .commit();
        }
    }














    private void readPhoneStateWrapper() {
        int hasReadContactsPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        // Check permission
        if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
            // Show explanation about permission reason request if denied before
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
                showMessageOKCancel(getResources().getString(R.string.access_phone_state),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(SingleFragmentActivity.this,
                                        new String[]{Manifest.permission.READ_PHONE_STATE},
                                        111);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    111);
            return;
        }
        // PERMISSION_GRANTED. Do action here
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int state = telephonyManager.getCallState();
        Log.d("Tel_EXTRA_STATE", "Call state:" + state);

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.ok), okListener)
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .create()
                .show();
    }



}
