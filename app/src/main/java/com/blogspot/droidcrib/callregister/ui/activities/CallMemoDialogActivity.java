package com.blogspot.droidcrib.callregister.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.contract.Constants;
import com.blogspot.droidcrib.callregister.eventbus.NewCallEvent;
import com.blogspot.droidcrib.callregister.model.CallRecord;
import com.blogspot.droidcrib.callregister.model.ContactCard;
import com.blogspot.droidcrib.callregister.model.NoteRecord;
import com.blogspot.droidcrib.callregister.telephony.ContactsProvider;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import static com.blogspot.droidcrib.callregister.contract.Constants.EXTRA_CALL_RECORD_ID;
import static com.blogspot.droidcrib.callregister.contract.Constants.REQUEST_CODE_ASK_PERMISSIONS;

public class CallMemoDialogActivity extends AppCompatActivity {

    private String mPhoneNumber;
    private Date mCallDate = new Date();
    private long mRecordId;
    private EditText mNote;
    private boolean isNoAction = true;
    String mCallType;
    String mContactName;
    Bitmap mAvatarBitmap;
    String mAvatarUri;
    private String mNoteText;
    Button mNoteButton;
    Button mReminderButton;
    TextView mDisplayName;
    ImageView mDisplayCallType;
    SimpleDraweeView mDisplayAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_request_action);
        this.setFinishOnTouchOutside(false);

        mDisplayName = (TextView) findViewById(R.id.id_person_name);
        mDisplayCallType = (ImageView) findViewById(R.id.id_call_type);
        mDisplayAvatar = (SimpleDraweeView) findViewById(R.id.id_user_avatar);
        mNoteButton = (Button) findViewById(R.id.id_dialog_button_note);
        mReminderButton = (Button) findViewById(R.id.id_dialog_button_reminder);
        Button mCancelButton = (Button) findViewById(R.id.id_dialog_button_cancel);
        mNote = (EditText) findViewById(R.id.id_dialog_note);

        // Get call data
        mPhoneNumber = getIntent().getStringExtra(Constants.EXTRA_PHONE_NUMBER);
        long dateMilliseconds = getIntent().getLongExtra(Constants.EXTRA_DATE, -1);
        mCallDate.setTime(dateMilliseconds);
        mCallType = getIntent().getStringExtra(Constants.EXTRA_CALL_TYPE);
        readContactsWrapper(mPhoneNumber);

        mNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNoteButton.setVisibility(View.GONE);
                mReminderButton.setVisibility(View.GONE);
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
                mNoteText = s.toString();
            }
        });


        // Close activity if no mIntentAction performed during 4 seconds
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (isNoAction) {
                    CallMemoDialogActivity.this.finish();
                }
            }
        }, 6000);


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNoteText != null && mNoteText.length() > 0) {
            NoteRecord.insert(mNoteText, CallRecord.getRecordById(mRecordId));
        }
    }

    ////////////////////////////////////////////////////////
    // Handling permissions for API >= 23
    ////////////////////////////////////////////////////////

    private void readContactsWrapper(String phoneNumber) {
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
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        // PERMISSION_GRANTED. Do mIntentAction here
        setupView(ContactsProvider.getNameByPhoneNumber(this, phoneNumber));
        return;

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    setupView(ContactsProvider.getNameByPhoneNumber(this, mPhoneNumber));
                } else {
                    // Permission Denied
                    setupView(new ContactCard(mPhoneNumber));
                    Toast.makeText(CallMemoDialogActivity.this, "READ_CONTACTS Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setupView(ContactCard contactCard) {
        mContactName = contactCard.getName();
        mAvatarBitmap = contactCard.getAavatar();
        mAvatarUri = contactCard.getAvatarUri();

        // Insert new call record
        mRecordId = CallRecord.insert(mContactName, mPhoneNumber, mAvatarUri, mCallType, mCallDate);
        EventBus.getDefault().post(new NewCallEvent());


        // Setup views
        mDisplayName.setText(mContactName);
        if (mAvatarBitmap != null) {
            mDisplayAvatar.setImageURI(Uri.parse(mAvatarUri));
            mDisplayAvatar.setAlpha(1f);
        } else {
            mDisplayAvatar.setImageResource(R.drawable.ic_account_circle_black_48dp);
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
    }

}
