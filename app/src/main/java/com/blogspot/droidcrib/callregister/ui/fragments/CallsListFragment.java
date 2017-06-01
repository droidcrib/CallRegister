package com.blogspot.droidcrib.callregister.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.contract.Constants;
import com.blogspot.droidcrib.callregister.eventbus.NewCallEvent;
import com.blogspot.droidcrib.callregister.loaders.CallRecordsLoader;
import com.blogspot.droidcrib.callregister.model.CallRecord;
import com.blogspot.droidcrib.callregister.ui.activities.MainActivity;
import com.blogspot.droidcrib.callregister.ui.activities.SingleFragmentActivity;
import com.blogspot.droidcrib.callregister.ui.adapters.CallsListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Andrey Bulanov on 04.10.2016.
 */
public class CallsListFragment extends Fragment implements LoaderManager.LoaderCallbacks{

    public static CallsListFragment sCallsListFragment;
    List<CallRecord> mCallRecordsList;
    StickyListHeadersListView stickyList;
    private String mToolbarTextHeader;

    //
    // Provides instance of CallsListFragment
    //
    public static CallsListFragment getInstance() {

        if (sCallsListFragment == null) {
            sCallsListFragment = new CallsListFragment();
        }
        return sCallsListFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //insertDummyCallRecords();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_sticky_list, container, false);
        stickyList = (StickyListHeadersListView) v.findViewById(R.id.list_sticky);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        getLoaderManager().initLoader(0, null, this);

        // List items click processing
        stickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity activity = (MainActivity) getActivity();
                activity.setDetailsFragment(id);
            }
        });
        // Set text to Toolbar header
        MainActivity activity = (MainActivity) getActivity();
        mToolbarTextHeader = activity.getResources().getString(R.string.app_name);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }



    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CallRecordsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        mCallRecordsList = (List<CallRecord>) data;
        CallsListAdapter adapter = new CallsListAdapter(getActivity(), mCallRecordsList);
        stickyList.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        stickyList.setAdapter(null);
    }

    @Subscribe
    public void onEvent(NewCallEvent event){
        Log.d("onEvent", "Event received. Restarting loader");
        getLoaderManager().restartLoader(0, null, this);
    }

    private void insertDummyCallRecords() {

        Date today = new Date(System.currentTimeMillis());

        CallRecord.insert("Linda Wilson", "111 22 33", Constants.INCOMING_CALL, new Date(today.getTime() - (1 * 24 * 60 * 60 * 1000)));
        CallRecord.insert("John Doe", "555 34 34 ", Constants.INCOMING_CALL, new Date(today.getTime() - (2 * 24 * 60 * 60 * 1000)));
        CallRecord.insert("Barak Obama", "555 55 55", Constants.INCOMING_CALL, new Date(today.getTime() - (3 * 24 * 60 * 60 * 1000)));

        CallRecord.insert("Linda Wilson", "111 22 33", Constants.OUTGOING_CALL, new Date(today.getTime() - (1 * 24 * 60 * 60 * 1000)));
        CallRecord.insert("John Doe", "555 34 34 ", Constants.OUTGOING_CALL, new Date(today.getTime() - (2 * 24 * 60 * 60 * 1000)));
        CallRecord.insert("Barak Obama", "555 55 55", Constants.OUTGOING_CALL, new Date(today.getTime() - (3 * 24 * 60 * 60 * 1000)));

        CallRecord.insert("Linda Wilson", "111 22 33", Constants.MISSED_CALL, new Date(today.getTime() - (1 * 24 * 60 * 60 * 1000)));
        CallRecord.insert("John Doe", "555 34 34 ", Constants.MISSED_CALL, new Date(today.getTime() - (2 * 24 * 60 * 60 * 1000)));
        CallRecord.insert("Barak Obama", "555 55 55", Constants.MISSED_CALL, new Date(today.getTime() - (3 * 24 * 60 * 60 * 1000)));
    }


}
