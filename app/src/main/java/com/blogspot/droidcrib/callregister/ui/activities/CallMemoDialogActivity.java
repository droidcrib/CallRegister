package com.blogspot.droidcrib.callregister.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.contract.Constants;
import com.blogspot.droidcrib.callregister.eventbus.NewCallEvent;
import com.blogspot.droidcrib.callregister.model.CallRecord;
import com.blogspot.droidcrib.callregister.model.ContactCard;
import com.blogspot.droidcrib.callregister.telephony.ContactsProvider;
import com.blogspot.droidcrib.callregister.ui.adapters.MeasuredViewPager;
import com.blogspot.droidcrib.callregister.ui.adapters.TabsPagerAdapter;

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
    private Bitmap mAvatarBitmap;
    private String mAvatarUri;
    private MeasuredViewPager mViewPager;
    private LinearLayout mRootLinear;
    private LinearLayout mButtonsHolder;
    private RelativeLayout mPickerMainLayout;


    private static final String TAG = "CallMemoDialogActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        boolean flag = getIntent().getBooleanExtra("qaz", false);
        if (flag){
           setTheme(R.style.AppTheme);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_action);

        this.setFinishOnTouchOutside(false);


        //
        //  Setup TabLayout
        //
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Date"));
        tabLayout.addTab(tabLayout.newTab().setText("Time"));
        tabLayout.addTab(tabLayout.newTab().setText("Memo"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //
        //  Setup ViewPager
        //
        mViewPager = (MeasuredViewPager) findViewById(R.id.pager);
        final TabsPagerAdapter adapter = new TabsPagerAdapter(this, tabLayout.getTabCount());
        mViewPager.setAdapter(adapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout) {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    // do something with content
                }
                if (position == 1) {
                    // do something with content
                }
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        // Get call data
        mPhoneNumber = getIntent().getStringExtra(Constants.EXTRA_PHONE_NUMBER);
        long dateMilliseconds = getIntent().getLongExtra(Constants.EXTRA_DATE, -1);
        mCallDate.setTime(dateMilliseconds);
        mCallType = getIntent().getStringExtra(Constants.EXTRA_CALL_TYPE);
        ContactCard contactCard = readContactsWrapper(mPhoneNumber);
        mContactName = contactCard.getName();
        mAvatarBitmap = contactCard.getAavatar();
        mAvatarUri = contactCard.getAvatarUri();

        // Insert new call record,
        mRecordId = CallRecord.insert(mContactName, mPhoneNumber, mAvatarUri, mCallType, mCallDate);
        EventBus.getDefault().post(new NewCallEvent());

        mDisplayName = (TextView) findViewById(R.id.id_dialog_person_name);
        mDisplayCallType = (ImageView) findViewById(R.id.id_dialog_call_type);
        mDisplayAvatar = (ImageView) findViewById(R.id.id_dialog_avatar);
        mNoteButton = (Button) findViewById(R.id.id_dialog_button_note);
        mReminderButton = (Button) findViewById(R.id.id_dialog_button_reminder);
        mCancelButton = (Button) findViewById(R.id.id_dialog_button_cancel);
        mNote = (EditText) findViewById(R.id.id_dialog_note);
        mRootLinear = (LinearLayout) findViewById(R.id.root_linear);
        mButtonsHolder = (LinearLayout) findViewById(R.id.buttons_holder);
        mPickerMainLayout = (RelativeLayout) findViewById(R.id.picker_main_layout);

        if (flag){
            mPickerMainLayout.setVisibility(View.VISIBLE);
        }

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
                mNote.setVisibility(View.VISIBLE);
                mPickerMainLayout.setVisibility(View.GONE);
            }
        });

        mReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: start another activity with almost same layout to handle big datepicker
                // TODO: replace buttons at the bottom with FAB "Done"
                long dateMilliseconds = mCallDate.getTime();
                Intent intent = new Intent(CallMemoDialogActivity.this, CallMemoDialogActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.EXTRA_PHONE_NUMBER, mPhoneNumber);
                intent.putExtra(Constants.EXTRA_DATE, dateMilliseconds);
                intent.putExtra(Constants.EXTRA_CALL_TYPE, mCallType);
                intent.putExtra("qaz", true);
                CallMemoDialogActivity.this.startActivity(intent);
                finish();

//                mPickerMainLayout.setVisibility(View.VISIBLE);
//                mNote.setVisibility(View.GONE);
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
                Log.d(TAG, "afterTextChanged: " + s.toString());
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();


    }

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
