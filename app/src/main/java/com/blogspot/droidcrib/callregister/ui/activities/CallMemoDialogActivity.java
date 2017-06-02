package com.blogspot.droidcrib.callregister.ui.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
    private EditText mNote;
    private ImageView mDisplayCallType;
    private ImageView mDisplayAvatar;
    private Button mNoteButton;
    private Button mReminderButton;
    private Button mCancelButton;


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

        mDisplayName = (TextView) findViewById(R.id.id_dialog_person_name);
        mDisplayCallType = (ImageView) findViewById(R.id.id_dialog_call_type);
        mDisplayAvatar = (ImageView) findViewById(R.id.id_dialog_avatar);
        mNoteButton = (Button) findViewById(R.id.id_dialog_button_note);
        mReminderButton = (Button) findViewById(R.id.id_dialog_button_reminder);
        mCancelButton = (Button) findViewById(R.id.id_dialog_button_cancel);
        mNote = (EditText) findViewById(R.id.id_dialog_note);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Setup views
        mDisplayName.setText(mContactName);

        switch (mCallType) {
            case Constants.INCOMING_CALL:
                mDisplayCallType.setImageResource(R.drawable.ic_call_received_black_48dp);
                break;

            case Constants.OUTGOING_CALL:
                mDisplayCallType.setImageResource(R.drawable.ic_call_made_black_48dp);
                break;

            case Constants.MISSED_CALL:
                mDisplayCallType.setImageResource(R.drawable.ic_call_missed_black_48dp);
                break;
        }


        mNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: expand view with edittext
                mNote.setVisibility(View.VISIBLE);
            }
        });

        mReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: expand view with timer
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallMemoDialogActivity.this.finish();
            }
        });

        mNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO: save note to DB here
                CallRecord.updateMemo(mRecordId, s.toString());
                Log.d(TAG, "afterTextChanged: " + s.toString());
            }
        });
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
