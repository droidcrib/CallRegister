package com.blogspot.droidcrib.callregister.ui.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.blogspot.droidcrib.callregister.eventbus.NewCallEvent;
import com.blogspot.droidcrib.callregister.loaders.CallRecordsLoader;
import com.blogspot.droidcrib.callregister.model.CallRecord;
import com.blogspot.droidcrib.callregister.ui.activities.MainActivity;
import com.blogspot.droidcrib.callregister.ui.activities.SingleFragmentActivity;
import com.blogspot.droidcrib.callregister.ui.adapters.CallsListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.blogspot.droidcrib.callregister.contract.Constants.EXTRA_CALL_RECORD_ID;

/**
 * Created by Andrey Bulanov on 04.10.2016.
 */
public class CallsListFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    public static CallsListFragment sCallsListFragment;
    private List<CallRecord> mCallRecordsList;
    private StickyListHeadersListView stickyList;
    private long mRecordId;
    private Parcelable state;

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

        // List items long click processing
        stickyList.setOnCreateContextMenuListener(this);

        getLoaderManager().restartLoader(0, null, this);

        // List items click processing
        stickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
                intent.putExtra(EXTRA_CALL_RECORD_ID, id);
                getActivity().startActivity(intent);


            }
        });
    }


    @Override
    public void onPause() {
        // Save ListView state @ onPause
        state = stickyList.onSaveInstanceState();
        super.onPause();
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
        getActivity().getMenuInflater().inflate(R.menu.item_list_menu_context_calls, menu);
        // Get long-pressed item id
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        mRecordId = info.id;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.context_menu_item_delete_call:
                new RemoveRecordTask().execute(mRecordId);
                getLoaderManager().restartLoader(0, null, this);
                return true;
        }
        return super.onContextItemSelected(item);
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CallRecordsLoader(getActivity());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onLoadFinished(Loader loader, Object data) {
        mCallRecordsList = (List<CallRecord>) data;
        CallsListAdapter adapter = new CallsListAdapter(getActivity(), mCallRecordsList);
        stickyList.setAdapter(adapter);
        // Restore previous state (including selected item index and scroll position)
        if (state != null) {
            stickyList.onRestoreInstanceState(state);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        stickyList.setAdapter(null);
    }

    @Subscribe
    public void onEvent(NewCallEvent event) {
        getLoaderManager().restartLoader(0, null, this);
    }

    /**
     * Removes record from database
     */
    private class RemoveRecordTask extends AsyncTask<Long, Void, Void> {

        protected Void doInBackground(Long... args) {
            CallRecord.deleteRecordById(args[0]);
            return null;
        }

        protected void onPostExecute(Void result) {
            getLoaderManager().restartLoader(0, null, CallsListFragment.this);
        }
    }
}
