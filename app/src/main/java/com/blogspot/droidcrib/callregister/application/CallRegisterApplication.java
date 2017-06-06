package com.blogspot.droidcrib.callregister.application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by Andrey on 04.10.2016.
 */
public class CallRegisterApplication extends  com.activeandroid.app.Application{
    // Required by ActiveAndroid ORM

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }

}
