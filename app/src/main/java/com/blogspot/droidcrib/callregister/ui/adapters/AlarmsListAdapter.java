package com.blogspot.droidcrib.callregister.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.model.AlarmRecord;


import java.text.SimpleDateFormat;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 *
 */

public class AlarmsListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private List<AlarmRecord> mRecordsList;
    private LayoutInflater inflater;
    private Context mContext;
    private String header;
    private int headerId;
    private long mCurrentTime = System.currentTimeMillis();

    public AlarmsListAdapter(Context context, List<AlarmRecord> list) {
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
        AlarmsListAdapter.ViewHolder holder;

        if (convertView == null) {
            holder = new AlarmsListAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.listitem_alarms, parent, false);
            holder.alarmDate = (TextView) convertView.findViewById(R.id.id_tv_alarm_date);
            holder.memo = (TextView) convertView.findViewById(R.id.id_tv_alarms_note_short);
            convertView.setTag(holder);

        } else {
            holder = (AlarmsListAdapter.ViewHolder) convertView.getTag();
        }

        AlarmRecord record = (AlarmRecord) getItem(position);
        String convertedTime = new SimpleDateFormat("dd MMM yyyy  HH:mm").format(record.alarmDateInMillis);
        holder.alarmDate.setText(convertedTime);
        holder.memo.setText(record.memoText);

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        AlarmsListAdapter.HeaderViewHolder holder;
        if (convertView == null) {
            holder = new AlarmsListAdapter.HeaderViewHolder();
            convertView = inflater.inflate(R.layout.calls_list_header, parent, false);
            holder.headerAlarmDate = (TextView) convertView.findViewById(R.id.id_text_view_separator_date);
            convertView.setTag(holder);
        } else {
            holder = (AlarmsListAdapter.HeaderViewHolder) convertView.getTag();
        }

        AlarmRecord record = (AlarmRecord) getItem(position);
        if (mCurrentTime < record.alarmDateInMillis){
            header = "Actual";
        } else {
            header = "Outdated";
        }
        holder.headerAlarmDate.setText(header);
//        String convertedDate = new SimpleDateFormat("dd MMM yyyy").format(record.alarmDateInMillis);
//        holder.headerAlarmDate.setText(convertedDate);

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        AlarmRecord record = (AlarmRecord) getItem(position);
        if (mCurrentTime < record.alarmDateInMillis){
            headerId = 1;
        } else {
            headerId = 2;
        }
//        String convertedDate = new SimpleDateFormat("dd MMM yyyy").format(record.alarmDateInMillis);
        return headerId;
    }

    class HeaderViewHolder {
        TextView headerAlarmDate;
    }

    class ViewHolder {
        TextView alarmDate;
        TextView memo;
    }

}