package com.example.moneymanager.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

@Dao
public interface ReminderDAO {

    @Query("SELECT * FROM Reminder")
    Reminder get();

    @Insert
    void add(Reminder reminder);

    @Update
    void edit(Reminder reminder);
}
