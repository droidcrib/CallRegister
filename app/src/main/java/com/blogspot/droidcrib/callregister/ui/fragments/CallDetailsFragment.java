package com.blogspot.droidcrib.callregister.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.contract.Constants;
import com.blogspot.droidcrib.callregister.model.AlarmRecord;
import com.blogspot.droidcrib.callregister.model.CallRecord;
import com.blogspot.droidcrib.callregister.model.NoteRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Andrey on 04.10.2016.
 */
public class CallDetailsFragment extends Fragment {
    private Long mRecordId;
    private CallRecord mCallRecord;
    private LinearLayout mNoteLayout;
    private RelativeLayout mAlarmLayout;
    private ArrayList<NoteRecord> mNoteRecordsList;
    private ArrayList<AlarmRecord> mAlarmRecordsList;
    private ImageView mDisplayCallType;
    private TextView mDisplayCallTime;
    private TextView mDisplayCallMemo;
    private ImageView mDisplayAvatar;
    private TextView mDisplayAlarmTime;
    private TextView mDisplayAlarmMemo;
    private FloatingActionButton mFab;

    // Fragment instance
    public static CallDetailsFragment newInstance(long id) {
        Bundle args = new Bundle();
        args.putLong(Constants.EXTRA_CALL_RECORD_ID, id);
        CallDetailsFragment fragment = new CallDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecordId = getArguments().getLong(Constants.EXTRA_CALL_RECORD_ID);
        mCallRecord = CallRecord.getRecordById(mRecordId);
        if (mCallRecord != null) {
            mNoteRecordsList = new ArrayList<>(mCallRecord.getNotes());
            mAlarmRecordsList = new ArrayList<>(mCallRecord.getAlarms());
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_call_notes, container, false);

        mFab = (FloatingActionButton) v.findViewById(R.id.fab_call) ;
        mDisplayCallType = (ImageView) v.findViewById(R.id.id_image_view_details_call_type);
        mDisplayCallTime = (TextView) v.findViewById(R.id.id_text_view_details_call_time);
        mDisplayCallMemo = (TextView) v.findViewById(R.id.id_detail_note);
        mDisplayAvatar = (ImageView) v.findViewById(R.id.avatar_backdrop);
        mDisplayAlarmTime = (TextView) v.findViewById(R.id.id_detail_time);
        mDisplayAlarmMemo = (TextView) v.findViewById(R.id.id_detail_text);
        mNoteLayout = (LinearLayout) v.findViewById(R.id.in_note);
        mAlarmLayout = (RelativeLayout) v.findViewById(R.id.in_alarm);
        mNoteLayout.setVisibility(View.GONE);
        mAlarmLayout.setVisibility(View.GONE);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(mCallRecord.name);


        if (mNoteRecordsList != null && mNoteRecordsList.size() > 0) {
            mNoteLayout.setVisibility(View.VISIBLE);
            mDisplayCallMemo.setText(mNoteRecordsList.get(0).memoText);
        }

        if (mAlarmRecordsList != null && mAlarmRecordsList.size() > 0) {
            mAlarmLayout.setVisibility(View.VISIBLE);
            String convertedTime = new SimpleDateFormat("dd MMM yyyy  HH:mm").format(mAlarmRecordsList.get(0).alarmDateInMillis);
            String memo = mAlarmRecordsList.get(0).memoText;
            mDisplayAlarmTime.setText(convertedTime);
            mDisplayAlarmMemo.setText(memo);
        }

        if (mCallRecord.avatarUri != null) {
            mDisplayAvatar.setImageURI(Uri.parse(mCallRecord.avatarUri));
            mDisplayAvatar.setAlpha(1f);
        } else {
            mDisplayAvatar.setImageResource(R.drawable.ic_account_circle_black_48dp);
        }


        String convertedTime = new SimpleDateFormat("HH:mm").format(mCallRecord.callStartTime);
        mDisplayCallTime.setText(convertedTime);

        //setup call type
        switch (mCallRecord.callType) {
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
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCallRecord.phone));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
