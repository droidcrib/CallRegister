package com.blogspot.droidcrib.callregister.ui.fragments;

import android.graphics.Color;
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
import com.blogspot.droidcrib.callregister.eventbus.AlarmsListLoadFinishedEvent;
import com.blogspot.droidcrib.callregister.eventbus.NewCallEvent;
import com.blogspot.droidcrib.callregister.loaders.AlarmRecordsLoader;
import com.blogspot.droidcrib.callregister.model.AlarmRecord;
import com.blogspot.droidcrib.callregister.ui.activities.MainActivity;
import com.blogspot.droidcrib.callregister.ui.adapters.AlarmsListAdapter;
import com.blogspot.droidcrib.callregister.ui.adapters.NotesListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by BulanovA on 21.06.2017.
 */

public class AlarmsListFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    public static AlarmsListFragment sAlarmsListFragment;
    List<AlarmRecord> mAlarmRecordsList;
    StickyListHeadersListView stickyList;
    private String mToolbarTextHeader;
    private static final String TAG = "MainActivity";

    //
    // Provides instance of AlarmsListFragment
    //
    public static AlarmsListFragment getInstance() {

        if (sAlarmsListFragment == null) {
            sAlarmsListFragment = new AlarmsListFragment();
        }
        return sAlarmsListFragment;
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

        getLoaderManager().restartLoader(0, null, this);

        // List items click processing
        stickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlarmsListAdapter.ViewHolder holder = (AlarmsListAdapter.ViewHolder) (view.getTag());
                holder.memoShort.setVisibility(holder.memoShort.isShown() ? View.GONE : View.VISIBLE);
                holder.memo.setVisibility(holder.memo.isShown() ? View.GONE : View.VISIBLE);
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


    public void scrollToListItem(long id) {
        for (int i = 0; i < stickyList.getCount(); i++) {
            Log.d(TAG, "stickyList.pos = " + i + " stickyList.pos.id = " + stickyList.getItemIdAtPosition(i));
            if (stickyList.getItemIdAtPosition(i) == id) {
                stickyList.setSelection(i);
                return;
            }
        }
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new AlarmRecordsLoader(getActivity());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onLoadFinished(Loader loader, Object data) {
        mAlarmRecordsList = (List<AlarmRecord>) data;
        AlarmsListAdapter adapter = new AlarmsListAdapter(getActivity(), mAlarmRecordsList);
        stickyList.setAdapter(adapter);

        EventBus.getDefault().post(new AlarmsListLoadFinishedEvent());

//        scrollToListItem(8);

//        final View view = stickyList.getAdapter().getView(0, null, stickyList);
//        view.setBackgroundColor(Color.BLUE);

//        AlarmsListAdapter.ViewHolder holder = (AlarmsListAdapter.ViewHolder) (view.getTag());
//        holder.memoShort.setVisibility(holder.memoShort.isShown() ? View.GONE : View.VISIBLE);
//        holder.memo.setVisibility(holder.memo.isShown() ? View.GONE : View.VISIBLE);


//        stickyList.setSelection(8);
//        View view = stickyList.getChildAt(0);
//        AlarmsListAdapter.ViewHolder holder = (AlarmsListAdapter.ViewHolder) (view.getTag());
//        holder.memoShort.setVisibility(holder.memoShort.isShown() ? View.GONE : View.VISIBLE);
//        holder.memo.setVisibility(holder.memo.isShown() ? View.GONE : View.VISIBLE);

//        for(int i=0; i<stickyList.getCount(); i++){
//            Log.d(TAG, "stickyList.pos = " + i + " stickyList.pos.id = " + stickyList.getItemIdAtPosition(i));
//        }
//
//        stickyList.setSelection(2);
//        Log.d(TAG, "stickyList.getCount() = " + stickyList.getCount());
//        long itemIdAtPos = stickyList.getItemIdAtPosition(2);
//        Log.d(TAG, "stickyList.getItemIdAtPosition(2) = " + itemIdAtPos);
//        stickyList.getItemAtPosition(1).getClass();
////        Log.d(TAG, "getItemAtPosition instance of = " + stickyList.getAdapter().getView());
    }

    @Override
    public void onLoaderReset(Loader loader) {
        stickyList.setAdapter(null);
    }

    @Subscribe
    public void onEvent(NewCallEvent event) {
        Log.d("onEvent", "Event received. Restarting loader");
        getLoaderManager().restartLoader(0, null, this);
    }
}
