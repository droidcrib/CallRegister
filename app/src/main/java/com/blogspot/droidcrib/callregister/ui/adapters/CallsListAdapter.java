package com.blogspot.droidcrib.callregister.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.contract.Constants;
import com.blogspot.droidcrib.callregister.model.CallRecord;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Andrey on 10.10.2016.
 */
public class CallsListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private List<CallRecord> mRecordsList;
    private LayoutInflater inflater;
    private Context mContext;

    public CallsListAdapter(Context context, List<CallRecord> list) {
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
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.listitem_calls, parent, false);
            holder.personName = (TextView) convertView.findViewById(R.id.id_tv_alarm_date);
            holder.callTime =(TextView) convertView.findViewById(R.id.id_tv_note_short);
            holder.comment = (ImageView) convertView.findViewById(R.id.id_image_view_comment);
            holder.callType = (ImageView) convertView.findViewById(R.id.id_image_view_call_type);
            holder.avatar = (SimpleDraweeView ) convertView.findViewById(R.id.id_alarm_image);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.comment.setVisibility(View.INVISIBLE);
        }

        CallRecord record = (CallRecord)getItem(position);

        holder.personName.setText(record.name);
        // Set call time
        String convertedTime = new SimpleDateFormat("HH:mm").format(record.callStartTime);
        holder.callTime.setText(convertedTime);
        // Set comment icon
        if (record.getNotes().size() > 0) {
            holder.comment.setVisibility(View.VISIBLE);
            holder.comment.setImageResource(R.drawable.ic_comment_black_48dp);
        } else if (record.getAlarms().size() > 0) {
            holder.comment.setVisibility(View.VISIBLE);
            holder.comment.setImageResource(R.drawable.ic_alarm_black_48dp);
        } else {
            holder.comment.setVisibility(View.INVISIBLE);
        }
        // Set avatar
        if(record.avatarUri != null){
            holder.avatar.setImageURI(Uri.parse(record.avatarUri));
            holder.avatar.setAlpha(1f);
        } else {
            holder.avatar.setImageResource(R.drawable.ic_account_circle_black_48dp);

        }


        // Set call type icon
        //setup call type icon basing on record.callType value
        switch (record.callType) {
            case Constants.INCOMING_CALL:
                holder.callType.setImageResource(R.drawable.ic_call_received_black_48dp);
                break;
            case Constants.OUTGOING_CALL:
                holder.callType.setImageResource(R.drawable.ic_call_made_black_48dp);
                break;
            case Constants.MISSED_CALL:
                holder.callType.setImageResource(R.drawable.ic_call_missed_black_48dp);
                break;
        }

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.calls_list_header, parent, false);
            holder.callDate = (TextView) convertView.findViewById(R.id.id_text_view_separator_date);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        CallRecord record = (CallRecord)getItem(position);
        String convertedDate = new SimpleDateFormat("dd MMM yyyy").format(record.callStartTime);
        holder.callDate.setText(convertedDate);

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        CallRecord record = (CallRecord)getItem(position);
        return record.callDateInMillis;
    }

    class HeaderViewHolder {
        TextView callDate;
    }

    class ViewHolder {
        TextView personName;
        TextView callTime;
        ImageView comment;
        ImageView callType;
        SimpleDraweeView avatar;
    }

}
