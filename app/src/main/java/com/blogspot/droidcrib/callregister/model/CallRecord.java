package com.blogspot.droidcrib.callregister.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Table(name = "CallRecords", id = "_id")
public class CallRecord extends Model {

    @Column(name = "name")
    public String name;

    @Column(name = "phone")
    public String phone;

    @Column(name = "callType")
    public String callType;

    @Column(name = "callStartTime")
    public Date callStartTime;

    @Column(name = "memoText")
    public String memoText;

    @Column(name = "callDateId")
    public long callDateId;

    // add new record
    public static long insert(String contactName, String contactPhone,
                              String callType, Date callTime) {

        CallRecord record = new CallRecord();
        record.name = contactName;
        record.phone = contactPhone;
        record.callType = callType;
        record.callStartTime = callTime;
        record.memoText = "";
        record.callDateId = dateToLong(callTime);
        record.save();

        return record.getId();
    }

    // query all records
    public static List<CallRecord> queryAll() {

        return new Select()
                .from(CallRecord.class)
                .orderBy("callStartTime ASC")
                .execute();
    }

    public static void updateMemo(long id, String memo){
        CallRecord record = CallRecord.load(CallRecord.class, id);
        record.memoText = memo;
        record.save();
    }

    public static CallRecord getRecordById(long id){
        return new Select()
                .from(CallRecord.class)
                .where("_id = ?", id)
                .executeSingle();
    }




    private static long dateToLong(Date date){
        try {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        String str = sdf.format(date);
        date = sdf.parse(str);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date.getTime();
    }
}
