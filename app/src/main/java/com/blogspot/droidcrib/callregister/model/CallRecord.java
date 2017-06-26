package com.blogspot.droidcrib.callregister.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
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

    @Column(name = "avatarUri")
    public String avatarUri;

    @Column(name = "callType")
    public String callType;

    @Column(name = "callStartTime")
    public Date callStartTime;

    @Column(name = "callDateInMillis")
    public long callDateInMillis;

    public CallRecord() {
        super();
    }

    // add new record
    public static long insert(String contactName, String contactPhone, String avatarUri,
                              String callType, Date callTime) {

        CallRecord record = new CallRecord();
        record.name = contactName;
        record.phone = contactPhone;
        record.avatarUri = avatarUri;
        record.callType = callType;
        record.callStartTime = callTime;
        record.callDateInMillis = dateToLong(callTime);
        record.save();

        return record.getId();
    }

    // query all records
    public static List<CallRecord> queryAll() {

        return new Select()
                .from(CallRecord.class)
                .orderBy("callStartTime DESC")
                .execute();
    }

//    public static void updateMemo(long id, String memoShort){
//        CallRecord record = CallRecord.load(CallRecord.class, id);
//        record.memoText = memoShort;
//        record.save();
//    }

    public static CallRecord getRecordById(long id){
        return new Select()
                .from(CallRecord.class)
                .where("_id = ?", id)
                .executeSingle();
    }

    // remove record from table
    public static void deleteRecordById(long recordId) {
        new Delete()
                .from(CallRecord.class)
                .where("_id = ?", recordId)
                .execute();
    }


    // Used to return items from another table based on the foreign key
    public List<NoteRecord> getNotes() {
        return getMany(NoteRecord.class, "callRecord");
    }

    public List<AlarmRecord> getAlarms() {
        return getMany(AlarmRecord.class, "callRecord");
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
