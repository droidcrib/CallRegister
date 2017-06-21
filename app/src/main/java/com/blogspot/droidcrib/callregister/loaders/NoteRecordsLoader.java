package com.blogspot.droidcrib.callregister.loaders;

import android.content.Context;

import com.blogspot.droidcrib.callregister.model.AlarmRecord;
import com.blogspot.droidcrib.callregister.model.NoteRecord;

import java.util.List;

/**
 * Created by BulanovA on 21.06.2017.
 */

public class NoteRecordsLoader extends DatabaseLoader {

    public NoteRecordsLoader(Context context) {
        super(context);
    }

    @Override
    public List<NoteRecord> loadList() {
        return NoteRecord.queryAll();
    }
}