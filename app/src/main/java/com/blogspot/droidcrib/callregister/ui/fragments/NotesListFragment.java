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
import com.blogspot.droidcrib.callregister.eventbus.NewCallEvent;
import com.blogspot.droidcrib.callregister.loaders.NoteRecordsLoader;
import com.blogspot.droidcrib.callregister.model.CallRecord;
import com.blogspot.droidcrib.callregister.model.NoteRecord;
import com.blogspot.droidcrib.callregister.ui.activities.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by BulanovA on 21.06.2017.
 */

public class NotesListFragment extends Fragment implements LoaderManager.LoaderCallbacks{

    public static NotesListFragment sNotesListFragment;
    List<NoteRecord> mCallRecordsList;
    StickyListHeadersListView stickyList;
    private String mToolbarTextHeader;

    //
    // Provides instance of NotesListFragment
    //
    public static NotesListFragment getInstance() {

        if (sNotesListFragment == null) {
            sNotesListFragment = new NotesListFragment();
        }
        return sNotesListFragment;
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
//                MainActivity activity = (MainActivity) getActivity();
//                activity.setDetailsFragment(id);
//                Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
//                intent.putExtra(EXTRA_CALL_RECORD_ID, id);
//                getActivity().startActivity(intent);


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
        return new NoteRecordsLoader(getActivity());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onLoadFinished(Loader loader, Object data) {
        mCallRecordsList = (List<NoteRecord>) data;
        NotesListAdapter adapter = new NotesListAdapter(getActivity(), mCallRecordsList);
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
} {
}
