package com.blogspot.droidcrib.callregister.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by BulanovA on 09.06.2017.
 */


@Table(name = "AlarmRecords", id = "_id")
public class AlarmRecord extends Model {

    @Column(name = "year")
    public int year;

    @Column(name = "month")
    public int month;

    @Column(name = "dayOfMonth")
    public int dayOfMonth;

    @Column(name = "hourOfDay")
    public int hourOfDay;

    @Column(name = "minute")
    public int minute;

    @Column(name = "memoText")
    public String memoText;

    @Column(name = "callRecord", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public CallRecord callRecord;





}
