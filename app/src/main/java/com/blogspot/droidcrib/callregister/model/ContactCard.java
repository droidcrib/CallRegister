package com.blogspot.droidcrib.callregister.model;

import android.graphics.Bitmap;


public class ContactCard {

    public ContactCard() {
        super();
    }

    public ContactCard(String name) {
        super();
        this.mName = name;
    }

    private String mName;
    private Bitmap mAavatar;
    private String mAvatarUri;

    public String getAvatarUri() {
        return mAvatarUri;
    }

    public void setAvatarUri(String mAvatarUri) {
        this.mAvatarUri = mAvatarUri;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public Bitmap getAavatar() {
        return mAavatar;
    }

    public void setAavatar(Bitmap mAavatar) {
        this.mAavatar = mAavatar;
    }
}
