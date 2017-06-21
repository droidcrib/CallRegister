package com.blogspot.droidcrib.callregister.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.model.NoteRecord;

import java.text.SimpleDateFormat;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by BulanovA on 21.06.2017.
 */

public class NotesListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private List<NoteRecord> mRecordsList;
    private LayoutInflater inflater;
    private Context mContext;

    public NotesListAdapter(Context context, List<NoteRecord> list) {
        mContext = context;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRecordsList = list;
    }

    @Override
    public int getCount() {
        return mRecordsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mRecordsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mRecordsList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NotesListAdapter.ViewHolder holder;

        if (convertView == null) {
            holder = new NotesListAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.listitem_notes, parent, false);
            holder.memo =(TextView) convertView.findViewById(R.id.id_tv_note);
            convertView.setTag(holder);

        } else {
            holder = (NotesListAdapter.ViewHolder) convertView.getTag();
        }

        NoteRecord record = (NoteRecord)getItem(position);
        holder.memo.setText(record.memoText);

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        NotesListAdapter.HeaderViewHolder holder;
        if (convertView == null) {
            holder = new NotesListAdapter.HeaderViewHolder();
            convertView = inflater.inflate(R.layout.calls_list_header, parent, false);
            holder.headerNoteDate = (TextView) convertView.findViewById(R.id.id_text_view_separator_date);
            convertView.setTag(holder);
        } else {
            holder = (NotesListAdapter.HeaderViewHolder) convertView.getTag();
        }
        NoteRecord record = (NoteRecord)getItem(position);
        String convertedDate = new SimpleDateFormat("dd MMM yyyy").format(record.noteDateInMillis);
        holder.headerNoteDate.setText(convertedDate);

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        //return the first character of the country as ID because this is what headers are based upon
        NoteRecord record = (NoteRecord)getItem(position);
        String convertedDate = new SimpleDateFormat("dd MMM yyyy").format(record.noteDateInMillis);
        return (long)convertedDate.hashCode();
    }

    class HeaderViewHolder {
        TextView headerNoteDate;
    }

    class ViewHolder {
        TextView memo;
    }

}