package com.blogspot.droidcrib.callregister.loaders;

import android.content.Context;

import com.blogspot.droidcrib.callregister.model.AlarmRecord;
import com.blogspot.droidcrib.callregister.model.CallRecord;

import java.util.List;

/**
 * Created by BulanovA on 21.06.2017.
 */

public class AlarmRecordsLoader  extends DatabaseLoader {

    public AlarmRecordsLoader(Context context) {
        super(context);
    }

    @Override
    public List<AlarmRecord> loadList() {
        return AlarmRecord.queryAll();
    }
}