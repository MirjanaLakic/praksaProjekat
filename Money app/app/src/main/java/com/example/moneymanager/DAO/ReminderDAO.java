package com.example.moneymanager.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

@Dao
public interface ReminderDAO {

    @Insert
    void add(Reminder reminder);
}
