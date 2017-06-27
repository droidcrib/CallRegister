package com.blogspot.droidcrib.callregister.application;

import com.facebook.drawee.backends.pipeline.Fresco;


public class CallRegisterApplication extends  com.activeandroid.app.Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }

}
