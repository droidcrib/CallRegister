package com.blogspot.droidcrib.callregister.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 *
 */

@Table(name = "NoteRecords", id = "_id")
public class NoteRecord extends Model {

    @Column(name = "memoText")
    public String memoText;

    @Column(name = "callRecord", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public CallRecord callRecord;


    public NoteRecord() {
        super();
    }

    public static long insert(String memoText, CallRecord callRecord){
        NoteRecord noteRecord = new NoteRecord();
        noteRecord.memoText = memoText;
        noteRecord.callRecord = callRecord;
        noteRecord.save();

        return noteRecord.getId();
    }







}