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
            convertView = inflater.inflate(R.layout.calls_list_item, parent, false);
            holder.personName = (TextView) convertView.findViewById(R.id.id_text_view_person_name);
            holder.callTime =(TextView) convertView.findViewById(R.id.id_text_view_call_time);
            holder.comment = (ImageView) convertView.findViewById(R.id.id_image_view_comment);
            holder.callType = (ImageView) convertView.findViewById(R.id.id_image_view_call_type);
            holder.avatar = (SimpleDraweeView ) convertView.findViewById(R.id.id_image_view_photo);

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
        if (!record.memoText.equals("")) {
            holder.comment.setVisibility(View.VISIBLE);
        }
        // Set avatar
        if(record.avatarUri != null){
            holder.avatar.setImageURI(Uri.parse(record.avatarUri));
        } else {
            holder.avatar.setImageResource(R.drawable.ic_person_black_48dp);
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
        //set header callDate as first char in name
        //String headerText = "" + countries[position].subSequence(0, 1).charAt(0);
        //holder.callDate.setText(record.name);

        CallRecord record = (CallRecord)getItem(position);
        String convertedDate = new SimpleDateFormat("dd MMM yyyy").format(record.callStartTime);
        holder.callDate.setText(convertedDate);

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        //return the first character of the country as ID because this is what headers are based upon
        CallRecord record = (CallRecord)getItem(position);
//        return countries[position].subSequence(0, 1).charAt(0);
        return record.callDateId;
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
