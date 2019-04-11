package com.example.moneymanager.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

@Dao
public interface TimeStampDAO {

    @Query("SELECT * FROM TimeStamp")
    TimeStamp getCategoryTime();

    @Query("SELECT * FROM TimeStamp WHERE id =:id")
    TimeStamp getCategoryTimeId(int id);

    @Insert
    void addTimeStamp(TimeStamp timeStamp);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void edit(TimeStamp item);

    @Delete
    void delete(TimeStamp timeStamp);
}
