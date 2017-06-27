package com.blogspot.droidcrib.callregister.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.blogspot.droidcrib.callregister.model.CallRecord;

import java.util.List;


public abstract class DatabaseLoader extends AsyncTaskLoader<List<?>> {

    private List<?> mList;

    public abstract List<?> loadList();

    @Override
    public List<?> loadInBackground() {
        List<?> list = loadList();
        if (!list.isEmpty()) {
            // check content window is filled
            list.size();
        }
        return list;
    }

    public DatabaseLoader(Context context) {
        super(context);
    }

    @Override
    public void deliverResult(List<?> data) {
        super.deliverResult(data);
        mList = data;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (mList != null) {
            deliverResult(mList);
        }
        if (takeContentChanged() || mList == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
    }


}
