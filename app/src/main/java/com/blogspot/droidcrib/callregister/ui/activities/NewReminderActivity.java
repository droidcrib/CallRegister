package com.blogspot.droidcrib.callregister.ui.activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.contract.Constants;
import com.blogspot.droidcrib.callregister.eventbus.NewCallEvent;
import com.blogspot.droidcrib.callregister.eventbus.PickerDateChangedEvent;
import com.blogspot.droidcrib.callregister.eventbus.PickerTextChangedEvent;
import com.blogspot.droidcrib.callregister.eventbus.PickerTimeCangedEvent;
import com.blogspot.droidcrib.callregister.model.CallRecord;
import com.blogspot.droidcrib.callregister.ui.adapters.MeasuredViewPager;
import com.blogspot.droidcrib.callregister.ui.adapters.TabsPagerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;

import static com.blogspot.droidcrib.callregister.contract.Constants.EXTRA_RECORD_ID;

/**
 *
 */

public class NewReminderActivity extends AppCompatActivity {

    private MeasuredViewPager mViewPager;
    private long mRecordId;
    private TextView mDisplayName;
    private EditText mNote;
    private ImageView mDisplayCallType;
    private ImageView mDisplayAvatar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);

        EventBus.getDefault().register(this);

        mRecordId = getIntent().getLongExtra(EXTRA_RECORD_ID, 0);
        CallRecord callRecord = CallRecord.getRecordById(mRecordId);

        mDisplayName = (TextView) findViewById(R.id.id_person_name);
        mDisplayCallType = (ImageView) findViewById(R.id.id_call_type);
        mDisplayAvatar = (ImageView) findViewById(R.id.id_avatar);
        mNote = (EditText) findViewById(R.id.id_dialog_note);


        // Name
        mDisplayName.setText(callRecord.name);
        // Avatar
        if (callRecord.avatarUri != null) {
            mDisplayAvatar.setImageURI(Uri.parse(callRecord.avatarUri));
        } else {
            mDisplayAvatar.setImageResource(R.drawable.ic_person_black_48dp);
        }
        // Call type
        switch (callRecord.callType) {
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


        //  Setup TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Date"));
        tabLayout.addTab(tabLayout.newTab().setText("Time"));
        tabLayout.addTab(tabLayout.newTab().setText("Memo"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //  Setup ViewPager
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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    /////////////////////////////////////////////////////////////
    // EVENT LISTENERS
    /////////////////////////////////////////////////////////////

    @Subscribe
    public void onEvent(PickerDateChangedEvent event){
        Log.d("onEvent", "PickerDateChangedEvent " + event.getYear() + " " + event.getMonth() + " " + event.getDayOfMonth());
    }

    @Subscribe
    public void onEvent(PickerTimeCangedEvent event){
        Log.d("onEvent", "PickerTimeCangedEvent " + event.getHourOfDay() + " " + event.getMinute());
    }

    @Subscribe
    public void onEvent(PickerTextChangedEvent event){
        Log.d("onEvent", "PickerTextChangedEvent " + event.getText());

    }
}
