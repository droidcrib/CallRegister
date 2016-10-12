package com.blogspot.droidcrib.callregister.loaders;

import android.content.Context;

import com.blogspot.droidcrib.callregister.model.CallRecord;

import java.util.List;

public class CallRecordsLoader extends DatabaseLoader {

    public CallRecordsLoader(Context context) {
        super(context);
    }

    @Override
    public List<CallRecord> loadList() {
        return CallRecord.queryAll();
    }
}
