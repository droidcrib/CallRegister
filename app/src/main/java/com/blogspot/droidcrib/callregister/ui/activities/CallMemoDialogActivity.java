package com.blogspot.droidcrib.callregister.ui.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.contract.Constants;
import com.blogspot.droidcrib.callregister.eventbus.NewCallEvent;
import com.blogspot.droidcrib.callregister.model.CallRecord;
import com.blogspot.droidcrib.callregister.model.ContactCard;
import com.blogspot.droidcrib.callregister.telephony.ContactsProvider;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import static com.blogspot.droidcrib.callregister.contract.Constants.EXTRA_CALL_RECORD_ID;

public class CallMemoDialogActivity extends AppCompatActivity {

    private static final String TAG = "CallMemoDialogActivity";

    private String mPhoneNumber;
    private Date mCallDate = new Date();
    private long mRecordId;
    private EditText mNote;
    private boolean isNoAction = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_action);
        this.setFinishOnTouchOutside(false);

        TextView mDisplayName = (TextView) findViewById(R.id.id_person_name);
        ImageView mDisplayCallType = (ImageView) findViewById(R.id.id_call_type);
        ImageView mDisplayAvatar = (ImageView) findViewById(R.id.id_avatar);
        Button mNoteButton = (Button) findViewById(R.id.id_dialog_button_note);
        Button mReminderButton = (Button) findViewById(R.id.id_dialog_button_reminder);
        Button mCancelButton = (Button) findViewById(R.id.id_dialog_button_cancel);
        mNote = (EditText) findViewById(R.id.id_dialog_note);

        // Get call data
        mPhoneNumber = getIntent().getStringExtra(Constants.EXTRA_PHONE_NUMBER);
        long dateMilliseconds = getIntent().getLongExtra(Constants.EXTRA_DATE, -1);
        mCallDate.setTime(dateMilliseconds);
        String mCallType = getIntent().getStringExtra(Constants.EXTRA_CALL_TYPE);
        ContactCard contactCard = readContactsWrapper(mPhoneNumber);
        String mContactName = contactCard.getName();
        Bitmap mAvatarBitmap = contactCard.getAavatar();
        String mAvatarUri = contactCard.getAvatarUri();

        // Insert new call record
        mRecordId = CallRecord.insert(mContactName, mPhoneNumber, mAvatarUri, mCallType, mCallDate);
        EventBus.getDefault().post(new NewCallEvent());

        // Setup views
        mDisplayName.setText(mContactName);
        if (mAvatarBitmap != null) {
            mDisplayAvatar.setImageBitmap(mAvatarBitmap);
        } else {
            mDisplayAvatar.setImageResource(R.drawable.ic_person_black_48dp);
        }
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
                isNoAction = false;
                mNote.setVisibility(View.VISIBLE);
            }
        });

        mReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CallMemoDialogActivity.this, NewReminderActivity.class);
                intent.putExtra(EXTRA_CALL_RECORD_ID, mRecordId);
                CallMemoDialogActivity.this.startActivity(intent);
                CallMemoDialogActivity.this.finish();
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
                CallRecord.updateMemo(mRecordId, s.toString());
            }
        });


        // Close activity if no action performed during 4 seconds
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if(isNoAction) {
                    CallMemoDialogActivity.this.finish();
                }
            }
        }, 4000);


    }


    ////////////////////////////////////////////////////////
    // Handling permissions for API >= 23
    ////////////////////////////////////////////////////////

    private ContactCard readContactsWrapper(String phoneNumber) {
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
                return new ContactCard(phoneNumber);
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    111);
            return new ContactCard(phoneNumber);
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
