package com.blogspot.droidcrib.callregister.eventbus;

/**
 * Created by BulanovA on 12.06.2017.
 */

public class PickerDateChangedEvent {

    private final int year;
    private final int month;
    private final int dayOfMonth;

    public PickerDateChangedEvent(int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }
}
