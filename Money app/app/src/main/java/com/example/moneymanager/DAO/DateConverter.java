package com.example.moneymanager.DAO;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class DateConverter {
    @TypeConverter
    public static Date toDate(Long date){
        return date == null ? null : new Date(date);
    }

    @TypeConverter
    public static Long toTimeStamp (Date date){
        return date == null ? null : date.getTime();
    }
}
