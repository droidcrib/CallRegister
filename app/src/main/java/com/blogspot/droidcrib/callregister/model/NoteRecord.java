package com.blogspot.droidcrib.callregister.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.Calendar;
import java.util.List;

/**
 *
 */

@Table(name = "NoteRecords", id = "_id")
public class NoteRecord extends Model {

    @Column(name = "memoText")
    public String memoText;

    @Column(name = "noteDateInMillis")
    public long noteDateInMillis;

    @Column(name = "callRecord", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public CallRecord callRecord;


    public NoteRecord() {
        super();
    }

    public static long insert(String memoText, CallRecord callRecord) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        NoteRecord noteRecord = new NoteRecord();
        noteRecord.memoText = memoText;
        noteRecord.callRecord = callRecord;
        noteRecord.noteDateInMillis = calendar.getTimeInMillis();
        noteRecord.save();
        return noteRecord.getId();
    }


    // query all records
    public static List<NoteRecord> queryAll() {
        return new Select()
                .from(NoteRecord.class)
                .orderBy("noteDateInMillis DESC")
                .execute();
    }


}
