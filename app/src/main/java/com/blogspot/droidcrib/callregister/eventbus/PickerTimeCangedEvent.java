package com.blogspot.droidcrib.callregister.eventbus;


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
