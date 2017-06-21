package com.blogspot.droidcrib.callregister.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
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

    @Column(name = "alarmDateId")
    public long alarmDateId;

    @Column(name = "callRecord", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public CallRecord callRecord;


    public AlarmRecord() {
        super();
    }


    public static long insert(int year, int month, int dayOfMonth, int hourOfDay, int minute, CallRecord callRecord, String memoText) {

        AlarmRecord alarmRecord = new AlarmRecord();
        alarmRecord.year = year;
        alarmRecord.month = month;
        alarmRecord.dayOfMonth = dayOfMonth;
        alarmRecord.hourOfDay = hourOfDay;
        alarmRecord.minute = minute;
        alarmRecord.memoText = memoText;
        alarmRecord.callRecord = callRecord;
        alarmRecord.callRecord.name = callRecord.name;
        alarmRecord.callRecord.phone = callRecord.phone;
        alarmRecord.callRecord.avatarUri = callRecord.avatarUri;
        alarmRecord.alarmDateId = dateToLong(new Date());
        alarmRecord.save();

        return alarmRecord.getId();
    }

    public static AlarmRecord getRecordById(long id) {
        return new Select()
                .from(AlarmRecord.class)
                .where("_id = ?", id)
                .executeSingle();
    }

    // query all records
    public static List<AlarmRecord> queryAll() {

        return new Select()
                .from(AlarmRecord.class)
                .orderBy("_id ASC")
                .execute();
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

    @Override
    public String toString() {
        return "AlarmRecord{" +
                "year=" + year +
                ", month=" + month +
                ", dayOfMonth=" + dayOfMonth +
                ", hourOfDay=" + hourOfDay +
                ", minute=" + minute +
                ", memoText='" + memoText + '\'' +
                ", callRecord=" + callRecord +
                ", callRecord.name=" + callRecord.name +
                ", callRecord.phone=" + callRecord.phone +
//                ", callRecord.memoText=" + callRecord.memoText +
                ", callRecord.avatarUri=" + callRecord.avatarUri +
                '}';
    }
}
