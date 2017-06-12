package com.blogspot.droidcrib.callregister.eventbus;

/**
 * Created by BulanovA on 12.06.2017.
 */

public class PickerTimeCangedEvent {

    private final int hourOfDay;
    private final int minute;

    public PickerTimeCangedEvent(int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public int getMinute() {
        return minute;
    }
}
