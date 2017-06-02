package com.blogspot.droidcrib.callregister.ui.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.contract.Constants;
import com.blogspot.droidcrib.callregister.eventbus.NewCallEvent;
import com.blogspot.droidcrib.callregister.model.CallRecord;
import com.blogspot.droidcrib.callregister.telephony.ContactsProvider;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CallMemoDialogActivity extends AppCompatActivity {

    private String mPhoneNumber;
    private Date mCallDate = new Date();
    private String mContactName;
    private String mCallType;
    private long mRecordId;

    private TextView mDisplayName;
    private ImageView mDisplayCallType;
    private TextView mDisplayCallTime;
    private EditText mCallMemo;
    private Button mSkipButton;
    private Button mSaveButton;


    private static final String TAG = "CallMemoDialogActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_action);

        this.setFinishOnTouchOutside(false);

        // Get call data
        mPhoneNumber = getIntent().getStringExtra(Constants.EXTRA_PHONE_NUMBER);
        long dateMilliseconds = getIntent().getLongExtra(Constants.EXTRA_DATE, -1);
        mCallDate.setTime(dateMilliseconds);
        mCallType = getIntent().getStringExtra(Constants.EXTRA_CALL_TYPE);

        mContactName = readContactsWrapper(mPhoneNumber);

        // Insert new call record
        mRecordId = CallRecord.insert(mContactName, mPhoneNumber, mCallType, mCallDate);
        EventBus.getDefault().post(new NewCallEvent());

//        mDisplayName = (TextView) findViewById(R.id.id_text_view_dialog_person_name);
//        mDisplayCallType = (ImageView) findViewById(R.id.id_image_view_dialog_call_type);
//        mDisplayCallTime = (TextView) findViewById(R.id.id_text_view_dialog_call_time);
//        mCallMemo = (EditText) findViewById(R.id.id_edit_text_dialog_call_memo);
//        mSkipButton = (Button) findViewById(R.id.id_button_dialog_skip);
//        mSaveButton = (Button) findViewById(R.id.id_button_dialog_save);

    }


    @Override
    protected void onResume() {
        super.onResume();

//        // Setup views
//        mDisplayName.setText(mContactName);
//
//        switch (mCallType) {
//            case Constants.INCOMING_CALL:
//                mDisplayCallType.setImageResource(R.drawable.ic_call_received_black_48dp);
//                break;
//
//            case Constants.OUTGOING_CALL:
//                mDisplayCallType.setImageResource(R.drawable.ic_call_made_black_48dp);
//                break;
//
//            case Constants.MISSED_CALL:
//                mDisplayCallType.setImageResource(R.drawable.ic_call_missed_black_48dp);
//                break;
//        }
//
//        String callTime = new SimpleDateFormat("HH:mm").format(mCallDate);
//        mDisplayCallTime.setText(callTime);
//
//        mSkipButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CallMemoDialogActivity.this.finish();
//            }
//        });
//
//        mSaveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Get text from memo EditText field and save it to database record
//                String memo = mCallMemo.getText().toString();
//                CallRecord.updateMemo(mRecordId, memo);
//                CallMemoDialogActivity.this.finish();
//
//            }
//        });


    }

    private String readContactsWrapper(String phoneNumber) {
        int hasReadContactsPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);
        // Check permission
        if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
            // Show explanation about permission reason request if denied before
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                showMessageOKCancel(getResources().getString(R.string.access_contacts),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(CallMemoDialogActivity.this,
                                        new String[]{Manifest.permission.READ_CONTACTS},
                                        111);
                            }
                        });
                return phoneNumber;
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    111);
            return phoneNumber;
        }
        // PERMISSION_GRANTED. Do action here
        return ContactsProvider.getNameByPhoneNumber(this, mPhoneNumber);

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
