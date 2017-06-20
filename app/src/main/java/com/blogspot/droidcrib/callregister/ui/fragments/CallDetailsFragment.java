package com.blogspot.droidcrib.callregister.ui.fragments;

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
    private NoteRecord mNoteRecord;
    private AlarmRecord mAlarmRecord;
    private String mToolbarTextHeader;
    private  String mMemoText;

    ArrayList<NoteRecord> mNoteRecordsList;
    ArrayList<AlarmRecord> mAlarmRecordsList;


    private ImageView mDisplayCallType;
    private TextView mDisplayCallTime;
    private TextView mDisplayCallMemo;
    private ImageView mDisplayAvatar;
    private TextView mDisplayAlarmTime;
    private TextView mDisplayAlarmMemo;
    private FloatingActionButton mFab;

    private static final String TAG = "CallDetailsFragment";


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
        mNoteRecordsList =  new ArrayList<>(mCallRecord.getNotes());
        mAlarmRecordsList =  new ArrayList<>(mCallRecord.getAlarms());

        Log.d(TAG, "Related notes: " + mCallRecord.getNotes().size());
        Log.d(TAG, "Related alarms: " + mCallRecord.getAlarms().size());



    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_call_notes, container, false);

        mDisplayCallType = (ImageView) v.findViewById(R.id.id_image_view_details_call_type);
        mDisplayCallTime = (TextView) v.findViewById(R.id.id_text_view_details_call_time);
        mDisplayCallMemo = (TextView) v.findViewById(R.id.id_detail_note);
        mDisplayAvatar = (ImageView) v.findViewById(R.id.avatar_backdrop);
        mDisplayAlarmTime = (TextView) v.findViewById(R.id.id_detail_time);
        mDisplayAlarmMemo = (TextView) v.findViewById(R.id.id_detail_text);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(mCallRecord.name);



        if (mNoteRecordsList.size() > 0){
            mDisplayCallMemo.setText(mNoteRecordsList.get(0).memoText);
        }

        if (mAlarmRecordsList.size() > 0){
            String date = String.valueOf(mAlarmRecordsList.get(0).year);
            String memo = mAlarmRecordsList.get(0).memoText;

            mDisplayAlarmTime.setText(date);
            mDisplayAlarmMemo.setText(memo);
//            mDisplayAlarmTime.setText("DATE");
//            mDisplayAlarmMemo.setText("MEMO");
        }

        Log.d(TAG, "mCallRecord.avatarUri: " + mCallRecord.avatarUri);
        if (mCallRecord.avatarUri != null) {
            mDisplayAvatar.setImageURI(Uri.parse(mCallRecord.avatarUri));
        } else {
            mDisplayAvatar.setImageResource(R.drawable.ic_person_black_48dp);
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
    public void onResume() {
        super.onResume();

    }


}
