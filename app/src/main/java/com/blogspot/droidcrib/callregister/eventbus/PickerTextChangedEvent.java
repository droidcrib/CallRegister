package com.blogspot.droidcrib.callregister.eventbus;

/**
 * Created by BulanovA on 12.06.2017.
 */

public class PickerTextChangedEvent {

    private final CharSequence text;

    public PickerTextChangedEvent(CharSequence text) {
        this.text = text;
    }

    public CharSequence getText() {
        return text;
    }
}
