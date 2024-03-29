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
import android.widget.TextView;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.eventbus.AlarmsListLoadFinishedEvent;
import com.blogspot.droidcrib.callregister.eventbus.NewCallEvent;
import com.blogspot.droidcrib.callregister.loaders.AlarmRecordsLoader;
import com.blogspot.droidcrib.callregister.model.AlarmRecord;
import com.blogspot.droidcrib.callregister.ui.activities.MainActivity;
import com.blogspot.droidcrib.callregister.ui.activities.NewReminderActivity;
import com.blogspot.droidcrib.callregister.ui.adapters.AlarmsListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.blogspot.droidcrib.callregister.contract.Constants.EXTRA_ALARM_RECORD_ID;

/**
 * Created by BulanovA on 21.06.2017.
 */

public class AlarmsListFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    public static AlarmsListFragment sAlarmsListFragment;
    private List<AlarmRecord> mAlarmRecordsList;
    private StickyListHeadersListView stickyList;
    private long mRecordId;
    private Parcelable state;
    private TextView mEmptyView;

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
        mEmptyView = (TextView) v.findViewById(R.id.empty_message_1);

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
                AlarmsListAdapter.ViewHolder holder = (AlarmsListAdapter.ViewHolder) (view.getTag());
                holder.memoShort.setVisibility(holder.memoShort.isShown() ? View.GONE : View.VISIBLE);
                holder.memo.setVisibility(holder.memo.isShown() ? View.GONE : View.VISIBLE);
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


    public void scrollToListItem(long id) {
        for (int i = 0; i < stickyList.getCount(); i++) {
            if (stickyList.getItemIdAtPosition(i) == id) {
                stickyList.setSelection(i);
                return;
            }
        }
    }


    //
    // Context menu
    //

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.item_list_menu_context_alarms, menu);
        // Get long-pressed item id
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        mRecordId = info.id;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.context_menu_item_delete_alarm:
                new RemoveRecordTask().execute(mRecordId);
                getLoaderManager().restartLoader(0, null, this);
                return true;
            case R.id.context_menu_item_edit_alarm:
                Intent i = new Intent(getActivity(), NewReminderActivity.class);
                i.putExtra(EXTRA_ALARM_RECORD_ID, mRecordId);
                startActivity(i);
                return true;
        }
        return super.onContextItemSelected(item);
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
        stickyList.setEmptyView(mEmptyView);
        // Restore previous state (including selected item index and scroll position)
        if (state != null) {
            stickyList.onRestoreInstanceState(state);
        }

        EventBus.getDefault().post(new AlarmsListLoadFinishedEvent());

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
     * Removes record from database and correspondent data directory from storage
     */
    private class RemoveRecordTask extends AsyncTask<Long, Void, Void> {

        protected Void doInBackground(Long... args) {
            AlarmRecord.deleteRecordById(args[0]);
            return null;
        }

        protected void onPostExecute(Void result) {
            getLoaderManager().restartLoader(0, null, AlarmsListFragment.this);
        }
    }
}
