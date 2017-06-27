package com.blogspot.droidcrib.callregister.eventbus;



public class PickerTextChangedEvent {

    private final CharSequence text;

    public PickerTextChangedEvent(CharSequence text) {
        this.text = text;
    }

    public CharSequence getText() {
        return text;
    }
}
