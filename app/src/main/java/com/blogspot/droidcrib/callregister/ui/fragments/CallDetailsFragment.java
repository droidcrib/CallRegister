package com.blogspot.droidcrib.callregister.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.contract.Constants;
import com.blogspot.droidcrib.callregister.model.CallRecord;
import com.blogspot.droidcrib.callregister.ui.activities.MainActivity;
import com.blogspot.droidcrib.callregister.ui.activities.SingleFragmentActivity;

import java.text.SimpleDateFormat;

/**
 * Created by Andrey on 04.10.2016.
 */
public class CallDetailsFragment extends Fragment {
    private Long mRecordId;
    private CallRecord mCallRecord;
    private String mToolbarTextHeader;

    private TextView mDisplayName;
    private ImageView mDisplayCallType;
    private TextView mDisplayCallTime;
    private TextView mDisplayCallMemo;
    private ImageView mDisplayAvatar;


    // Fragment instance
    public static CallDetailsFragment newInstance(long id) {
        Bundle args = new Bundle();
        args.putLong(Constants.EXTRA_RECORD_ID, id);
        CallDetailsFragment fragment = new CallDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecordId = getArguments().getLong(Constants.EXTRA_RECORD_ID);
        mCallRecord = CallRecord.getRecordById(mRecordId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_call_details, container, false);

        mDisplayName = (TextView) v.findViewById(R.id.id_text_view_details_person_name);
        mDisplayCallType = (ImageView) v.findViewById(R.id.id_image_view_details_call_type);
        mDisplayCallTime = (TextView) v.findViewById(R.id.id_text_view_details_call_time);
        mDisplayCallMemo = (TextView) v.findViewById(R.id.id_text_view_details_memo);

        mDisplayName.setText(mCallRecord.name);
        mDisplayCallMemo.setText(mCallRecord.memoText);
        mDisplayAvatar.setImageURI(Uri.parse(mCallRecord.avatarUri));

        String convertedTime = new SimpleDateFormat("HH:mm").format(mCallRecord.callStartTime);
        mDisplayCallTime.setText(convertedTime);

        //setup call type text
        switch (mCallRecord.callType) {
            case Constants.INCOMING_CALL:
                mDisplayCallType.setImageResource(R.drawable.ic_call_received_black_48dp);
                mToolbarTextHeader = getActivity().getResources().getString(R.string.incoming);
                break;
            case Constants.OUTGOING_CALL:
                mDisplayCallType.setImageResource(R.drawable.ic_call_made_black_48dp);
                mToolbarTextHeader = getActivity().getResources().getString(R.string.outgoing);
                break;
            case Constants.MISSED_CALL:
                mDisplayCallType.setImageResource(R.drawable.ic_call_missed_black_48dp);
                mToolbarTextHeader = getActivity().getResources().getString(R.string.missed);
                break;
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
//        MainActivity activity = (MainActivity) getActivity();
//        activity.setToolbarTextHeader(mToolbarTextHeader);
    }


}
