package com.blogspot.droidcrib.callregister.ui.fragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.eventbus.NewNoteEvent;
import com.blogspot.droidcrib.callregister.loaders.NoteRecordsLoader;
import com.blogspot.droidcrib.callregister.model.NoteRecord;
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
    private List<NoteRecord> mNoteRecordsList;
    private StickyListHeadersListView stickyList;
    private long mRecordId;
    private Parcelable state;
    private TextView mEmptyView;
    String memoText;


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

                NotesListAdapter.ViewHolder holder = (NotesListAdapter.ViewHolder) (view.getTag());
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
            case R.id.context_menu_item_edit_note:
                editMemoDialog();
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
        stickyList.setEmptyView(mEmptyView);
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
    public void onEvent(NewNoteEvent event) {
        getLoaderManager().restartLoader(0, null, this);
    }

    /**
     * Removes record from database
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

    public void editMemoDialog() {

        NoteRecord noteRecord = NoteRecord.load(NoteRecord.class, mRecordId);
        memoText = noteRecord.memoText;

        // Messages
        String msg = getString(R.string.new_note);
        String buttonYes = getString(R.string.new_note_done);
        // EditText setup
        final EditText input = new EditText(getActivity());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setLines(5);
        input.setText(memoText);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                memoText = s.toString();
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(input)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        if (memoText != null && memoText.length() > 0) {
                            NoteRecord.update(mRecordId, memoText);
                            EventBus.getDefault().post(new NewNoteEvent());
                        }
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
        // Center button
        final Button positiveButton = alert.getButton(AlertDialog.BUTTON_POSITIVE);
        ViewGroup.LayoutParams positiveButtonLL = positiveButton.getLayoutParams();
        positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
        positiveButton.setLayoutParams(positiveButtonLL);
    }
}