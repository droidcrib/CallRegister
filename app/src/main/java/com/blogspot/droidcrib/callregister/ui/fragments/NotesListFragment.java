package com.blogspot.droidcrib.callregister.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.blogspot.droidcrib.callregister.R;
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

public class NotesListFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    public static NotesListFragment sNotesListFragment;
    List<NoteRecord> mNoteRecordsList;
    StickyListHeadersListView stickyList;
    private String mToolbarTextHeader;
    private long mRecordId;

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

        // List items long click processing
        stickyList.setOnCreateContextMenuListener(this);

        getLoaderManager().restartLoader(0, null, this);

        // List items click processing
        stickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NotesListAdapter.ViewHolder holder = (NotesListAdapter.ViewHolder) (view.getTag());
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


    //
    // Context menu
    //

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.item_list_menu_context_notes, menu);
        // Get long-pressed item id
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        mRecordId = info.id;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.context_menu_item_delete_note:
                new RemoveRecordTask().execute(mRecordId);
                getLoaderManager().restartLoader(0, null, this);
                return true;
        }
        return super.onContextItemSelected(item);
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
    public void onEvent(NewNoteEvent event) {
        Log.d("onEvent", "NewNoteEvent received. Restarting loader");
        getLoaderManager().restartLoader(0, null, this);
    }

    /**
     * Removes record from database and correspondent data directory from storage
     */
    private class RemoveRecordTask extends AsyncTask<Long, Void, Void> {

        protected Void doInBackground(Long... args) {
            NoteRecord.deleteRecordById(args[0]);
            return null;
        }

        protected void onPostExecute(Void result) {
            getLoaderManager().restartLoader(0, null, NotesListFragment.this);
        }
    }
}