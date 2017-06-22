package com.blogspot.droidcrib.callregister.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.eventbus.NewCallEvent;
import com.blogspot.droidcrib.callregister.eventbus.NewNoteEvent;
import com.blogspot.droidcrib.callregister.loaders.NoteRecordsLoader;
import com.blogspot.droidcrib.callregister.model.NoteRecord;
import com.blogspot.droidcrib.callregister.ui.activities.MainActivity;
import com.blogspot.droidcrib.callregister.ui.adapters.NotesListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 *
 */

public class NotesListFragment extends Fragment implements LoaderManager.LoaderCallbacks{

    public static NotesListFragment sNotesListFragment;
    List<NoteRecord> mNoteRecordsList;
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

        getLoaderManager().restartLoader(0, null, this);

        // List items click processing
        stickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NotesListAdapter.ViewHolder holder = (NotesListAdapter.ViewHolder)(view.getTag());
                holder.memoShort.setVisibility(holder.memoShort.isShown() ? View.GONE  : View.VISIBLE);
                holder.memo.setVisibility(holder.memo.isShown() ? View.GONE  : View.VISIBLE);
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
        mNoteRecordsList = (List<NoteRecord>) data;
        NotesListAdapter adapter = new NotesListAdapter(getActivity(), mNoteRecordsList);
        stickyList.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        stickyList.setAdapter(null);
    }

    @Subscribe
    public void onEvent(NewNoteEvent event){
        Log.d("onEvent", "NewNoteEvent received. Restarting loader");
        getLoaderManager().restartLoader(0, null, this);
    }
}